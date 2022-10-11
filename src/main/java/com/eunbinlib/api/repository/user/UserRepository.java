package com.eunbinlib.api.repository.user;

import com.eunbinlib.api.domain.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long>, UserRepositoryCustom {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

}
