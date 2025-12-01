package com.bugmaker.apt.domain.news;

import com.bugmaker.apt.domain.common.Tag;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Table(name = "news_tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("뉴스 게시글에 달린 tag을 저장하는 테이블")
@IdClass(NewsTag.NewsTagId.class)
public class NewsTag {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "news_id")
    @Comment("뉴스 ID")
    private News news;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    @Comment("태그 ID")
    private Tag tag;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class NewsTagId implements Serializable {
        private Long news;
        private Long tag;
    }
}
