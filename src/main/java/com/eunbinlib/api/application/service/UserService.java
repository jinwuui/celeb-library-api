package com.eunbinlib.api.application.service;

import com.eunbinlib.api.application.utils.EncryptUtils;
import com.eunbinlib.api.application.utils.ImageUtils;
import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.application.domain.repository.user.MemberRepository;
import com.eunbinlib.api.application.domain.repository.user.UserRepository;
import com.eunbinlib.api.application.domain.user.Guest;
import com.eunbinlib.api.application.domain.user.Member;
import com.eunbinlib.api.application.dto.request.GuestCreateRequest;
import com.eunbinlib.api.application.dto.request.MeUpdateRequest;
import com.eunbinlib.api.application.dto.request.MemberCreateRequest;
import com.eunbinlib.api.application.dto.response.UserMeResponse;
import com.eunbinlib.api.application.exception.type.notfound.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final MemberRepository memberRepository;

    public Member findMemberById(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(UserNotFoundException::new);
    }

    public UserMeResponse readMeByUsername(String username) {
        Member findMember = memberRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return UserMeResponse.from(findMember);
    }

    public void createMember(MemberCreateRequest memberCreateRequest) {
        Member member = Member.builder()
                .username(memberCreateRequest.getUsername())
                .password(EncryptUtils.encrypt(memberCreateRequest.getPassword()))
                .nickname(memberCreateRequest.getNickname())
                .build();

        userRepository.save(member);
    }

    public void createGuest(GuestCreateRequest guestCreateRequest) {
        Guest guest = Guest.builder()
                .username(guestCreateRequest.getUsername())
                .password(EncryptUtils.encrypt(guestCreateRequest.getPassword()))
                .build();

        userRepository.save(guest);
    }

    @Transactional
    public void updateMe(Long userId, MeUpdateRequest meUpdateRequest) {
        Member me = findMemberById(userId);

        BaseImageFile baseImageFile = null;
        if (meUpdateRequest.getProfileImageFile() != null) {
            baseImageFile = ImageUtils.storeImage(meUpdateRequest.getProfileImageFile());
        }

        me.update(meUpdateRequest.getNickname(), baseImageFile);
    }
}
