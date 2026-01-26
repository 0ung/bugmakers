package com.bugmaker.apt.domain.common;

import com.bugmaker.apt.domain.shared.BaseEntity;
import com.bugmaker.apt.enums.member.Status;
import com.bugmaker.apt.enums.menu.MenuLevel;
import com.bugmaker.apt.enums.menu.MenuType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "menu")
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("메뉴 테이블")
public class Menu extends BaseEntity {

    @Column(nullable = false, length = 50)
    @Comment("메뉴명")
    private String name;

    @Column(nullable = false, length = 50)
    @Comment("메뉴 타입")
    private MenuType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    @Comment("메뉴 레벨")
    private MenuLevel level; //MenuLevel.displayName 값 사용

    @Column(nullable = false)
    @Comment("메뉴 순서")
    private Integer seq;

    @Comment("상위 메뉴 ID")
    private Long parentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Comment("상태")
    @Builder.Default
    private Status status = Status.ACTIVE;

}
