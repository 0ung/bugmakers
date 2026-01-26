package com.bugmaker.apt.domain.trend;

import com.bugmaker.apt.domain.shared.BaseEntity;
import com.bugmaker.apt.enums.RegionLevel;
import jakarta.persistence.Entity;
import lombok.*;
import jakarta.persistence.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

import static io.jsonwebtoken.lang.Assert.state;

@Entity
@Table(name = "region")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Region extends BaseEntity {

    // 전국 / 서울 / 강남구 / 개포동
    @Column(nullable = false)
    private String name;

    // LV1, LV2, LV3, LV4
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegionLevel level;

    // 상위 지역 (self join)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Region parent;

    // 하위 지역
    @OneToMany(mappedBy = "parent")
    @Builder.Default
    private List<Region> children = new ArrayList<>();

    // 정렬용 (서울, 경기, 인천 순서 등)
    private Integer sortOrder;

    @Column(nullable = false)
    @Comment("조회수")
    @Builder.Default
    private Long viewCount = 0L;

    @Column(nullable = false)
    @Comment("좋아요 누적수")
    @Builder.Default
    private Long likeCount = 0L;

    @Column(nullable = false)
    @Comment("즐겨찾기 누적수")
    @Builder.Default
    private Long favoritedCount = 0L;

    @Column(nullable = false)
    @Comment("공유 누적수")
    @Builder.Default
    private Long shareCount = 0L;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseLikeCount() {
        this.likeCount++;
    }

    public void decreaseLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void increaseFavoritedCount() { this.favoritedCount++; }

    public void decreaseFavoritedCount() {
        state(this.favoritedCount > 0, "즐겨찾기는 음수가 될 수 없습니다.");
        this.favoritedCount--;
    }

    public void increaseShareCount() { this.shareCount++; }
}
