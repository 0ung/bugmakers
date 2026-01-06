package com.bugmaker.apt.domain.member;

import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.forum.Forum;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Builder
public record MyPageForumResponse(
        Long forumId,
        String title,
        String content,
        Long viewCount,
        Long heartCount,
        Status status,
        LocalDateTime createdDate,
        List<String> tags
) {
    public static MyPageForumResponse of(Forum forum) {
        return MyPageForumResponse.builder()
                .forumId(forum.getId())
                .title(forum.getTitle())
                .content(forum.getContent())
                .viewCount(forum.getViewCount())
                .heartCount(forum.getHeartCount())
                .status(forum.getStatus())
                .createdDate(forum.getCreatedDate())
                .tags(forum.getForumTagRelationList().stream()
                        .map(relation -> relation.getTag().getName())
                        .toList())
                .build();
    }

    public static List<MyPageForumResponse> ofList(List<Forum> forums) {
        return forums.stream()
                .map(MyPageForumResponse::of)
                .toList();
    }
}
