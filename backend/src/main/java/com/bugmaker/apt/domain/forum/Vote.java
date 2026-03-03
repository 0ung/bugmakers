package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

/**
 * 투표 마스터 테이블
 */
@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("투표 마스터 테이블")
//테스트
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("투표 ID")
    private Long id;

    @Column(nullable = false, length = 100)
    @Comment("투표 항목명")
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
    @Comment("토론 ID")
    private Forum forum;

    public static Vote create(String name) {
        Vote vote = new Vote();

        vote.name = name;

        return vote;
    }

    public void setForum(Forum forum) {
        this.forum = forum;
    }
}
