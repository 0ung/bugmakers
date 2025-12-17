package com.bugmaker.apt.repository;

import com.bugmaker.apt.domain.tag.Tag;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
class TagRepositoryTest {
    @Autowired
    TagRepository tagRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void createTag() {
        Tag tag1 = Tag.createTag("경기");
        assertThat(tag1.getId()).isNull();

        tagRepository.save(tag1);

        entityManager.flush();
        entityManager.clear();

        Tag foundTag1 = tagRepository.findById(tag1.getId()).orElseThrow();

        assertThat(foundTag1.getName()).isEqualTo(tag1.getName());
    }
}