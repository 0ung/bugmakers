package com.bugmaker.apt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;

/**
 * 뉴스 카테고리 Enum
 * 카테고리와 크롤링 키워드 관리 (점수 기반 분류)
 */
@Getter
@RequiredArgsConstructor
public enum NewsCategory {
    REAL_ESTATE("부동산",
            createKeywords(
                    // ⭐ 복합 키워드 (최우선 - 높은 가중치)
                    "아파트분양", 15, "아파트시세", 15, "아파트가격", 15, "아파트매매", 15,
                    "주택분양", 15, "주택시세", 15, "주택가격", 15, "주택매매", 15,
                    "아파트값", 15, "집값상승", 12, "집값하락", 12,
                    
                    // 부동산 거래 (높은 가중치)
                    "분양", 12, "청약", 12, "입주", 10, "입주권", 10,
                    "전세", 12, "월세", 12, "매매", 10, "임대", 9,
                    "재건축", 14, "재개발", 14, "리모델링", 10,
                    
                    // 핵심 부동산 키워드 (중간 가중치 - 복합 키워드와 함께 사용)
                    "부동산", 8, "주택", 6, "주거", 5,
                    "오피스텔", 6, "빌라", 5, "다세대", 5,
                    "주상복합", 8, "단독주택", 6,
                    
                    // 단독 사용 시 낮은 가중치 (위치 표현일 수 있음)
                    "아파트", 5,  // 낮춤 (10 → 5)
                    
                    // 지역
                    "신도시", 4, "택지", 6, "주거지", 5
            )
    ),

    MARKET("시장",
            createKeywords(
                    // 부동산 시장 복합 키워드 (높은 가중치)
                    "부동산시장", 15, "주택시장", 15, "아파트시장", 15,
                    "매매시장", 12, "전세시장", 12, "임대시장", 12,
                    "분양시장", 12, "부동산경기", 12,
                    
                    "미분양", 10, "분양가", 9, "주택보급", 8,
                    
                    // 일반 키워드는 매우 낮은 가중치
                    "시세", 3, "거래량", 3, "공급", 2
            )
    ),

    POLICY("정책",
            createKeywords(
                    // 부동산 정책 복합 키워드 (높은 가중치)
                    "부동산정책", 15, "주택정책", 15, "분양정책", 12,
                    "주택규제", 12, "분양규제", 12, "청약규제", 12,
                    
                    // 부동산 관련 정부 기관
                    "국토교통부", 12, "국토부", 12, "주택도시보증공사", 10,
                    "한국부동산원", 10,
                    
                    // 부동산 규제
                    "투기과열지구", 12, "조정대상지역", 12, "규제지역", 10,
                    "규제완화", 10, "주택공급", 10,
                    
                    // 부동산 가격 공시
                    "공시가격", 10, "공시지가", 10, "기준시가", 8,
                    "임대차보호법", 12, "전월세", 10
            )
    ),

    FINANCE("금융",
            createKeywords(
                    // 부동산 대출 복합 키워드 (높은 가중치)
                    "주택담보대출", 15, "주담대", 15, "주택대출", 15,
                    "전세대출", 15, "전세자금대출", 15,
                    "아파트담보대출", 12, "주택구입자금", 12,
                    "주택금리", 10,
                    
                    // 부동산 대출 규제
                    "LTV", 12, "DTI", 12, "DSR", 12,
                    "총부채원리금상환비율", 10,
                    
                    // 일반 금융 키워드는 매우 낮은 가중치
                    "대출금리", 3, "금리", 2, "기준금리", 3
            )
    ),

    TAX("세금",
            createKeywords(
                    // 부동산 세금 복합 키워드 (높은 가중치)
                    "부동산세", 15, "재산세", 12, "보유세", 12,
                    "취득세", 14, "양도소득세", 15, "양도세", 15,
                    "종합부동산세", 15, "종부세", 15,
                    "1주택", 10, "다주택", 12, "다주택자", 12,
                    
                    // 일반 세금 키워드는 매우 낮은 가중치
                    "세금", 2, "과세", 2
            )
    ),

    INFRA("지역·인프라",
            createKeywords(
                    // 교통 인프라 복합 키워드 (높은 가중치)
                    "GTX", 12, "광역급행철도", 12, "역세권", 12,
                    "신도시개발", 14, "택지개발", 12, "도시개발", 10,
                    "재정비촉진지구", 10, "정비사업", 10,
                    
                    // 일반 교통 키워드는 낮은 가중치
                    "지하철", 4, "전철", 4, "교통망", 4,
                    "지역개발", 5, "균형발전", 4,
                    "산업단지", 3, "혁신도시", 5
            )
    ),

    GENERAL("일반", Map.of());

    private final String displayName;  // 화면 표시명
    private final Map<String, Integer> keywords;  // 키워드 -> 가중치

    // 🎯 필터링 임계값 설정 (원래대로 복원)
    private static final int MINIMUM_SCORE = 8;              // 최소 점수 (복합 키워드 필요)
    private static final int MINIMUM_KEYWORD_MATCHES = 2;     // 최소 매칭 키워드 개수 (2개 필수!)

