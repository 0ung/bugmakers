package com.bugmaker.apt.common.util;

import com.bugmaker.apt.domain.member.NicknameCreator;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.Random;


@Component
public class CustomRandomNickname implements NicknameCreator {
    private final String[] ADJECTIVES = {
            "도도한", "까칠한", "능청스러운", "앙칼진", "시크한",
            "도발적인", "톡톡 튀는", "심술궂은", "약삭빠른", "얄미운",
            "뻔뻔한", "제멋대로인", "까불거리는", "콧대 높은", "깐깐한",
            "능글맞은", "야심찬", "영악한", "건방진", "당돌한",
            "엉뚱한", "반항적인", "자신감 넘치는", "시니컬한", "비꼬는",
            "시건방진", "오만한", "얄궂은", "고집 센", "삐딱한",
            "개구쟁이 같은", "거만한", "으스대는", "거침없는", "천연덕스러운",
            "넉살 좋은", "비딱한", "시큰둥한", "자유분방한", "엉뚱 발랄한",
            "치명적인", "쿨한", "속물적인", "요염한", "깐죽거리는",
            "뭉툭한", "엉성한", "능청맞은", "도발적인", "새침한"
    };

    private final String[] NOUNS = {
            "너구리", "다람쥐", "고양이", "원숭이", "하마",
            "여우", "토끼", "병아리", "부엉이", "펭귄",
            "두더지", "사자", "호랑이", "코뿔소", "악어",
            "망고", "자몽", "앵두", "복숭아", "바나나",
            "순돌", "복길", "영철", "미숙", "짱구",
            "맹구", "철수", "유리", "훈이", "두목"
    };

    private final Random RANDOM = new Random();

    @Override
    public String generate() {
        String adjective = ADJECTIVES[RANDOM.nextInt(ADJECTIVES.length)];

        String noun = NOUNS[RANDOM.nextInt(NOUNS.length)];

        String randomInt = String.valueOf(RANDOM.nextInt(999));

        return MessageFormat.format("{0}{1}{2}", adjective, noun, randomInt);
    }
}
