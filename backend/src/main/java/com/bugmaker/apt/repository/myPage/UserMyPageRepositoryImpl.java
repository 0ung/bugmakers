package com.bugmaker.apt.repository.myPage;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * UserMyPageRepositoryImpl - UserMyPageRepository 구현체
 * Native Query를 사용한 집계/통계 쿼리 구현
 */
@Repository
public class UserMyPageRepositoryImpl implements UserMyPageRepository {

    @PersistenceContext
    private EntityManager entityManager;

    // 좋아요 관련
    @Override
    public Long countLikesOnMyForums(Long memberId) {
        String sql = """
            SELECT COUNT(l.id)
            FROM liked l
            INNER JOIN forum f ON l.forum_id = f.id
            WHERE f.member_id = :memberId
            AND l.forum_id IS NOT NULL
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("memberId", memberId);
        
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findWhoLikedMyForums(Long memberId) {
        String sql = """
            SELECT 
                l.member_id as memberId,
                m.nickname,
                f.id as forumId,
                f.title as forumTitle,
                l.created_date as likedAt
            FROM liked l
            INNER JOIN forum f ON l.forum_id = f.id
            INNER JOIN member m ON l.member_id = m.id
            WHERE f.member_id = :memberId
            AND l.forum_id IS NOT NULL
            ORDER BY l.created_date DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("memberId", memberId);
        
        return query.getResultList();
    }

    @Override
    public Long countLikesOnMyComments(Long memberId) {
        String sql = """
            SELECT COUNT(l.id)
            FROM liked l
            INNER JOIN commented c ON l.comment_id = c.id
            WHERE c.member_id = :memberId
            AND l.comment_id IS NOT NULL
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("memberId", memberId);
        
        Object result = query.getSingleResult();
        return result != null ? ((Number) result).longValue() : 0L;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findWhoLikedMyComments(Long memberId) {
        String sql = """
            SELECT 
                l.member_id as memberId,
                m.nickname,
                c.id as commentId,
                l.created_date as likedAt
            FROM liked l
            INNER JOIN commented c ON l.comment_id = c.id
            INNER JOIN member m ON l.member_id = m.id
            WHERE c.member_id = :memberId
            AND l.comment_id IS NOT NULL
            ORDER BY l.created_date DESC
            """;
        
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("memberId", memberId);
        
        return query.getResultList();
    }
}
