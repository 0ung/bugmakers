package com.bugmaker.apt.repository;

import com.bugmaker.apt.constants.Status;
import com.bugmaker.apt.domain.forum.Forum;
import com.bugmaker.apt.domain.member.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface ForumRepository extends Repository<Forum, Long> {
    Forum save(Forum forum);

    Optional<Forum> findById(Long id);

    List<Forum> findAll();

    Page<Forum> findByTitleContaining(String title, Pageable pageable);

    //todo : forum만 완성 되어서 일단 이렇게 하고 나중에는 type=comments, type=liked 와 같이 통합으로 myPage api진행 하도록
    // MyPage 전용: 내가 작성한 포럼 목록
    Page<Forum> findByMemberAndStatus(Member member, Status status, Pageable pageable);

    //todo : 위와 동일
    // MyPage 전용: 내가 작성한 포럼 개수
    long countByMemberAndStatus(Member member, Status status);
}
