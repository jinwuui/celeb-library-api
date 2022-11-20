package com.eunbinlib.api.domain.repository.user;

import com.eunbinlib.api.domain.user.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

}
