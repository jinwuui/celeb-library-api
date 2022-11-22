package com.eunbinlib.api.domain.user;

import com.eunbinlib.api.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotBlank(message = "아이디는 필수 입니다.")
    private String username;

    @NotBlank(message = "비밀번호는 필수 입니다.")
    private String password;

    protected User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Transient
    public String getUserType() {
        return this.getClass().getSimpleName().toLowerCase();
    }

}
