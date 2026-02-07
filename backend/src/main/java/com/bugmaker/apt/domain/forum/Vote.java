package com.bugmaker.apt.domain.forum;

import jakarta.persistence.*;
import lombok.*;

/**
 * 투표 마스터 테이블
 */
@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Vote {
    /**
     * 투표 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 투표 항목명
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * 토론 ID
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;
}
