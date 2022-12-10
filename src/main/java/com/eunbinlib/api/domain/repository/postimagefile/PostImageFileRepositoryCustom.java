package com.eunbinlib.api.domain.repository.postimagefile;

import com.eunbinlib.api.domain.imagefile.PostImageFile;

import java.util.List;

public interface PostImageFileRepositoryCustom {

    List<PostImageFile> getList(Long limit, Long afterCond);

    boolean existsNext(Long id);

}
