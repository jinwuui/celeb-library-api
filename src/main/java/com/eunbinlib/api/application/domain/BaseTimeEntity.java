package com.eunbinlib.api.application.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;


@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@MappedSuperclass // 1
@EntityListeners(AuditingEntityListener.class) // 2
public abstract class BaseTimeEntity {

    @CreatedDate // 3
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate // 4
    @Column(name = "modified_date", nullable = false)
    private LocalDateTime modifiedDate;

}

// 1 : JPA Entity 클래스들이 BaseTimeEntity을 상속할 경우 필드들(createdDate, modifiedDate)도 칼럼으로 인식하도록 한다.
// 2 : BaseTimeEntity 클래스에 Auditing 기능을 포함
// 3 : Entity가 생성되어 저장될 때 시간이 자동 저장
// 4 : 조회한 Entity의 값을 변경할 때 시간이 자동 저장
