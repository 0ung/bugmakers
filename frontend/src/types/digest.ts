// MCP Digest API 타입 정의

export interface NewsItem {
  id: number;
  title: string;
  content: string;
  reference: string;
}

export interface PendingNewsResponse {
  digestDate: string;  // "2026-02-07"
  category: NewsCategory;
  newsCount: number;
  newsList: NewsItem[];
}

export interface DigestResponse {
  id: number;
  digestDate: string;  // "2026-02-07"
  category: NewsCategory;
  summary: string;
  newsIds: number[];
  newsCount: number;
  createdAt: string;
}

export interface DigestCreateRequest {
  digestDate: string;
  category: NewsCategory;
  summary: string;
  newsIds: number[];
}

export type NewsCategory = 
  | 'REAL_ESTATE'
  | 'MARKET'
  | 'POLICY'
  | 'FINANCE'
  | 'TAX'
  | 'INFRA'
  | 'GENERAL';

export const NewsCategoryLabel: Record<NewsCategory, string> = {
  REAL_ESTATE: '부동산',
  MARKET: '시장',
  POLICY: '정책',
  FINANCE: '금융',
  TAX: '세금',
  INFRA: '인프라',
  GENERAL: '일반',
};
