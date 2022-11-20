package com.eunbinlib.api.domain.user;


import com.eunbinlib.api.domain.imagefile.ProfileImageFile;
import com.eunbinlib.api.domain.post.Post;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DiscriminatorValue("MEMBER")
public class Member extends User {

    private String nickname;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "PROFILE_IMAGE_ID")
    private ProfileImageFile profileImage;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
    private final List<Post> posts = new ArrayList<>();

    @Builder
    public Member(String username, String password, String nickname) {
        super(username, password);
        this.nickname = nickname;
    }

    public void addPost(Post post) {
        this.posts.add(post);
        if (post.getMember() != this) {
            post.setMember(this);
        }
    }
}
