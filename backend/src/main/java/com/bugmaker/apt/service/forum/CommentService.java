package com.bugmaker.apt.service.forum;

import com.bugmaker.apt.domain.forum.Comment;
import com.bugmaker.apt.domain.forum.CommentCreateRequest;
import com.bugmaker.apt.domain.forum.CommentLike;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.dto.forum.CommentResponse;
import com.bugmaker.apt.repository.forum.CommentLikeRepository;
import com.bugmaker.apt.repository.forum.CommentRepository;
import com.bugmaker.apt.repository.forum.ForumRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final ForumRepository forumRepository;

    @Transactional(readOnly = true)
    public List<CommentResponse> getComments(Long forumId, Member member) {
        List<Comment> all = commentRepository.findByForumId(forumId);

        if (all.isEmpty()) return List.of();

        Set<Long> likedCommentIds = new HashSet<>();
        if (member != null) {
            List<Long> commentIds = all.stream().map(Comment::getId).toList();
            likedCommentIds.addAll(commentLikeRepository.findLikedCommentIdsByMemberId(commentIds, member.getId()));
        }

        // parentId 기준으로 대댓글 그룹핑
        Map<Long, List<Comment>> repliesByParentId = all.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Comment::getParentId));

        final Set<Long> finalLikedIds = likedCommentIds;

        // 최상위 댓글만 필터링해서 반환 (대댓글은 replies에 포함)
        return all.stream()
                .filter(c -> c.getParentId() == null)
                .map(c -> toResponse(c, repliesByParentId, finalLikedIds))
                .toList();
    }

    public CommentResponse create(Long forumId, CommentCreateRequest request, Member member) {
        Forum forum = forumRepository.findById(forumId)
                .orElseThrow(() -> new EntityNotFoundException("해당 포럼이 존재하지 않습니다."));

        Comment parent = null;
        if (request.parentId() != null) {
            parent = commentRepository.findById(request.parentId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));
            if (parent.getParentId() != null) {
                throw new IllegalArgumentException("대댓글에는 답글을 달 수 없습니다.");
            }
        }

        Comment comment = Comment.create(request.content(), forum, member, parent);
        Comment saved = commentRepository.save(comment);

        return new CommentResponse(
                saved.getId(),
                saved.getParentId(),
                saved.getContent(),
                member.getNickname(),
                0L,
                false,
                saved.getCreatedDate(),
                List.of()
        );
    }

    public CommentResponse toggleLike(Long commentId, Member member) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("해당 댓글이 존재하지 않습니다."));

        Optional<CommentLike> existing = commentLikeRepository.findByCommentIdAndMemberId(commentId, member.getId());

        if (existing.isPresent()) {
            commentLikeRepository.delete(existing.get());
            comment.decreaseLikeCount();
        } else {
            commentLikeRepository.save(CommentLike.create(comment, member));
            comment.increaseLikeCount();
        }

        return new CommentResponse(
                comment.getId(),
                comment.getParentId(),
                comment.getContent(),
                comment.getMember().getNickname(),
                comment.getLikeCount(),
                existing.isEmpty(),
                comment.getCreatedDate(),
                List.of()
        );
    }

    private CommentResponse toResponse(Comment c, Map<Long, List<Comment>> repliesByParentId, Set<Long> likedIds) {
        List<CommentResponse> replies = repliesByParentId
                .getOrDefault(c.getId(), List.of())
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedDate))
                .map(r -> toResponse(r, Map.of(), likedIds))
                .toList();

        return new CommentResponse(
                c.getId(),
                c.getParentId(),
                c.getContent(),
                c.getMember().getNickname(),
                c.getLikeCount(),
                likedIds.contains(c.getId()),
                c.getCreatedDate(),
                replies
        );
    }
}
