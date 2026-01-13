/**
 * 뉴스 관련 타입 정의
 */

// 뉴스 아이템 (목록용 - API 응답)
export interface NewsItem {
    id: number;
    title: string;
    reference: string;
    viewCount: number;
    heartCount: number;
    createdDate: string;
}

// 뉴스 UI 아이템 (화면 표시용)
export interface NewsUIItem {
    id: number;
    category: string;
    title: string;
    description: string;
    date: string;
    image: string;
}

// 뉴스 상세 (상세 페이지용)
export interface NewsDetail extends NewsItem {
    content: string;
    reportCount: number;
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