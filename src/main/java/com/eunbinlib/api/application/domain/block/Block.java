package com.eunbinlib.api.application.domain.block;

import com.eunbinlib.api.application.domain.user.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Block {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKER_ID", nullable = false)
    private Member blocker;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "BLOCKED_ID", nullable = false)
    private Member blocked;

    @Builder
    public Block(Member blocker, Member blocked) {
        this.blocker = blocker;
        this.blocked = blocked;
    }
}