    /**
     * displayName으로 Enum 찾기
     */
    public static NewsCategory fromDisplayName(String name) {
        if (name == null) {
            return GENERAL;
        }

        return Arrays.stream(values())
                .filter(c -> c.displayName.equals(name))
                .findFirst()
                .orElse(GENERAL);
    }

    /**
     * 제목/내용에서 카테고리 분류 (3단계 필터링 - 제외 키워드 제거)
     * 
     * ⭐ 핵심 개선: 복합 키워드 중심, 단독 키워드 가중치 낮춤
     * 
     * 1단계: 부동산 핵심 키워드 체크 (최소 1개 필수)
     * 2단계: 점수 계산 (복합 키워드 높은 점수)
     * 3단계: 최소 점수 & 최소 2개 키워드 매칭 체크
     */
    public static NewsCategory classify(String title, String content) {
        String titleLower = title.toLowerCase();
        String contentLower = (content != null ? content : "").toLowerCase();
        String text = (title + " " + content).toLowerCase();

        // 🚨 1단계: 부동산 핵심 키워드 체크
        boolean hasRealEstateKeyword = checkRealEstateKeywords(text);
        
        if (!hasRealEstateKeyword) {
            return GENERAL;
        }

        // 📊 2단계: 점수 계산 (제목 3배 가중치!)
        Map<NewsCategory, Integer> scores = new EnumMap<>(NewsCategory.class);
        Map<NewsCategory, Integer> matchCounts = new EnumMap<>(NewsCategory.class);

        for (NewsCategory category : values()) {
            if (category == GENERAL) {
                continue;
            }

            int score = 0;
            int matchCount = 0;
            
            for (Map.Entry<String, Integer> entry : category.keywords.entrySet()) {
                String keyword = entry.getKey().toLowerCase();
                int weight = entry.getValue();

                // 제목에서 키워드 발견 → 가중치 3배! (복합 키워드 우대)
                int titleCount = countOccurrences(titleLower, keyword);
                if (titleCount > 0) {
                    score += titleCount * weight * 3;  // 제목은 3배!
                    matchCount++;
                }
                
                // 본문에서 키워드 발견 → 가중치 1배
                int contentCount = countOccurrences(contentLower, keyword);
                if (contentCount > 0) {
                    score += contentCount * weight;
                    if (titleCount == 0) {
                        matchCount++;
                    }
                }
            }

            if (score > 0) {
                scores.put(category, score);
                matchCounts.put(category, matchCount);
            }
        }

        // 🎯 3단계: 최소 점수 & 최소 2개 키워드 매칭 체크
        Optional<Map.Entry<NewsCategory, Integer>> maxEntry = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue());
        
        if (maxEntry.isPresent()) {
            NewsCategory bestCategory = maxEntry.get().getKey();
            int bestScore = maxEntry.get().getValue();
            int bestMatchCount = matchCounts.get(bestCategory);
            
            // 최소 점수 8점 & 최소 2개 키워드 매칭 필수
            if (bestScore >= MINIMUM_SCORE && bestMatchCount >= MINIMUM_KEYWORD_MATCHES) {
                return bestCategory;
            }
        }

        return GENERAL;
    }

    /**
     * 부동산 핵심 키워드 체크 (복합 키워드 추가)
     */
    private static boolean checkRealEstateKeywords(String text) {
        // 부동산 핵심 키워드 (복합 키워드 우선)
        String[] coreKeywords = {
            // 복합 키워드 (우선)
            "아파트분양", "아파트시세", "아파트가격", "아파트매매", "아파트값",
            "주택분양", "주택시세", "주택가격", "주택매매",
            "집값", "부동산시장", "주택시장",
            
            // 단일 키워드
            "부동산", "아파트", "주택", "주거", "오피스텔", "빌라",
            "전세", "월세", "매매", "분양", "청약", "입주",
            "재건축", "재개발", "리모델링",
            "주상복합", "다세대", "단독주택",
            "택지", "주거지"
        };

        for (String keyword : coreKeywords) {
            if (text.contains(keyword.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * 문자열에서 특정 키워드의 출현 횟수 카운트
     */
    private static int countOccurrences(String text, String keyword) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(keyword, index)) != -1) {
            count++;
            index += keyword.length();
        }
        return count;
    }

    /**
     * 키워드 Map 생성 헬퍼 메서드
     */
    private static Map<String, Integer> createKeywords(Object... pairs) {
        Map<String, Integer> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((String) pairs[i], (Integer) pairs[i + 1]);
        }
        return map;
    }

    /**
     * 모든 카테고리의 displayName 목록
     */
    public static List<String> getAllDisplayNames() {
        return Arrays.stream(values())
                .map(NewsCategory::getDisplayName)
                .toList();
    }

    /**
     * 키워드 목록 반환 (호환성 유지)
     */
    public List<String> getKeywordList() {
        return new ArrayList<>(keywords.keySet());
    }
}
