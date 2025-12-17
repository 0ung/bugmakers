package com.bugmaker.apt.domain.tag;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TagTest {

    @Test
    void createTag() {
        Tag tag = Tag.createTag("경기");
        Tag tag2 = Tag.createTag("서울");
        Tag tag3 = Tag.createTag("인천");

        assertThat(tag.getName()).isEqualTo("경기");
        assertThat(tag2.getName()).isEqualTo("서울");
        assertThat(tag3.getName()).isEqualTo("인천");
    }

    @Test
    void tagFixture() {
        Tag tag = TagFixture.createTag();
        Tag customTag = TagFixture.createTag("인천");

        assertThat(tag.getName()).isEqualTo("기본태그");
        assertThat(customTag.getName()).isEqualTo("인천");
    }

}
