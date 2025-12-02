package com.bugmaker.apt.domain.member;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class NicknameCreatorTest {
    @Autowired
    NicknameCreator nicknameCreator;

    @Test
    void generateNickname() {
        String generate = nicknameCreator.generate();

        assertThat(generate).isNotNull();
    }
}