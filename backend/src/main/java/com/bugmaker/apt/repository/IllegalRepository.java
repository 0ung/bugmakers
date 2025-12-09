package com.bugmaker.apt.repository;

import com.bugmaker.apt.constants.IllegalStatus;
import com.bugmaker.apt.domain.common.Illegal;
import org.springframework.data.repository.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IllegalRepository extends Repository<Illegal, Long> {

    Illegal save(Illegal illegal);

    Optional<Illegal> findById(Long id);

    // 특정 회원이 신고당한 내역 조회
    List<Illegal> findByMemberId(Long memberId);

    // 특정 상태의 신고 내역 조회
    List<Illegal> findByIllegalStatus(IllegalStatus status);

    // 등록된 지 일정 시간 지난 신고 조회 (REGISTERED → PENDING 변경용)
    List<Illegal> findByIllegalStatusAndCreatedDateBefore(IllegalStatus status, LocalDateTime dateTime);

    // 특정 회원의 승인된 신고 건수 (정지 판단용)
    long countByMemberIdAndIllegalStatus(Long memberId, IllegalStatus status);

    // 뉴스 신고 조회
    List<Illegal> findByNewsIdIsNotNull();

    // 토론 신고 조회
    List<Illegal> findByForumIdIsNotNull();
}
