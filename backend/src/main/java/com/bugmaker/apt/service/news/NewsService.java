package com.bugmaker.apt.service.news;

import com.bugmaker.apt.common.exception.custom.CustomException;
import com.bugmaker.apt.common.exception.errorcode.ErrorCode;
import com.bugmaker.apt.domain.common.Favorited;
import com.bugmaker.apt.domain.common.Liked;
import com.bugmaker.apt.domain.member.Member;
import com.bugmaker.apt.domain.news.News;
import com.bugmaker.apt.domain.news.NewsCreateRequest;
import com.bugmaker.apt.domain.news.NewsDetailResponse;
import com.bugmaker.apt.domain.news.NewsResponse;
import com.bugmaker.apt.enums.NewsCategory;
import com.bugmaker.apt.repository.common.FavoritedRepository;
import com.bugmaker.apt.repository.common.LikedRepository;
import com.bugmaker.apt.repository.member.MemberRepository;
import com.bugmaker.apt.repository.news.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 뉴스 서비스
 * RSS 크롤링 데이터 관리 및 뉴스 CRUD
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NewsService {

    private final MemberRepository memberRepository;
    private final NewsRepository newsRepository;
    private final LikedRepository likedRepository;
    private final FavoritedRepository favoritedRepository;

    /**
     * 뉴스 등록 (Python 크롤러용)
     * 중복 체크: 제목과 출처 모두 확인
     * Idempotent: 중복된 뉴스는 기존 데이터 반환 (예외 없음)
     * 
     * @param request 뉴스 생성 요청
     * @return 생성된 또는 기존 뉴스 응답
     * @throws IllegalArgumentException 필수 값이 없는 경우
     */
    @Transactional
    public NewsResponse createNews(NewsCreateRequest request) {
        // 1. Validation: 필수 값 체크
        validateNewsRequest(request);
        
        log.info("뉴스 등록 시도 - 제목: {}", request.title());

        // 2. 중복 체크: 제목으로 기존 뉴스 확인
        Optional<News> existingByTitle = newsRepository.findByTitle(request.title());
        if (existingByTitle.isPresent()) {
            News existing = existingByTitle.get();
            log.warn("⚠️  중복 뉴스 (제목) - ID: {}, 제목: {}", existing.getId(), existing.getTitle());
            return NewsResponse.from(existing);
        }

        // 3. 중복 체크: 출처로 기존 뉴스 확인
        Optional<News> existingByReference = newsRepository.findByReference(request.reference());
        if (existingByReference.isPresent()) {
            News existing = existingByReference.get();
            log.warn("⚠️  중복 뉴스 (출처) - ID: {}, 출처: {}", existing.getId(), existing.getReference());
            return NewsResponse.from(existing);
        }

        // 4. 새 뉴스 저장 (카테고리 자동 추출됨)
        News news = request.toEntity();
        News savedNews = newsRepository.save(news);

        log.info("✅ 뉴스 등록 성공 - ID: {}, 제목: {}, 카테고리: {}",
                savedNews.getId(), savedNews.getTitle(), savedNews.getCategory());
        return NewsResponse.from(savedNews);
    }

    /**
     * 뉴스 등록 요청 검증
     * 
     * @param request 뉴스 생성 요청
     * @throws IllegalArgumentException 필수 값이 없는 경우
     */
    private void validateNewsRequest(NewsCreateRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("뉴스 등록 요청이 null입니다.");
        }
        
        if (request.title() == null || request.title().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 제목은 필수입니다.");
        }
        
        if (request.content() == null || request.content().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 내용은 필수입니다.");
        }
        
        if (request.reference() == null || request.reference().trim().isEmpty()) {
            throw new IllegalArgumentException("뉴스 출처는 필수입니다.");
        }
    }

    /**
     * 뉴스 목록 조회 (페이징)
     * 최신순 정렬
     * 
     * @param pageable 페이징 정보
     * @return 뉴스 목록
     */
    public Page<NewsResponse> getNewsList(Pageable pageable) {
        log.info("뉴스 목록 조회 - 페이지: {}, 사이즈: {}", pageable.getPageNumber(), pageable.getPageSize());
        
        Page<News> newsPage = newsRepository.findAll(pageable);
        
        log.info("뉴스 목록 조회 완료 - 총 {}건", newsPage.getTotalElements());
        return newsPage.map(NewsResponse::from);
    }

    /** 카테고리 + 검색어로 뉴스 조회 (페이징) */
    public Page<NewsResponse> searchNews(String categoryName, String keyword, Pageable pageable) {
        log.info("뉴스 검색 - 카테고리: {}, 검색어: {}, 페이지: {}", categoryName, keyword, pageable.getPageNumber());

        Page<News> newsPage;

        // displayName을 Enum으로 변환
        NewsCategory category = categoryName != null ? NewsCategory.fromDisplayName(categoryName) : null;

        boolean hasCategory = category != null && category != NewsCategory.GENERAL;
        boolean hasKeyword = keyword != null && !keyword.trim().isEmpty();

        if (!hasKeyword && !hasCategory) {
            // 전체 조회
            newsPage = newsRepository.findAll(pageable);
        } else if (!hasKeyword) {
            // 카테고리만
            newsPage = newsRepository.findByCategory(category, pageable);
        } else if (!hasCategory) {
            // 검색어만
            newsPage = newsRepository.findByKeyword(keyword, pageable);
        } else {
            // 카테고리 + 검색어
            newsPage = newsRepository.findByCategoryAndKeyword(category, keyword, pageable);
        }

        log.info("뉴스 검색 완료 - 카테고리: {}, 검색어: {}, 총 {}건", category, keyword, newsPage.getTotalElements());
        return newsPage.map(NewsResponse::from);
    }

    /** 모든 카테고리 목록 조회 (Enum 기반) */
    public List<String> getAllCategories() {
        // NewsCategory Enum의 모든 displayName 반환
        return NewsCategory.getAllDisplayNames();
    }

    /** DB에 실제로 뉴스가 있는 카테고리 목록 조회 */
    public List<String> getExistingCategories() {
        // DB에서 실제로 사용 중인 카테고리만 조회
        List<NewsCategory> categories = newsRepository.findDistinctCategories();
        return categories.stream()
                .map(NewsCategory::getDisplayName)
                .toList();
    }

    /**
     * 뉴스 상세 조회
     * 조회수 증가는 별도 메서드로 분리
     * 
     * @param newsId 뉴스 ID
     * @return 뉴스 상세 정보
     * @throws CustomException 뉴스를 찾을 수 없는 경우
     */
    public NewsDetailResponse getNewsDetail(Long newsId) {
        log.info("뉴스 상세 조회 - ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        return NewsDetailResponse.from(news);
    }

    /**
     * 조회수 증가
     * 
     * @param newsId 뉴스 ID
     * @throws CustomException 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void increaseViewCount(Long newsId) {
        log.info("조회수 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        news.increaseViewCount();
        // JPA 더티 체킹으로 자동 업데이트
    }

    /**
     * 공유 수 증가
     * 
     * @param newsId 뉴스 ID
     * @throws CustomException 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void increaseShareCount(Long newsId) {
        log.info("공유 수 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        news.increaseShareCount();
        log.info("✅ 공유 수 증가 완료 - 뉴스 ID: {}, 현재 공유 수: {}", newsId, news.getShareCount());
    }

    /**
     * 좋아요 증가 (Like Count 증가)
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @throws CustomException 이미 좋아요를 누른 경우 또는 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void increaseLikeCount(Long memberId, Long newsId) {
        // 중복 체크
        if (likedRepository.existsByMember_IdAndNews_Id(memberId, newsId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        likedRepository.save(Liked.forNews(member, news));
        news.increaseLikeCount();
        
        log.info("✅ 좋아요 추가 - 회원 ID: {}, 뉴스 ID: {}", memberId, newsId);
    }

    /**
     * 좋아요 취소 (Like Count 감소)
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @throws CustomException 좋아요를 누르지 않은 경우 또는 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void decreaseLikeCount(Long memberId, Long newsId) {
        // 좋아요 여부 체크
        if (!likedRepository.existsByMember_IdAndNews_Id(memberId, newsId)) {
            throw new CustomException(ErrorCode.NOT_LIKED_YET);
        }

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        likedRepository.deleteByMember_IdAndNews_Id(memberId, newsId);
        news.decreaseLikeCount();
        
        log.info("✅ 좋아요 취소 - 회원 ID: {}, 뉴스 ID: {}", memberId, newsId);
    }

    /**
     * 좋아요 여부 확인
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @return 좋아요 여부
     */
    public boolean isLikedByMe(Long memberId, Long newsId) {
        return likedRepository.existsByMember_IdAndNews_Id(memberId, newsId);
    }

    /**
     * 즐겨찾기 추가
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @throws CustomException 이미 즐겨찾기한 경우 또는 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void addFavorite(Long memberId, Long newsId) {
        // 중복 체크
        if (favoritedRepository.existsByMember_IdAndNews_Id(memberId, newsId)) {
            throw new CustomException(ErrorCode.ALREADY_FAVORITED);
        }

        // 뉴스 & 멤버 존재 여부 확인
        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        favoritedRepository.save(Favorited.forNews(member, news));
        
        log.info("✅ 즐겨찾기 추가 - 회원 ID: {}, 뉴스 ID: {}", memberId, newsId);
    }

    /**
     * 즐겨찾기 취소
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @throws CustomException 즐겨찾기하지 않은 경우 또는 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void removeFavorite(Long memberId, Long newsId) {
        // 즐겨찾기 여부 체크
        if (!favoritedRepository.existsByMember_IdAndNews_Id(memberId, newsId)) {
            throw new CustomException(ErrorCode.NOT_FAVORITED_YET);
        }

        // 뉴스 존재 여부 확인
        newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        favoritedRepository.deleteByMember_IdAndNews_Id(memberId, newsId);
        
        log.info("✅ 즐겨찾기 취소 - 회원 ID: {}, 뉴스 ID: {}", memberId, newsId);
    }

    /**
     * 즐겨찾기 여부 확인
     * 
     * @param memberId 회원 ID
     * @param newsId 뉴스 ID
     * @return 즐겨찾기 여부
     */
    public boolean isFavoritedByMe(Long memberId, Long newsId) {
        return favoritedRepository.existsByMember_IdAndNews_Id(memberId, newsId);
    }

    /**
     * 신고 증가
     * 
     * @param newsId 뉴스 ID
     * @throws CustomException 뉴스를 찾을 수 없는 경우
     */
    @Transactional
    public void increaseReportCount(Long newsId) {
        log.info("신고 증가 - 뉴스 ID: {}", newsId);

        News news = newsRepository.findById(newsId)
                .orElseThrow(() -> new CustomException(ErrorCode.NEWS_NOT_FOUND));

        news.increaseReportCount();
    }
}
