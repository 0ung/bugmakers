-- 어제 날짜 (2026-02-06) 뉴스 더미 데이터

-- 부동산 뉴스 (REAL_ESTATE)
INSERT INTO news (title, content, reference, category, view_count, like_count, favorite_count, share_count, report_count, deleted, created_date, last_modified_date)
VALUES 
('서울 아파트 평균 매매가 12억 돌파', '서울 지역 아파트 평균 매매가가 처음으로 12억원을 넘어섰습니다. 강남3구를 중심으로 가격 상승세가 지속되고 있으며...', 'https://news.example.com/real-estate/1', 'REAL_ESTATE', 0, 0, 0, 0, 0, false, '2026-02-06 09:00:00', '2026-02-06 09:00:00'),
('청약 경쟁률 100대1 돌파, 분양시장 과열', '수도권 신규 분양 아파트의 청약 경쟁률이 100대1을 넘어서며 과열 양상을 보이고 있습니다...', 'https://news.example.com/real-estate/2', 'REAL_ESTATE', 0, 0, 0, 0, 0, false, '2026-02-06 10:30:00', '2026-02-06 10:30:00'),
('GTX 개통으로 인근 아파트값 급등', 'GTX-A 노선 개통을 앞두고 인근 지역 아파트 가격이 급등하고 있습니다. 파주, 운정 신도시 등에서...', 'https://news.example.com/real-estate/3', 'REAL_ESTATE', 0, 0, 0, 0, 0, false, '2026-02-06 14:20:00', '2026-02-06 14:20:00'),
('재건축 규제 완화, 강남 재건축 단지 들썩', '정부의 재건축 규제 완화 방침에 따라 강남 지역 재건축 단지들이 들썩이고 있습니다...', 'https://news.example.com/real-estate/4', 'REAL_ESTATE', 0, 0, 0, 0, 0, false, '2026-02-06 16:45:00', '2026-02-06 16:45:00'),
('전월세 상한제 개편안 발표', '정부가 전월세 상한제 개편안을 발표했습니다. 현행 5%에서 7%로 상향 조정하는 내용을...', 'https://news.example.com/real-estate/5', 'REAL_ESTATE', 0, 0, 0, 0, 0, false, '2026-02-06 18:00:00', '2026-02-06 18:00:00');

-- 정책 뉴스 (POLICY)
INSERT INTO news (title, content, reference, category, view_count, like_count, favorite_count, share_count, report_count, deleted, created_date, last_modified_date)
VALUES 
('2026년 주택공급 확대 정책 발표', '정부가 2026년 주택공급 확대 정책을 발표했습니다. 수도권에 30만호 공급 목표...', 'https://news.example.com/policy/1', 'POLICY', 0, 0, 0, 0, 0, false, '2026-02-06 09:30:00', '2026-02-06 09:30:00'),
('생애최초 구매자 대출한도 상향', '생애최초 주택 구매자에 대한 대출한도가 6억에서 8억으로 상향 조정됩니다...', 'https://news.example.com/policy/2', 'POLICY', 0, 0, 0, 0, 0, false, '2026-02-06 11:00:00', '2026-02-06 11:00:00'),
('신혼부부 특별공급 확대', '신혼부부 특별공급 물량이 기존 대비 2배 확대됩니다. 소득 기준도 완화...', 'https://news.example.com/policy/3', 'POLICY', 0, 0, 0, 0, 0, false, '2026-02-06 15:30:00', '2026-02-06 15:30:00');

-- 금융 뉴스 (FINANCE)
INSERT INTO news (title, content, reference, category, view_count, like_count, favorite_count, share_count, report_count, deleted, created_date, last_modified_date)
VALUES 
('주택담보대출 금리 연 4% 진입', '시중은행 주택담보대출 금리가 연 4%대로 진입했습니다. 기준금리 인하 영향...', 'https://news.example.com/finance/1', 'FINANCE', 0, 0, 0, 0, 0, false, '2026-02-06 10:00:00', '2026-02-06 10:00:00'),
('DSR 규제 완화 검토', '금융당국이 DSR 규제 완화를 검토 중입니다. 실수요자 대출 부담 완화 목적...', 'https://news.example.com/finance/2', 'FINANCE', 0, 0, 0, 0, 0, false, '2026-02-06 13:20:00', '2026-02-06 13:20:00');

-- 일반 뉴스 (GENERAL)
INSERT INTO news (title, content, reference, category, view_count, like_count, favorite_count, share_count, report_count, deleted, created_date, last_modified_date)
VALUES 
('부동산 시장 전망 설문조사', '전문가 70%가 올해 집값 상승 전망. 부동산 시장에 대한 낙관론 확산...', 'https://news.example.com/general/1', 'GENERAL', 0, 0, 0, 0, 0, false, '2026-02-06 12:00:00', '2026-02-06 12:00:00'),
('1인 가구 증가로 소형 주택 수요 급증', '1인 가구가 전체 가구의 40%를 넘어서며 소형 주택 수요가 급증하고 있습니다...', 'https://news.example.com/general/2', 'GENERAL', 0, 0, 0, 0, 0, false, '2026-02-06 17:00:00', '2026-02-06 17:00:00');
