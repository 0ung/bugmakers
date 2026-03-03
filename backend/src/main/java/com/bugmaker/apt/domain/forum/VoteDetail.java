package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
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
public class VoteDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vote_id", nullable = false)
    private Vote vote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    public static VoteDetail create(Vote vote, Member member) {
        VoteDetail voteDetail = new VoteDetail();
        voteDetail.vote = vote;
        voteDetail.member = member;
        return voteDetail;
    }
}
