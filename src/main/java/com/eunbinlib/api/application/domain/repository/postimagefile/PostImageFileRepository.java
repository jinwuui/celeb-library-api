package com.eunbinlib.api.application.domain.repository.postimagefile;

import com.eunbinlib.api.application.domain.imagefile.PostImageFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostImageFileRepository extends JpaRepository<PostImageFile, Long>, PostImageFileRepositoryCustom {

    Optional<List<PostImageFile>> findAllByPostId(Long postId);

}
