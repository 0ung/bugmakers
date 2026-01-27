/**
 * 뉴스 기본 이미지 관리
 * 
 * DB에 이미지가 없을 경우 카테고리별로 기본 이미지 제공
 */

import type { NewsCategory } from '../types/news';

// 카테고리별 기본 이미지 풀
const DEFAULT_IMAGES: Record<NewsCategory, string[]> = {
  // 부동산, 시장, 정책, 일반 → 주택/아파트 이미지
  REAL_ESTATE: [
    "https://images.unsplash.com/photo-1599809275695-5e96ca83bc43?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1628624747186-a941c476b7ef?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80",
    "https://images.unsplash.com/photo-1689574120966-c7b1e57a8cfe?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1727327572051-bbbb5501bfe2?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1572162407527-3adf1a248a3a?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  MARKET: [
    "https://images.unsplash.com/photo-1599809275695-5e96ca83bc43?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1628624747186-a941c476b7ef?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80",
    "https://images.unsplash.com/photo-1689574120966-c7b1e57a8cfe?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1727327572051-bbbb5501bfe2?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1572162407527-3adf1a248a3a?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  POLICY: [
    "https://images.unsplash.com/photo-1599809275695-5e96ca83bc43?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1628624747186-a941c476b7ef?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80",
    "https://images.unsplash.com/photo-1689574120966-c7b1e57a8cfe?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1727327572051-bbbb5501bfe2?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1572162407527-3adf1a248a3a?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  // 금융, 세금 → 금융/세금 이미지
  FINANCE: [
    "https://images.unsplash.com/photo-1690117670229-a114a6dd4ade?q=80&w=627&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1676983351979-ebf75cb0899a?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1707623988408-ab88c9981730?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1661771900806-b7002b408f54?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1562564055-71e051d33c19?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  TAX: [
    "https://images.unsplash.com/photo-1690117670229-a114a6dd4ade?q=80&w=627&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1676983351979-ebf75cb0899a?q=80&w=1160&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1707623988408-ab88c9981730?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://plus.unsplash.com/premium_photo-1661771900806-b7002b408f54?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1562564055-71e051d33c19?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  // 인프라 → 교통/인프라 이미지
  INFRA: [
    "https://images.unsplash.com/photo-1767260913949-fd2c6f6a8b95?q=80&w=774&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1449824913935-59a10b8d2000?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ],
  
  GENERAL: [
    "https://images.unsplash.com/photo-1599809275695-5e96ca83bc43?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1628624747186-a941c476b7ef?q=80&w=1740&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1560518883-ce09059eeffa?auto=format&fit=crop&w=800&q=80",
    "https://images.unsplash.com/photo-1689574120966-c7b1e57a8cfe?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1727327572051-bbbb5501bfe2?q=80&w=2148&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
    "https://images.unsplash.com/photo-1572162407527-3adf1a248a3a?q=80&w=1470&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
  ]
};

// 특수 케이스: 집값하락/상승
const HOUSE_PRICE_DOWN = "https://plus.unsplash.com/premium_photo-1682310015260-7313c699657d?q=80&w=1812&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";
const HOUSE_PRICE_UP = "https://plus.unsplash.com/premium_photo-1682309743711-a8f84f47fef6?q=80&w=1812&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D";

/**
 * 뉴스 이미지 가져오기
 * 
 * 우선순위:
 * 1. DB 이미지 (imageUrl)
 * 2. 특수 케이스 (집값하락/상승)
 * 3. 카테고리별 랜덤 이미지
 */
export function getNewsImage(
  category: NewsCategory,
  title: string,
  content: string,
  imageUrl?: string | null,
  newsId?: number
): string {
  // 1순위: DB 이미지
  if (imageUrl) {
    return imageUrl;
  }
  
  // 2순위: 특수 케이스 (집값하락/상승 - REAL_ESTATE만)
  if (category === 'REAL_ESTATE') {
    const text = (title + ' ' + content).toLowerCase();
    
    if (text.includes('집값하락') || text.includes('집값 하락')) {
      return HOUSE_PRICE_DOWN;
    }
    
    if (text.includes('집값상승') || text.includes('집값 상승')) {
      return HOUSE_PRICE_UP;
    }
  }
  
  // 3순위: 카테고리별 랜덤 이미지
  const images = DEFAULT_IMAGES[category] || DEFAULT_IMAGES.GENERAL;
  
  // newsId 기반 rotation (같은 뉴스는 항상 같은 이미지)
  if (newsId !== undefined) {
    const index = newsId % images.length;
    return images[index];
  }
  
  // newsId 없으면 랜덤
  const randomIndex = Math.floor(Math.random() * images.length);
  return images[randomIndex];
}
