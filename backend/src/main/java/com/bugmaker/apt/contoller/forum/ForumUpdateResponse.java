package com.bugmaker.apt.contoller.forum;

import com.bugmaker.apt.domain.forum.Forum;

public record ForumUpdateResponse(Long forumId, String title) {
    public static ForumUpdateResponse of(Forum forum) {
        return new ForumUpdateResponse(forum.getId(), forum.getTitle());
    }
}
