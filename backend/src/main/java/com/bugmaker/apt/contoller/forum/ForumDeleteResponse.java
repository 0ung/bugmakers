package com.bugmaker.apt.contoller.forum;

import com.bugmaker.apt.domain.forum.Forum;

public record ForumDeleteResponse(Long forumId) {
    public static ForumDeleteResponse of(Forum forum) {
        return new ForumDeleteResponse(forum.getId());
    }
}
