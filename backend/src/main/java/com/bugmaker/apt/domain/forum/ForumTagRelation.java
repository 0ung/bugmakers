package com.bugmaker.apt.domain.forum;


import com.bugmaker.apt.domain.shared.BaseEntity;
import com.bugmaker.apt.domain.tag.Tag;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class ForumTagRelation extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "forum_id")
    private Forum forum;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "tag_id")
    private Tag tag;

    public static ForumTagRelation relation(ForumTagRelationRequest relationRequest) {
        ForumTagRelation forumTagRelation = new ForumTagRelation();

        forumTagRelation.forum = relationRequest.forum();
        forumTagRelation.tag = relationRequest.tag();

        return forumTagRelation;
    }

}
