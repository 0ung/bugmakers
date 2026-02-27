package com.bugmaker.apt.dto.forum;

import com.bugmaker.apt.domain.forum.Vote;

public record VoteResponse(Long voteId, String name, long count, double percentage) {
    public static VoteResponse of(Vote vote, long count, double percentage) {
        return new VoteResponse(vote.getId(), vote.getName(), count, Math.round(percentage * 10) / 10.0);
    }
}
