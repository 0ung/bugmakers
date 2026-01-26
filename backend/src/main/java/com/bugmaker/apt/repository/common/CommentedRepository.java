package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.enums.menu.MenuLevel;
import com.bugmaker.apt.domain.common.Commented;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CommentedRepository extends JpaRepository<Commented, Long> {

    /* 포럼 전체 댓글 */
    List<Commented> findByForum_Id(Long forumId);

    /* 댓글 / 대댓글 공용 */
    List<Commented> findByForum_IdAndSeq(Long forumId, MenuLevel seq);

    /* 작성자 기준 */
    List<Commented> findByMember_Id(Long memberId);
    List<Commented> findByMember_IdAndForum_Id(Long memberId, Long forumId);

    /* 단건 / 검증 */
    Optional<Commented> findByIdAndForum_Id(Long commentedId, Long forumId);
    boolean existsByIdAndForum_Id(Long commentedId, Long forumId);

    /* 관리 */
    long countByForum_Id(Long forumId);
    long countByMember_Id(Long memberId);
    void deleteByIdAndMember_Id(Long commentedId, Long memberId);
}
