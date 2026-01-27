-- 2026-01-26 엔티티 수정 관련 하여 NOT NULL조건 때문에 hibernate auto update 안되는 건 수동 DML

ALTER TABLE news
    ADD COLUMN favorited_count BIGINT;

UPDATE news SET favorited_count = 0;

ALTER TABLE news
    ALTER COLUMN favorited_count SET NOT NULL;

ALTER TABLE news
    RENAME COLUMN heart_count TO like_count;

select * from news;

-- 2026-01-26 menu  삭제하고 실행시 다시 만들고 데이터 넣음

DROP TABLE IF EXISTS menu CASCADE;

select * from menu;

-- 2026-01-27

ALTER TABLE news
    RENAME COLUMN favorited_count TO favorite_count;

SELECT conname
FROM pg_constraint
WHERE conrelid = 'commented'::regclass; -- commented_seq_check

alter table commented
    drop constraint commented_seq_check;


