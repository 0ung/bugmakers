/**
 * 뉴스 관련 타입 정의
 */

export type NewsCategory = 'REAL_ESTATE' | 'MARKET' | 'POLICY' | 'FINANCE' | 'TAX' | 'INFRA' | 'GENERAL';

// 뉴스 아이템 (목록용 - API 응답)
export interface NewsItem {
    id: number;
    title: string;
    category: NewsCategory;     // 영문 (로직용)
    displayName: string;        // 한글 (화면 표시용)
    reference: string;
    thumbnailUrl?: string;
    viewCount: number;
    likeCount: number;
    shareCount: number;
    createdDate: string;
}

// 뉴스 UI 아이템 (화면 표시용)
export interface NewsUIItem {
    id: number;
    category: string;        // 한글 (화면 표시용)
    title: string;
    description: string;
    date: string;
    image: string;
}

// 뉴스 상세 (상세 페이지용)
export interface NewsDetail extends NewsItem {
    content: string;
    detailImageUrl?: string;
    reportCount: number;
    lastModifiedDate: string;
}

// 뉴스 목록 응답 (페이징)
export interface NewsPageResponse {
    content: NewsItem[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

// 뉴스 생성 요청 (크롤러용)
export interface NewsCreateRequest {
    title: string;
    content: string;
    reference: string;
}
