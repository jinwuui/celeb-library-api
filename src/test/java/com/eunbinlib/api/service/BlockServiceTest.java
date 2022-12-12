package com.eunbinlib.api.service;

import com.eunbinlib.api.application.domain.block.Block;
import com.eunbinlib.api.application.domain.user.Member;
import com.eunbinlib.api.application.exception.type.notfound.UserNotFoundException;
import com.eunbinlib.api.application.service.BlockService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Slf4j
class BlockServiceTest extends ServiceTest {

    @Autowired
    BlockService blockService;

    @Nested
    @DisplayName("유저 차단")
    class BlockUser {

        @Test
        @DisplayName("유저를 차단하는 경우")
        void blockUser() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            blockService.blockUser(member1.getId(), member2.getId());

            // then
            Block block = blockRepository
                    .findByBlockerAndBlocked(member1, member2)
                    .orElseThrow(IllegalArgumentException::new);

            assertThat(block.getBlocker().getId())
                    .isEqualTo(member1.getId());
            assertThat(block.getBlocked().getId())
                    .isEqualTo(member2.getId());
        }

        @Test
        @DisplayName("이미 차단된 유저를 차단하는 경우")
        void blockUserAlreadyBlocked() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();
            blockRepository.save(Block.builder()
                    .blocker(member1)
                    .blocked(member2)
                    .build()
            );

            // when
            blockService.blockUser(member1.getId(), member2.getId());
            List<Block> blockList = blockRepository.findAll();

            // then
            assertThat(blockList.size())
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("존재하지 않는 유저를 차단하는 경우")
        void blockUserNotFoundBlocked() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            assertThatThrownBy(() -> blockService
                    .blockUser(member1.getId(), member2.getId() + 100L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 유저가 차단하는 경우")
        void blockUserNotFoundBlocker() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            assertThatThrownBy(() -> blockService
                    .blockUser(member1.getId() + 100L, member2.getId()))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("유저 차단 해제")
    class UnblockUser {

        @Test
        @DisplayName("유저를 차단 해제하는 경우")
        void unblockUser() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();
            blockRepository.save(Block.builder()
                    .blocker(member1)
                    .blocked(member2)
                    .build()
            );

            // when
            blockService.unblockUser(member1.getId(), member2.getId());

            assertThat(blockRepository.existsByBlockerAndBlocked(member1, member2))
                    .isFalse();
        }

        @Test
        @DisplayName("차단되어 있지 않은 유저를 차단 해제하는 경우")
        void unblockUserNotExist() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            blockService.unblockUser(member1.getId(), member2.getId());
        }

        @Test
        @DisplayName("존재하지 않는 유저를 차단 해제하는 경우")
        void unblockUserNotFoundBlocked() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            assertThatThrownBy(() -> blockService
                    .unblockUser(member1.getId(), member2.getId() + 100L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 유저가 차단 해제하는 경우")
        void unblockUserNotFoundBlocker() {
            // given
            Member member1 = getMember();
            Member member2 = getMember();

            // when
            assertThatThrownBy(() -> blockService
                    .unblockUser(member1.getId() + 100L, member2.getId()))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }
}