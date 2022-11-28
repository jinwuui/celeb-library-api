package com.eunbinlib.api.dto.response;

import com.eunbinlib.api.domain.imagefile.ProfileImageFile;
import com.eunbinlib.api.domain.user.Member;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class UserMeResponse {

    private final Long id;

    private final String nickname;

    private final String profileImageFilePath;

    private final String userType;

    @Builder
    public UserMeResponse(Long id, String nickname, String profileImageFilePath, String userType) {
        this.id = id;
        this.nickname = nickname;
        this.profileImageFilePath = profileImageFilePath;
        this.userType = userType;
    }

    public static UserMeResponse from(Member member, String profileImageDir) {
        String profileImageFilePath = generateProfileImageFilePath(member.getProfileImageFile(), profileImageDir);

        return UserMeResponse.builder()
                .id(member.getId())
                .nickname(member.getNickname().getValue())
                .profileImageFilePath(profileImageFilePath)
                .userType(member.getUserType())
                .build();
    }

    private static String generateProfileImageFilePath(ProfileImageFile profileImageFile, String profileImageDir) {
        if (profileImageFile == null) {
            return "default_profile";
        }

        return StringUtils.join(profileImageDir, profileImageFile
                .getBaseImageFile()
                .getStoredFilename());
    }
}
