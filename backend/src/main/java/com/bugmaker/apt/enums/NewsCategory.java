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
                    "부동산", 5, "아파트", 5, "주택", 4, "주거", 3, "주상복합", 3,
                    "분양", 4, "청약", 4, "전세", 3, "월세", 3, "매매", 3,
                    "재건축", 4, "재개발", 4,
                    "신도시", 2  // 낮은 가중치 - 복합 키워드에서 점수 합산됨
            )
    ),

    MARKET("시장",
            createKeywords(
                    "거래", 4, "시세", 5, "가격", 4, "집값", 5,
                    "상승", 3, "하락", 3, "보합", 3,
                    "미분양", 4, "입주", 3, "공급", 3
            )
    ),

    POLICY("정책",
            createKeywords(
                    "정부", 5, "국토부", 5, "규제", 5, "완화", 4,
                    "부동산정책", 5, "주택정책", 5,
                    "공시가", 4, "공시가격", 4, "임대차", 4
            )
    ),

    FINANCE("금융",
            createKeywords(
                    "대출", 5, "주담대", 5, "전세대출", 5,
                    "금리", 5, "기준금리", 5,
                    "LTV", 4, "DTI", 4, "DSR", 4
            )
    ),

    TAX("세금",
            createKeywords(
                    "세금", 5, "과세", 4,
                    "취득세", 5, "양도세", 5, "종부세", 5, "보유세", 4,
                    "거래세", 4, "분리과세", 4
            )
    ),

    INFRA("지역·인프라",
            createKeywords(
                    "GTX", 5, "철도", 4, "도로", 3, "교통", 3,
                    "지역개발", 4, "도시개발", 4, "균형발전", 3,
                    "지방", 3, "거점", 3, "산업단지", 3, "규제혁신", 3,
                    "신도시", 3  // GTX와 함께 나오면 점수 합산 (3+5=8)
            )
    ),

    GENERAL("일반", Map.of());

    private final String displayName;  // 화면 표시명
    private final Map<String, Integer> keywords;  // 키워드 -> 가중치

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
     * 제목/내용에서 카테고리 분류 (점수 기반)
     * 
     * 각 키워드마다 가중치를 부여하고,
     * 출현 횟수 * 가중치 = 점수로 계산하여
     * 가장 높은 점수의 카테고리를 반환
     * 
     * 예시:
     * - "신도시 + GTX" → INFRA (3+5=8점)
     * - "신도시 + 분양" → REAL_ESTATE (2+4=6점)
     */
    public static NewsCategory classify(String title, String content) {
        String text = (title + " " + content).toLowerCase();

        Map<NewsCategory, Integer> scores = new EnumMap<>(NewsCategory.class);

        for (NewsCategory category : values()) {
            if (category == GENERAL) {
                continue;
            }

            int score = 0;
            for (Map.Entry<String, Integer> entry : category.keywords.entrySet()) {
                String keyword = entry.getKey().toLowerCase();
                int weight = entry.getValue();

                // 키워드 출현 횟수 * 가중치
                int count = countOccurrences(text, keyword);
                score += count * weight;
            }

            if (score > 0) {
                scores.put(category, score);
            }
        }

        // 가장 높은 점수의 카테고리 반환
        return scores.isEmpty() ? GENERAL :
                scores.entrySet().stream()
                        .max(Map.Entry.comparingByValue())
                        .map(Map.Entry::getKey)
                        .orElse(GENERAL);
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
     * 
     * 사용법: createKeywords("키워드1", 가중치1, "키워드2", 가중치2, ...)
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
     * 
     * Python 크롤러와 gRPC 통신을 위해
     * 키워드만 추출하여 List로 반환
     */
    public List<String> getKeywordList() {
        return new ArrayList<>(keywords.keySet());
    }
}
