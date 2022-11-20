package com.eunbinlib.api.repository.user;

import com.eunbinlib.api.domain.entity.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
