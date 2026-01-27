export type MenuLevel = 'LV1' | 'LV2' | 'LV3' | 'LV4' | 'COMMENT' | 'RE_COMMENT';
export type MenuType = 'NEWS' | 'REGION' | 'FORUM';

export interface MenuItem {
name: string;
type: MenuType;
level: MenuLevel;
seq: number;
children?: MenuItem[];
}

// Backend type → Frontend path 매핑
export const MENU_PATH_MAP: Record<MenuType, string> = {
  NEWS: '/news',
  REGION: '/trend',
  FORUM: '/community'
};

// MenuType으로 path 가져오기
export function getMenuPath(type: MenuType): string {
  return MENU_PATH_MAP[type];
}

export const MENU_TREE: MenuItem[] = [
    {
        name: '부동산뉴스',
        type: 'NEWS',
        level: 'LV1',
        seq: 1
    },
    {
        name: '시세트렌드',
        type: 'REGION',
        level: 'LV1',
        seq: 2,
        children: [
            {
                name: '서울',
                type: 'REGION',
                level: 'LV2',
                seq: 1,
                children: [
                    { name: '종로구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '용산구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '성동구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '광진구', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '동대문구', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '중랑구', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '성북구', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '강북구', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '도봉구', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '노원구', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '은평구', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '서대문구', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '마포구', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '양천구', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '강서구', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '구로구', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '금천구', type: 'REGION', level: 'LV3', seq: 18 },
                    { name: '영등포구', type: 'REGION', level: 'LV3', seq: 19 },
                    { name: '동작구', type: 'REGION', level: 'LV3', seq: 20 },
                    { name: '관악구', type: 'REGION', level: 'LV3', seq: 21 },
                    { name: '서초구', type: 'REGION', level: 'LV3', seq: 22 },
                    {
                        name: '강남구',
                        type: 'REGION',
                        level: 'LV3',
                        seq: 23,
                        children: [
                            { name: '신사동', type: 'REGION', level: 'LV4', seq: 1 },
                            { name: '논현동', type: 'REGION', level: 'LV4', seq: 2 },
                            { name: '압구정동', type: 'REGION', level: 'LV4', seq: 3 },
                            { name: '청담동', type: 'REGION', level: 'LV4', seq: 4 },
                            { name: '삼성동', type: 'REGION', level: 'LV4', seq: 5 },
                            { name: '대치동', type: 'REGION', level: 'LV4', seq: 6 },
                            { name: '역삼동', type: 'REGION', level: 'LV4', seq: 7 },
                            { name: '도곡동', type: 'REGION', level: 'LV4', seq: 8 },
                            { name: '개포동', type: 'REGION', level: 'LV4', seq: 9 },
                            { name: '일원동', type: 'REGION', level: 'LV4', seq: 10 },
                            { name: '수서동', type: 'REGION', level: 'LV4', seq: 11 },
                            { name: '세곡동', type: 'REGION', level: 'LV4', seq: 12 },
                            { name: '자곡동', type: 'REGION', level: 'LV4', seq: 13 },
                            { name: '율현동', type: 'REGION', level: 'LV4', seq: 14 }
                        ]
                    },
                    { name: '송파구', type: 'REGION', level: 'LV3', seq: 24 },
                    { name: '강동구', type: 'REGION', level: 'LV3', seq: 25 }
                ]
            },
            {
                name: '경기',
                type: 'REGION',
                level: 'LV2',
                seq: 2,
                children: [
                    { name: '수원시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '성남시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '의정부시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '안양시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '부천시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '광명시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '평택시', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '동두천시', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '안산시', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '고양시', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '과천시', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '구리시', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '남양주시', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '오산시', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '시흥시', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '군포시', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '의왕시', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '하남시', type: 'REGION', level: 'LV3', seq: 18 },
                    { name: '용인시', type: 'REGION', level: 'LV3', seq: 19 },
                    { name: '파주시', type: 'REGION', level: 'LV3', seq: 20 },
                    { name: '이천시', type: 'REGION', level: 'LV3', seq: 21 },
                    { name: '안성시', type: 'REGION', level: 'LV3', seq: 22 },
                    { name: '김포시', type: 'REGION', level: 'LV3', seq: 23 },
                    { name: '화성시', type: 'REGION', level: 'LV3', seq: 24 },
                    { name: '광주시', type: 'REGION', level: 'LV3', seq: 25 },
                    { name: '양주시', type: 'REGION', level: 'LV3', seq: 26 },
                    { name: '포천시', type: 'REGION', level: 'LV3', seq: 27 },
                    { name: '여주시', type: 'REGION', level: 'LV3', seq: 28 },
                    { name: '연천군', type: 'REGION', level: 'LV3', seq: 29 },
                    { name: '가평군', type: 'REGION', level: 'LV3', seq: 30 },
                    { name: '양평군', type: 'REGION', level: 'LV3', seq: 31 }
                ]
            },
            {
                name: '인천',
                type: 'REGION',
                level: 'LV2',
                seq: 3,
                children: [
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '미추홀구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '연수구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '남동구', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '부평구', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '계양구', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '서구', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '강화군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '옹진군', type: 'REGION', level: 'LV3', seq: 10 }
                ]
            },
            {
                name: '부산',
                type: 'REGION',
                level: 'LV2',
                seq: 4,
                children: [
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '서구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '영도구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '부산진구', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '동래구', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '남구', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '북구', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '해운대구', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '사하구', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '금정구', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '강서구', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '연제구', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '수영구', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '사상구', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '기장군', type: 'REGION', level: 'LV3', seq: 16 }
                ]
            },
            {
                name: '대구',
                type: 'REGION',
                level: 'LV2',
                seq: 5,
                children: [
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '서구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '남구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '북구', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '수성구', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '달서구', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '달성군', type: 'REGION', level: 'LV3', seq: 8 }
                ]
            },
            {
                name: '광주',
                type: 'REGION',
                level: 'LV2',
                seq: 6,
                children: [
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '서구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '남구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '북구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '광산구', type: 'REGION', level: 'LV3', seq: 5 }
                ]
            },
            {
                name: '대전',
                type: 'REGION',
                level: 'LV2',
                seq: 7,
                children: [
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '서구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '유성구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '대덕구', type: 'REGION', level: 'LV3', seq: 5 }
                ]
            },
            {
                name: '울산',
                type: 'REGION',
                level: 'LV2',
                seq: 8,
                children: [
                    { name: '중구', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '남구', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '동구', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '북구', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '울주군', type: 'REGION', level: 'LV3', seq: 5 }
                ]
            },
            {
                name: '세종',
                type: 'REGION',
                level: 'LV2',
                seq: 9,
                children: [
                    { name: '세종시', type: 'REGION', level: 'LV3', seq: 1 }
                ]
            },
            {
                name: '강원',
                type: 'REGION',
                level: 'LV2',
                seq: 10,
                children: [
                    { name: '춘천시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '원주시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '강릉시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '동해시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '태백시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '속초시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '삼척시', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '홍천군', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '횡성군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '영월군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '평창군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '정선군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '철원군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '화천군', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '양구군', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '인제군', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '고성군', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '양양군', type: 'REGION', level: 'LV3', seq: 18 }
                ]
            },
            {
                name: '충북',
                type: 'REGION',
                level: 'LV2',
                seq: 11,
                children: [
                    { name: '청주시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '충주시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '제천시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '보은군', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '옥천군', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '영동군', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '증평군', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '진천군', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '괴산군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '음성군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '단양군', type: 'REGION', level: 'LV3', seq: 11 }
                ]
            },
            {
                name: '충남',
                type: 'REGION',
                level: 'LV2',
                seq: 12,
                children: [
                    { name: '천안시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '공주시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '보령시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '아산시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '서산시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '논산시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '계룡시', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '당진시', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '금산군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '부여군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '서천군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '청양군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '홍성군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '예산군', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '태안군', type: 'REGION', level: 'LV3', seq: 15 }
                ]
            },
            {
                name: '전북',
                type: 'REGION',
                level: 'LV2',
                seq: 13,
                children: [
                    { name: '전주시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '군산시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '익산시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '정읍시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '남원시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '김제시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '완주군', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '진안군', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '무주군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '장수군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '임실군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '순창군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '고창군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '부안군', type: 'REGION', level: 'LV3', seq: 14 }
                ]
            },
            {
                name: '전남',
                type: 'REGION',
                level: 'LV2',
                seq: 14,
                children: [
                    { name: '목포시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '여수시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '순천시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '나주시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '광양시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '담양군', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '곡성군', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '구례군', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '고흥군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '보성군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '화순군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '장흥군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '강진군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '해남군', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '영암군', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '무안군', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '함평군', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '영광군', type: 'REGION', level: 'LV3', seq: 18 },
                    { name: '장성군', type: 'REGION', level: 'LV3', seq: 19 },
                    { name: '완도군', type: 'REGION', level: 'LV3', seq: 20 },
                    { name: '진도군', type: 'REGION', level: 'LV3', seq: 21 },
                    { name: '신안군', type: 'REGION', level: 'LV3', seq: 22 }
                ]
            },
            {
                name: '경북',
                type: 'REGION',
                level: 'LV2',
                seq: 15,
                children: [
                    { name: '포항시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '경주시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '김천시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '안동시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '구미시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '영주시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '영천시', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '상주시', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '문경시', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '경산시', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '의성군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '청송군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '영양군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '영덕군', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '청도군', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '고령군', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '성주군', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '칠곡군', type: 'REGION', level: 'LV3', seq: 18 },
                    { name: '예천군', type: 'REGION', level: 'LV3', seq: 19 },
                    { name: '봉화군', type: 'REGION', level: 'LV3', seq: 20 },
                    { name: '울진군', type: 'REGION', level: 'LV3', seq: 21 },
                    { name: '울릉군', type: 'REGION', level: 'LV3', seq: 22 }
                ]
            },
            {
                name: '경남',
                type: 'REGION',
                level: 'LV2',
                seq: 16,
                children: [
                    { name: '창원시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '진주시', type: 'REGION', level: 'LV3', seq: 2 },
                    { name: '통영시', type: 'REGION', level: 'LV3', seq: 3 },
                    { name: '사천시', type: 'REGION', level: 'LV3', seq: 4 },
                    { name: '김해시', type: 'REGION', level: 'LV3', seq: 5 },
                    { name: '밀양시', type: 'REGION', level: 'LV3', seq: 6 },
                    { name: '거제시', type: 'REGION', level: 'LV3', seq: 7 },
                    { name: '양산시', type: 'REGION', level: 'LV3', seq: 8 },
                    { name: '의령군', type: 'REGION', level: 'LV3', seq: 9 },
                    { name: '함안군', type: 'REGION', level: 'LV3', seq: 10 },
                    { name: '창녕군', type: 'REGION', level: 'LV3', seq: 11 },
                    { name: '고성군', type: 'REGION', level: 'LV3', seq: 12 },
                    { name: '남해군', type: 'REGION', level: 'LV3', seq: 13 },
                    { name: '하동군', type: 'REGION', level: 'LV3', seq: 14 },
                    { name: '산청군', type: 'REGION', level: 'LV3', seq: 15 },
                    { name: '함양군', type: 'REGION', level: 'LV3', seq: 16 },
                    { name: '거창군', type: 'REGION', level: 'LV3', seq: 17 },
                    { name: '합천군', type: 'REGION', level: 'LV3', seq: 18 }
                ]
            },
            {
                name: '제주',
                type: 'REGION',
                level: 'LV2',
                seq: 17,
                children: [
                    { name: '제주시', type: 'REGION', level: 'LV3', seq: 1 },
                    { name: '서귀포시', type: 'REGION', level: 'LV3', seq: 2 }
                ]
            }
        ]
    },
    {
        name: '커뮤니티',
        type: 'FORUM',
        level: 'LV1',
        seq: 3,
        children: [
            {
                name: '댓글',
                type: 'FORUM',
                level: 'COMMENT',
                seq: 1,
                children: [
                    {
                        name: '대댓글',
                        type: 'FORUM',
                        level: 'RE_COMMENT',
                        seq: 2
                    }
                ]
            }
        ]
    }
];

// 유틸리티 함수들
export function findMenuByName(name: string, items: MenuItem[] = MENU_TREE): MenuItem | undefined {
    for (const item of items) {
        if (item.name === name) return item;
        if (item.children) {
            const found = findMenuByName(name, item.children);
            if (found) return found;
        }
    }
    return undefined;
}

export function getMenusByLevel(level: MenuLevel, items: MenuItem[] = MENU_TREE): MenuItem[] {
    const result: MenuItem[] = [];
    for (const item of items) {
        if (item.level === level) {
            result.push(item);
        }
        if (item.children) {
            result.push(...getMenusByLevel(level, item.children));
        }
    }
    return result;
}

export function getMenusByType(type: MenuType, items: MenuItem[] = MENU_TREE): MenuItem[] {
    const result: MenuItem[] = [];
    for (const item of items) {
        if (item.type === type) {
            result.push(item);
        }
        if (item.children) {
            result.push(...getMenusByType(type, item.children));
        }
    }
    return result;
}

// Navigation에서 사용하기 편한 최상위 메뉴만 추출
export const TOP_MENUS = MENU_TREE.filter(item => item.level === 'LV1');

// Navigation용 - path 포함
export const TOP_MENUS_WITH_PATH = TOP_MENUS.map(menu => ({
  ...menu,
  path: getMenuPath(menu.type)
}));