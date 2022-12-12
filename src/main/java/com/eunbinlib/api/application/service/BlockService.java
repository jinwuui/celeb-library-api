package com.eunbinlib.api.application.service;

import com.eunbinlib.api.application.domain.block.Block;
import com.eunbinlib.api.application.domain.repository.block.BlockRepository;
import com.eunbinlib.api.application.domain.user.Member;
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
