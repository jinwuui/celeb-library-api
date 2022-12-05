package com.eunbinlib.api.domain.repository.blockbetweenmembers;

import com.eunbinlib.api.domain.blockbetweenmembers.Block;
import com.eunbinlib.api.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

    void deleteByBlockerAndBlocked(Member blocker, Member blocked);

}
