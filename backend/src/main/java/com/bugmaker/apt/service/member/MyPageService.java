package com.bugmaker.apt.service.member;

import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.member.MyPageForumResponse;
import com.bugmaker.apt.domain.member.MyPageProfileResponse;
import com.bugmaker.apt.domain.member.MyPageProfileUpdateRequest;
import com.bugmaker.apt.repository.ForumRepository;
import com.bugmaker.apt.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final ForumRepository forumRepository;

    /**
     * 프로필 정보 조회
     * @param member 현재 로그인한 회원
     * @return 프로필 정보
     */
    public MyPageProfileResponse getProfile(Member member) {
        log.info("프로필 조회 - MemberId: {}, Nickname: {}", member.getId(), member.getNickname());
        return MyPageProfileResponse.of(member);
    }

    /**
     * 프로필 정보 수정 (닉네임)
     * @param member 현재 로그인한 회원
     * @param updateRequest 수정 요청 데이터
     * @return 수정된 프로필 정보
     */
    @Transactional
    public MyPageProfileResponse updateProfile(Member member, MyPageProfileUpdateRequest updateRequest) {
        String oldNickname = member.getNickname();
        member.updateNickname(updateRequest.nickname());

        log.info("프로필 수정 완료 - MemberId: {}, Old: {}, New: {}", 
                member.getId(), oldNickname, member.getNickname());

        return MyPageProfileResponse.of(member);
    }

    /**
     * 작성한 게시글 조회
     * @param member 현재 로그인한 회원
     * @param pageable 페이징 정보
     * @return 작성한 게시글 목록
     */
    public Page<MyPageForumResponse> getMyForums(Member member, Pageable pageable) {
        log.info("내가 작성한 게시글 조회 - MemberId: {}", member.getId());
        
        Page<Forum> forums = forumRepository.findByMemberAndStatus(
                member, Status.ACTIVE, pageable);
        
        return forums.map(MyPageForumResponse::of);
    }

    /**
     * 작성한 게시글 통계
     * @param member 현재 로그인한 회원
     * @return 게시글 총 개수
     */
    public long getMyForumsCount(Member member) {
        return forumRepository.countByMemberAndStatus(member, Status.ACTIVE);
    }
}
