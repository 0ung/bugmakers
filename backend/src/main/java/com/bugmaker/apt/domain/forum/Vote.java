package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("투표 마스터 테이블")
public class Vote extends BaseEntity {
  
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forum_id", nullable = false)
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
