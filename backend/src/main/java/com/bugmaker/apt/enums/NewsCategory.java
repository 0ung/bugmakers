package com.bugmaker.apt.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

/**
 * 뉴스 카테고리 Enum
 * 카테고리와 크롤링 키워드 관리
 */
@Getter
@RequiredArgsConstructor
public enum NewsCategory {
    REAL_ESTATE("부동산", List.of(
            "아파트", "분양", "청약", "재건축", "재개발",
            "전세", "매매", "부동산", "주택", "주담대", "전세대출"
    )),
    POLICY("정책", List.of(
            "정부", "국토부", "규제", "법안", "정책", "공시가"
    )),
    MARKET("시장", List.of(
            "거래", "시세", "가격", "하락", "상승"
    )),
    FINANCE("금융", List.of(
            "대출", "금리", "담보", "DSR", "LTV"
    )),
    ECONOMY("경제", List.of(
            "경제", "산업", "기업"
    )),
    TAX("세금", List.of(
            "세금", "과세", "양도세", "취득세", "종부세", "보유세", "거래세", "분리과세"
    )),
    GENERAL("일반", List.of());

    private final String displayName;  // 화면 표시명
    private final List<String> keywords;  // 크롤링 키워드

    /** displayName으로 Enum 찾기 */
    public static NewsCategory fromDisplayName(String name) {
        if (name == null) {
            return GENERAL;
        }

        return Arrays.stream(values())
                .filter(c -> c.displayName.equals(name))
                .findFirst()
                .orElse(GENERAL);
    }

    /** 제목/내용에서 카테고리 분류 */
    public static NewsCategory classify(String title, String content) {
        String text = (title + " " + content).toLowerCase();

        for (NewsCategory category : values()) {
            if (category == GENERAL) continue;

            for (String keyword : category.keywords) {
                if (text.contains(keyword.toLowerCase())) {
                    return category;
                }
            }
        }

        return GENERAL;
    }

    /** 모든 카테고리의 displayName 목록 */
    public static List<String> getAllDisplayNames() {
        return Arrays.stream(values())
                .map(NewsCategory::getDisplayName)
                .toList();
    }
}
