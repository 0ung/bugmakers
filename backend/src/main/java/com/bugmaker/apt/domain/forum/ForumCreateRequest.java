package com.bugmaker.apt.domain.forum;

import java.util.List;

public record ForumCreateRequest(
        String title,
        String content,
        List<VoteCreateRequest> voteList
        ) {
}
