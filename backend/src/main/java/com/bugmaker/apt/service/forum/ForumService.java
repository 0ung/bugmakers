package com.bugmaker.apt.service.forum;

import com.bugmaker.apt.common.exception.errorcode.ErrorCode;
import com.bugmaker.apt.common.exception.custom.CustomException;
import com.bugmaker.apt.common.exception.custom.IncludeImproperWordsException;
import com.bugmaker.apt.domain.forum.*;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.common.SliceResponse;
import com.bugmaker.apt.dto.forum.ForumDetailResponse;
import com.bugmaker.apt.dto.forum.ForumListResponse;
import com.bugmaker.apt.dto.forum.VoteResponse;
import com.bugmaker.apt.dto.forum.VoteSummaryResponse;
import com.bugmaker.apt.repository.forum.ForumRepository;
import com.bugmaker.apt.repository.forum.VoteDetailRepository;
import com.bugmaker.apt.repository.forum.VoteRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@Validated
@RequiredArgsConstructor
public class ForumService {
    private final ForumRepository forumRepository;
    private final VoteRepository voteRepository;
    private final VoteDetailRepository voteDetailRepository;

    public SliceResponse<ForumListResponse> forumList(Pageable pageable) {
        Slice<Forum> forums = forumRepository.findForumAll(pageable);

        List<Long> forumIds = forums.stream().map(Forum::getId).toList();

        Map<Long, List<Object[]>> voteCountsByForum = forumIds.isEmpty()
                ? Map.of()
                : voteDetailRepository.findVoteCountsByForumIds(forumIds).stream()
                        .collect(Collectors.groupingBy(row -> (Long) row[0]));

        Slice<ForumListResponse> responses = forums.map(forum -> {
            List<Object[]> forumVotes = voteCountsByForum.getOrDefault(forum.getId(), List.of());

            List<VoteSummaryResponse> topVotes = forumVotes.stream()
                    .sorted(Comparator.comparingLong((Object[] row) -> (Long) row[2]).reversed())
                    .limit(2)
                    .map(row -> new VoteSummaryResponse((String) row[1], (Long) row[2]))
                    .toList();

            long totalVoteCount = forumVotes.stream().mapToLong(row -> (Long) row[2]).sum();

            return new ForumListResponse(
                    forum.getId(),
                    forum.getTitle(),
                    forum.getContent(),
                    forum.getMember().getNickname(),
                    forum.getViewCount(),
                    forum.getLikeCount(),
                    forum.getReportCount(),
                    forum.getCreatedDate(),
                    forum.getLastModifiedDate(),
                    topVotes,
                    totalVoteCount
            );
        });

        return new SliceResponse<>(responses);
    }

    @Transactional
    public ForumDetailResponse findDetail(Long forumId, Member member) {
        Forum forum = forumRepository.findDetailById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("해당 포럼이 존재하지 않습니다."));

        forum.increaseViewCount();

        return buildForumDetailResponse(forum, member);
    }

    @Transactional
    public ForumDetailResponse submitVote(Long forumId, VoteSubmitRequest request, Member member) {
        Forum forum = forumRepository.findDetailById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("해당 포럼이 존재하지 않습니다."));

        // 마감 체크
        if (!forum.isVoteOpen()) {
            throw new CustomException(ErrorCode.VOTE_CLOSED);
        }

        // 투표 항목이 해당 포럼에 속하는지 확인
        Vote vote = forum.getVoteList().stream()
                .filter(v -> v.getId().equals(request.voteId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("해당 투표 항목이 존재하지 않습니다."));

        // 기존 투표 확인 (중복 투표 데이터 방어 포함)
        List<VoteDetail> existingVotes = voteDetailRepository.findAllByForumIdAndMemberId(forumId, member.getId());

        if (!existingVotes.isEmpty()) {
            // 같은 항목 재클릭이고 중복 데이터가 없으면 현재 상태 그대로 반환
            if (existingVotes.size() == 1 && existingVotes.get(0).getVote().getId().equals(request.voteId())) {
                return buildForumDetailResponse(forum, member);
            }
            // 기존 투표 모두 삭제 (중복 데이터 정리 포함)
            voteDetailRepository.deleteAll(existingVotes);
            voteDetailRepository.flush();
        }

        voteDetailRepository.save(VoteDetail.create(vote, member));

        return buildForumDetailResponse(forum, member);
    }

    private ForumDetailResponse buildForumDetailResponse(Forum forum, Member member) {
        long totalVoteCount = voteDetailRepository.countByForumId(forum.getId());

        List<VoteResponse> voteDtos = forum.getVoteList().stream()
                .map(vote -> {
                    long count = voteDetailRepository.countByVoteId(vote.getId());
                    double percentage = totalVoteCount > 0 ? (double) count / totalVoteCount * 100 : 0;
                    return VoteResponse.of(vote, count, percentage);
                })
                .toList();

        // 로그인 사용자의 투표 항목 확인
        Long myVoteId = null;
        if (member != null) {
            List<VoteDetail> myVotes = voteDetailRepository.findAllByForumIdAndMemberId(forum.getId(), member.getId());
            if (!myVotes.isEmpty()) {
                myVoteId = myVotes.get(0).getVote().getId();
            }
        }

        return new ForumDetailResponse(
                forum.getId(),
                forum.getTitle(),
                forum.getContent(),
                forum.getMember().getNickname(),
                forum.getViewCount(),
                forum.getLikeCount(),
                forum.getCreatedDate(),
                forum.getLastModifiedDate(),
                voteDtos,
                myVoteId,
                totalVoteCount,
                forum.getVoteDeadline(),
                forum.isVoteOpen()
        );
    }

    public Forum postUp(ForumCreateRequest createRequest, Member member) {
        checkCurse(createRequest.title(), createRequest.content());

        Forum forum  = Forum.postUp(createRequest, member);

        createRequest.voteList()
                .forEach(vote -> forum.addVoteContent(Vote.create(vote.name())));

        return forumRepository.save(forum);
    }

    public Forum update(ForumUpdateRequest updateRequest, Member member) {
        checkCurse(updateRequest.title(), updateRequest.content());

        Forum forum = forumRepository.findById(updateRequest.forumId()).orElseThrow();

        if(!forum.getMember().equals(member)) {
            throw new IllegalArgumentException("본인이 작성하지 않은 게시글은 수정할 수 없습니다");
        }

        forum.update(updateRequest);

        return forumRepository.save(forum);
    }

    public Forum delete(ForumDeleteRequest deleteRequest, Member member) {
        Forum forum = forumRepository.findById(deleteRequest.forumId()).orElseThrow(() -> new EntityNotFoundException("해당 포럼이 존재하지 않습니다."));

        if (!forum.getMember().equals(member)) {
            throw new IllegalArgumentException("본인이 작성하지 않은 게시글은 삭제할 수 없습니다");
        }

        forum.delete();

        return forumRepository.save(forum);
    }

    private void checkCurse(String title, String content) {
        // FIXME: 부절절한 단어 필터링하는 api 등 기능 넣을지 고민
        if (title.contains("멍청이")) {
            throw new IncludeImproperWordsException(ErrorCode.INCLUDE_IMPROPER_WORDS);
        }
    }

}
