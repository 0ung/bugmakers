package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "vote_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Comment("투표한 회원 정보 테이블")
public class VoteDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("투표 상세 ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    @Comment("투표 항목 ID")
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    @Comment("회원 ID")
    private Member member;
}
