package com.eunbinlib.api.domain.repository.block;

import com.eunbinlib.api.domain.block.Block;
import com.eunbinlib.api.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlockRepository extends JpaRepository<Block, Long> {

    Optional<Block> findByBlockerAndBlocked(Member blocker, Member blocked);

    boolean existsByBlockerAndBlocked(Member blocker, Member blocked);

    void deleteByBlockerAndBlocked(Member blocker, Member blocked);

}
