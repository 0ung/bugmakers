package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.forum.Forum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ForumRepository extends Repository<Forum, Long> {
    Optional<Forum> findById(Long id);

    Page<Forum> findByTitleContaining(String title, Pageable pageable);
}
