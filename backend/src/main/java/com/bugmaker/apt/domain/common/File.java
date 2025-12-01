package com.bugmaker.apt.domain.common;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("파일 마스터 테이블")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("파일 ID")
    private Long id;

    @Column(nullable = false, length = 255)
    @Comment("파일명")
    private String name;

    @Column(nullable = false, length = 20)
    @Comment("확장자")
    private String extension;
}
