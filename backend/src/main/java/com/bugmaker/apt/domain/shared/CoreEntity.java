package com.bugmaker.apt.domain.shared;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/**
 * 논리 삭제를 위한 주요 Entity용 BaseEntity 입니다.
 * */

/*
- Soft 삭제
- 주요 Entity (비즈니스 핵심 데이터) 는 물리적 삭제가 아닌, 기록을 남기는 논리 삭제로 진행
- delete 컬럼을 추가한 주요 Entity용 Base Entity를 새로 생성함

- 사용법 :
@Entity
@Where(clause = "deleted = false")
public class Forum extends CoreEntity {

: 이렇게 사용 하면 forumRepository.findAll() 해도 삭제된 글 자동 제외됨.
*/

@MappedSuperclass
@Getter
public abstract class CoreEntity extends BaseEntity {

    @Column(nullable = false)
    protected boolean deleted = false;


    public void delete() {
        this.deleted = true;
    }

    public void restore() {
        this.deleted = false;
    }
}
