package com.eunbinlib.api.domain.user;

import com.eunbinlib.api.domain.imagefile.BaseImageFile;
import com.eunbinlib.api.domain.post.Post;
import com.eunbinlib.api.exception.type.EunbinlibIllegalArgumentException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class MemberTest {

    @Test
    @DisplayName("닉네임 없이 회원 객체를 생성할 경우")
    void createMemberWithoutNickname() {
        // expected
        assertThatThrownBy(() -> Member.builder().build())
                .isInstanceOf(EunbinlibIllegalArgumentException.class);
    }

    @Test
    @DisplayName("회원에 게시글 추가")
    void addPost() {
        // given
        Member member = Member.builder()
                .nickname("닉네임")
                .build();
        Post post = Post.builder().build();

        // when
        member.addPost(post);

        // then
        assertThat(member.getPosts().contains(post))
                .isTrue();
        assertThat(post.getMember())
                .isEqualTo(member);
    }

    @Test
    @DisplayName("회원 정보에서 닉네임만 수정하는 경우")
    void updateNickname() {
        // given
        Member member = Member.builder()
                .nickname("닉네임")
                .build();

        // when
        member.update("수정된 닉네임", null);

        // then
        assertThat(member.getNickname().getValue())
                .isEqualTo("수정된 닉네임");
        assertThat(member.getProfileImageFile())
                .isNull();
    }

    @Test
    @DisplayName("회원 정보에서 프로필만 수정하는 경우")
    void updateProfileImageFile() {
        // given
        Member member = Member.builder()
                .nickname("닉네임")
                .build();
        BaseImageFile baseImageFile = BaseImageFile.builder().build();

        // when
        member.update(null, baseImageFile);

        // then
        assertThat(member.getNickname().getValue())
                .isEqualTo("닉네임");
        assertThat(member.getProfileImageFile().getBaseImageFile())
                .isEqualTo(baseImageFile);
    }

    @Test
    @DisplayName("회원 정보에서 닉네임과 프로필 둘 다 수정하는 경우")
    void updateNicknameAndProfileImageFile() {
        // given
        Member member = Member.builder()
                .nickname("닉네임")
                .build();
        BaseImageFile baseImageFile = BaseImageFile.builder().build();

        // when
        member.update("수정된 닉네임", baseImageFile);

        // then
        assertThat(member.getNickname().getValue())
                .isEqualTo("수정된 닉네임");
        assertThat(member.getProfileImageFile().getBaseImageFile())
                .isEqualTo(baseImageFile);
    }
}
