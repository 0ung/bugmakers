package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.common.Liked;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikedRepository extends JpaRepository<Liked, Long> {

    Optional<Liked> findByMemberIdAndNewsId(Long memberId, Long newsId);

    boolean existsByMemberIdAndNewsId(Long memberId, Long newsId);

    void deleteByMemberIdAndNewsId(Long memberId, Long newsId);
}
