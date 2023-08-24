package com.search.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.search.springbootinit.common.BaseResponse;
import com.search.springbootinit.common.ErrorCode;
import com.search.springbootinit.common.ResultUtils;
import com.search.springbootinit.exception.ThrowUtils;
import com.search.springbootinit.model.entity.Image;
import com.search.springbootinit.model.dto.image.ImageQueryRequest;
import com.search.springbootinit.service.ImageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 图片接口
 *
 */
@RestController
@RequestMapping("/post")
@Slf4j
public class ImageController {

    @Resource
    private ImageService imageService;

    private final static Gson GSON = new Gson();

    /**
     * 分页获取列表（封装类）
     *
     * @param imageQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/pic/vo")
    public BaseResponse<Page<Image>> listImageByPage(@RequestBody ImageQueryRequest imageQueryRequest,
                                                     HttpServletRequest request) {
        long current = imageQueryRequest.getCurrent();
        long size = imageQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        String searchText=imageQueryRequest.getSearchText();
        Page<Image> imagePage = imageService.searchImage(searchText, current, size);
        return ResultUtils.success(imagePage);
    }
}
