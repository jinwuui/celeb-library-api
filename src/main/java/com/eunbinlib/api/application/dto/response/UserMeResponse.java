package com.eunbinlib.api.application.dto.response;

import com.eunbinlib.api.application.domain.imagefile.ProfileImageFile;
import com.eunbinlib.api.application.domain.user.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserMeResponse {

    private final Long id;

    private final String nickname;

    private final String profileImageUrl;

    private final String userType;

    @Builder
    public UserMeResponse(Long id, String nickname, String profileImageUrl, String userType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageUrl = profileImageUrl;
        this.userType = userType;
    }

    public static UserMeResponse from(Member member) {
        String profileImageUrl = generateProfileImageUrl(member.getProfileImageFile());

        return UserMeResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname().getValue())
                .profileImageUrl(profileImageUrl)
                .userType(member.getUserType())
                .build();
    }

    private static String generateProfileImageUrl(ProfileImageFile profileImageFile) {
        if (profileImageFile == null) {
            return "default_profile";
        }

        // TODO: change to AWS S3 URL
        return profileImageFile
                .getBaseImageFile()
                .getStoredFilename();
    }
}
