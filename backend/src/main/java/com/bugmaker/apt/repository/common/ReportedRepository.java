package com.bugmaker.apt.repository.common;

import com.bugmaker.apt.domain.common.Reported;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.enums.report.ReportStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * ReportedRepository - "내가 무엇을 신고했는가" + 관리자용
 */
public interface ReportedRepository extends JpaRepository<Reported, Long> {

    /* 기본 CRUD */
    Optional<Reported> findByReporter_IdAndForum_Id(Long reporterId, Long forumId);
    Optional<Reported> findByReporter_IdAndCommented_Id(Long reporterId, Long commentId);
    Optional<Reported> findByReporter_IdAndTargetMember_Id(Long reporterId, Long targetMemberId);

    boolean existsByReporter_IdAndForum_Id(Long reporterId, Long forumId);
    boolean existsByReporter_IdAndCommented_Id(Long reporterId, Long commentId);
    boolean existsByReporter_IdAndTargetMember_Id(Long reporterId, Long targetMemberId);


    /* 마이페이지(USER) */
    // 내가 신고한 개수
    @Query("""
        SELECT COUNT(r)
        FROM Reported r
        WHERE r.reporter.id = :reporterId
    """)
    Long countMyReports(@Param("reporterId") Long reporterId);

    // 내가 신고한 목록
    @Query("""
        SELECT r
        FROM Reported r
        WHERE r.reporter.id = :reporterId
        ORDER BY r.createdDate DESC
    """)
    Page<Reported> findByReporter_Id(
            @Param("reporterId") Long reporterId,
            Pageable pageable
    );


    /* 관리자 페이지 */
    Page<Reported> findByStatus(
            ReportStatus status,
            Pageable pageable
    );

    @Query("""
        SELECT COUNT(r)
        FROM Reported r
        WHERE r.status = :status
    """)
    Long countByStatus(@Param("status") ReportStatus status);


    /* 특정 회원이 받은 신고 수 */
    @Query("""
        SELECT COUNT(r)
        FROM Reported r
        WHERE r.targetMember = :member
           OR r.forum.member = :member
           OR r.commented.member = :member
    """)
    Long countReportsAgainstMember(@Param("member") Member member);
}
