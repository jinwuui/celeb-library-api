package com.eunbinlib.api.application.domain.repository.user;

import com.eunbinlib.api.application.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

}