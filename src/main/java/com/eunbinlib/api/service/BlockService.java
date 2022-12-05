package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.blockbetweenmembers.Block;
import com.eunbinlib.api.domain.repository.blockbetweenmembers.BlockRepository;
import com.eunbinlib.api.domain.user.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlockService {

    private final UserService userService;
    private final BlockRepository blockRepository;

    @Transactional
    public void blockUser(Long blockerId, Long blockedId) {
        Member blocker = userService.findMemberById(blockerId);
        Member blocked = userService.findMemberById(blockedId);

        boolean alreadyBlocked = blockRepository.existsByBlockerAndBlocked(blocker, blocked);
        if (alreadyBlocked) {
            return;
        }

        blockRepository.save(Block.builder()
                .blocker(blocker)
                .blocked(blocked)
                .build()
        );
    }

    @Transactional
    public void unblockUser(Long blockerId, Long blockedId) {
        Member blocker = userService.findMemberById(blockerId);
        Member blocked = userService.findMemberById(blockedId);

        blockRepository.deleteByBlockerAndBlocked(blocker, blocked);
    }
}
