package com.bugmaker.apt.domain.member;


import com.bugmaker.apt.domain.AbstractEntity;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = PROTECTED)
public class MemberDetail extends AbstractEntity {
    private Nickname nickname;

    private String introduction;

    private LocalDateTime registeredAt;

    private LocalDateTime updatedAt;

    public static MemberDetail create() {
        MemberDetail memberDetail = new MemberDetail();
        memberDetail.registeredAt = LocalDateTime.now();

        return memberDetail;
    }

    public void updateInfo(MemberInfoUpdateRequest updateRequest) {
        this.nickname = new Nickname(updateRequest.nickname());
        this.introduction = updateRequest.introduction();

        this.updatedAt = LocalDateTime.now();
    }
}
