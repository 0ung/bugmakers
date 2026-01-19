package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.common.Favorited;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoritedRepository extends JpaRepository<Favorited, Long> {

    /** 회원 ID와 뉴스 ID로 즐겨찾기 조회 */
    Optional<Favorited> findByMemberIdAndNewsId(Long memberId, Long newsId);

    /** 회원 ID와 뉴스 ID로 즐겨찾기 존재 여부 확인 */
    boolean existsByMemberIdAndNewsId(Long memberId, Long newsId);

    /** 회원 ID와 뉴스 ID로 즐겨찾기 삭제 */
    void deleteByMemberIdAndNewsId(Long memberId, Long newsId);

    /** 회원 ID와 토론 ID로 즐겨찾기 조회 */
    Optional<Favorited> findByMemberIdAndForumId(Long memberId, Long forumId);

    /** 회원 ID와 토론 ID로 즐겨찾기 존재 여부 확인 */
    boolean existsByMemberIdAndForumId(Long memberId, Long forumId);

    /** 회원 ID와 토론 ID로 즐겨찾기 삭제 */
    void deleteByMemberIdAndForumId(Long memberId, Long forumId);
}
