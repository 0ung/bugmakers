package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.enums.Status;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "menu")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("메뉴 마스터 테이블")
public class Menu {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("메뉴 ID")
    private Long id;

    @Column(nullable = false, length = 50)
    @Comment("메뉴명")
    private String name;

    @Column(nullable = false, length = 50)
    @Comment("메뉴 타입")
    private String type;

    @Column(length = 500)
    @Comment("설명")
    private String description;

    @Column(nullable = false)
    @Comment("메뉴 순서") // 0: home, 1: news/forum/trends, 2-4: 지역 계층
    private Integer seq;

    @Comment("상위 메뉴")
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("상태")
    @Builder.Default
    private Status status = Status.ACTIVE;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @Comment("생성일")
    private LocalDateTime createdDate;

    @LastModifiedDate
    @Comment("수정일")
    private LocalDateTime lastModifiedDate;
}
