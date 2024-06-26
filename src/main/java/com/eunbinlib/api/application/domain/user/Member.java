package com.eunbinlib.api.application.domain.user;


import com.eunbinlib.api.application.domain.imagefile.ProfileImageFile;
import com.eunbinlib.api.application.domain.post.Post;
import com.eunbinlib.api.application.domain.postlike.PostLike;
import com.eunbinlib.api.application.domain.block.Block;
import com.eunbinlib.api.application.domain.imagefile.BaseImageFile;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class Member extends User {

    @NotNull(message = "닉네임은 필수입니다.")
    @Embedded
    private Nickname nickname;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PROFILE_IMAGE_FILE_ID")
    private ProfileImageFile profileImageFile;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<PostLike> postLikes = new ArrayList<>();

    @OneToMany(mappedBy = "blocker", fetch = FetchType.LAZY)
    private final List<Block> blocks = new ArrayList<>();

    @Builder
    public Member(final String username, final String password, final String nickname) {
        super(username, password);
        this.nickname = new Nickname(nickname);
    }

    public void addPost(final Post post) {
        this.posts.add(post);
        if (post.getMember() != this) {
            post.setMember(this);
        }
    }

    public void update(final String nickname, final BaseImageFile baseImageFile) {
        this.nickname = nickname != null ? new Nickname(nickname) : this.nickname;
        this.profileImageFile = baseImageFile != null
                ? ProfileImageFile.builder()
                        .baseImageFile(baseImageFile)
                        .member(this)
                        .build()
                : this.profileImageFile;
    }
}
