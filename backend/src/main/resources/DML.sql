-- 2026-01-26 엔티티 수정 관련 하여 NOT NULL조건 때문에 hibernate auto update 안되는 건 수동 DML

ALTER TABLE news
    ADD COLUMN favorited_count BIGINT;

UPDATE news SET favorited_count = 0;

ALTER TABLE news
    ALTER COLUMN favorited_count SET NOT NULL;

ALTER TABLE news
    RENAME COLUMN heart_count TO like_count;

select * from news;


