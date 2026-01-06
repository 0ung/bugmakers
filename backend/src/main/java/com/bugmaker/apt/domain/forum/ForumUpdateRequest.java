package com.bugmaker.apt.domain.forum;

public record ForumUpdateRequest(Long forumId, String title, String content) {
}
