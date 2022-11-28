package com.eunbinlib.api.service;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.repository.user.MemberRepository;
import com.eunbinlib.api.domain.repository.user.UserRepository;
import com.eunbinlib.api.domain.user.Guest;
import com.eunbinlib.api.domain.user.Member;
import com.eunbinlib.api.dto.request.GuestCreateRequest;
import com.eunbinlib.api.dto.request.MeUpdateRequest;
import com.eunbinlib.api.dto.request.MemberCreateRequest;
import com.eunbinlib.api.dto.response.UserMeResponse;
import com.eunbinlib.api.exception.type.notfound.UserNotFoundException;
import com.eunbinlib.api.utils.ImageUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    @Value("${images.profile.dir}")
    private String profileImageDir;

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserMeResponse readMeByUsername(String username) {
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return UserMeResponse.from(findMember, profileImageDir);
    }

    public void createMember(MemberCreateRequest memberCreateRequest) {

        Member member = Member.builder()
                .username(memberCreateRequest.getUsername())
                .password(memberCreateRequest.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                .nickname(memberCreateRequest.getNickname())
                .build();

        userRepository.save(member);
    }

    public void createGuest(GuestCreateRequest guestCreateRequest) {

        Guest guest = Guest.builder()
                .username(guestCreateRequest.getUsername())
                .password(guestCreateRequest.getPassword()) // TODO: 비밀번호 해싱 필요, BScrypt 인코딩 필요
                .build();

        userRepository.save(guest);
    }

    @Transactional
    public void updateMe(Long userId, MeUpdateRequest meUpdateRequest) {

        Member me = findMemberById(userId);

        BaseImageFile baseImageFile = null;
        if (meUpdateRequest.getProfileImageFile() != null) {
            baseImageFile = ImageUtils.storeImage(
                    profileImageDir, meUpdateRequest.getProfileImageFile());
        }

        me.update(meUpdateRequest.getNickname(), baseImageFile);
    }
}
