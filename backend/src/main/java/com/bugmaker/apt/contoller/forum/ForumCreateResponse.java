package com.bugmaker.apt.contoller.forum;

import com.bugmaker.apt.domain.forum.Forum;

public record ForumCreateResponse(Long forumId, String title) {
    public static ForumCreateResponse of(Forum forum) {
        return new ForumCreateResponse(forum.getId(), forum.getTitle());
    }
}
