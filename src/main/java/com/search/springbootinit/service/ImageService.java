package com.search.springbootinit.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.search.springbootinit.model.entity.Image;

/**
 * 图片服务
 *
 */
public interface ImageService {

    Page<Image> searchImage(String searchText, long pageNum, long pageSize);

}
