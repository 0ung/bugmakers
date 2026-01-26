package com.bugmaker.apt.repository.forum;

import com.bugmaker.apt.domain.tag.Tag;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends Repository<Tag, Long> {
    Tag save(Tag tag);

    Optional<Tag> findById(Long tagId);

    List<Tag> findByName(String name);
}
