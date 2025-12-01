package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.common.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table(name = "forum_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("포럼 게시글에 달린 tag을 저장하는 테이블")
public class ForumTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id")
    @Comment("포럼 ID")
    private Forum forum;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    @Comment("태그 ID")
    private Tag tag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ForumTagId implements Serializable {
        private Long forum;
        private Long tag;
    }
}
