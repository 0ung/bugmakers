package com.bugmaker.apt.domain.forum;

public class ForumFixture {

    public static ForumCreateRequest forumCreateRequest(String title, String content) {
        return new ForumCreateRequest(title, content);
    }

    public static ForumCreateRequest forumCreateRequest() {
        return forumCreateRequest("기본제목", "기본내용");
    }


}
