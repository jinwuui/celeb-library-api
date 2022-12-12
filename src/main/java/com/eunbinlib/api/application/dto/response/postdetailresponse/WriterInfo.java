package com.eunbinlib.api.application.dto.response.postdetailresponse;

import com.eunbinlib.api.application.domain.imagefile.ProfileImageFile;
import com.eunbinlib.api.application.domain.user.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WriterInfo {

    private Long id;

    private String nickname;

    private String profileImageUrl;

    public static WriterInfo from(final Member member) {
        String profileImageUrl = generateProfileImageUrl(member.getProfileImageFile());

        return WriterInfo.builder()
                .id(member.getId())
                .nickname(member.getNickname().getValue())
                .profileImageUrl(profileImageUrl)
                .build();
    }

    private static String generateProfileImageUrl(final ProfileImageFile profileImageFile) {
        if (profileImageFile == null) {
            return "default_profile";
        }

        // TODO: change to AWS S3 URI
        return profileImageFile
                .getBaseImageFile()
                .getStoredFilename();
    }
}
