package com.eunbinlib.api.application.domain.repository.postimagefile;

import com.eunbinlib.api.application.domain.imagefile.PostImageFile;

import java.util.List;

public interface PostImageFileRepositoryCustom {

    List<PostImageFile> getList(Long limit, Long afterCond);

    boolean existsNext(Long id);

}
