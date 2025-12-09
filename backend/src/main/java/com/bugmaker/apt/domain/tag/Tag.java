package com.bugmaker.apt.domain.tag;

import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Tag extends BaseEntity {

    private String name;

    public static Tag createTag(String tagName) {
        Tag tag = new Tag();

        tag.name = tagName;

        return tag;
    }

}
