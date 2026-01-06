package com.bugmaker.apt.domain.forum;

import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.Assert.state;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@ToString
public class Forum extends BaseEntity {
    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @OneToMany(mappedBy = "forum")
    private List<ForumTagRelation> forumTagRelationList = new ArrayList<>();

    private Long viewCount;

    private Long heartCount;

    private Long reportCount;

    @Enumerated(value = STRING)
    private Status status;

    private LocalDateTime deletedDate;

    public static Forum postUp(ForumCreateRequest createRequest, Member member) {
        Forum forum = new Forum();

        forum.title = createRequest.title();
        forum.content = createRequest.content();
        forum.status = Status.ACTIVE;
        forum.member = member;

        forum.viewCount = 0L;
        forum.heartCount = 0L;
        forum.reportCount = 0L;

        return forum;
    }

    public void update(ForumUpdateRequest updateRequest) {
        this.title = updateRequest.title();
        this.content = updateRequest.content();
    }

    public void delete() {
        state(status == Status.ACTIVE, "이미 삭제된 게시글은 삭제할 수 없습니다.");

        this.status = Status.DEACTIVE;
        this.deletedDate = LocalDateTime.now();
    }

    public boolean isActive() {
        return this.status == Status.ACTIVE;
    }

    public void addForumTagRelation(ForumTagRelation forumTagRelation) {
        this.forumTagRelationList.add(forumTagRelation);
    }

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void increaseHeartCount() {
        this.heartCount++;
    }

    public void decreaseHeartCount() {
        state(this.heartCount > 0 ,"좋아요 누적수는 마이너스가 될 수 없습니다.");
        if (this.heartCount > 0) {
            this.heartCount--;
        }
    }

    public void increaseReportCount() {
        this.reportCount++;
    }

}
