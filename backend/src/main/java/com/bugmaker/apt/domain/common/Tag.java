package com.bugmaker.apt.domain.common;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "tag")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("태그 마스터 테이블")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("태그 ID")
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @Comment("태그명")
    private String name;
}
