package com.eunbinlib.api.application.domain.user;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("GUEST")
public class Guest extends User {

    @Builder
    public Guest(String username, String password) {
        super(username, password);
    }
}
