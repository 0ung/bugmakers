package com.bugmaker.apt.domain.forum;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vote")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Vote {
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
