package com.eunbinlib.api.domain.entity.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="USERS")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    protected User(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = RoleType.ROLE_USER;
    }

    @Transient
    public String getDiscriminatorValue() {
        return this.getClass().getSimpleName().toLowerCase();
    }
}
