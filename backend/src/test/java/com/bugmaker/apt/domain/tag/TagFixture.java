package com.bugmaker.apt.domain.tag;

public class TagFixture {
    public static Tag createTag(String tagName) {
        return Tag.createTag(tagName);
    }

    public static Tag createTag() {
        return createTag("기본태그");
    }
}
