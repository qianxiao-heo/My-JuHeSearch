package com.search.springbootinit.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.search.springbootinit.common.BaseResponse;
import com.search.springbootinit.common.ErrorCode;
import com.search.springbootinit.common.ResultUtils;
import com.search.springbootinit.datasoure.*;
import com.search.springbootinit.exception.BusinessException;
import com.search.springbootinit.model.dto.post.PostQueryRequest;
import com.search.springbootinit.model.dto.user.UserQueryRequest;
import com.search.springbootinit.model.entity.Image;
import com.search.springbootinit.model.enums.SearchTypeEnum;
import com.search.springbootinit.model.vo.PostVO;
import com.search.springbootinit.model.vo.UserVO;
import com.search.springbootinit.model.dto.search.SearchQueryRequest;
import com.search.springbootinit.model.vo.SearchVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;

/**
 * 图片接口
 */
@RestController
@RequestMapping("/search")
@Slf4j
public class SearchController {

    @Resource
    private ImageDataSource imageDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    @Resource
    private DataSourceRegistry dataSourceRegistry;
    /**
     * 聚合搜索
     *
     * @param searchQueryRequest searchQueryRequest
     * @param request request
     * @return BaseResponse<SearchVO>
     */
    @PostMapping("/all")
    public BaseResponse<SearchVO> searchAll(@RequestBody SearchQueryRequest searchQueryRequest, HttpServletRequest request) {
        String searchText = searchQueryRequest.getSearchText();
        long pageNum = searchQueryRequest.getCurrent();//获取前端传回分页页数
        long pageSize = searchQueryRequest.getPageSize();//获取前端传回页面大小

        String type = searchQueryRequest.getType();
        SearchTypeEnum searchTypeEnum = SearchTypeEnum.getEnumByValue(type);
//        ThrowUtils.throwIf(StringUtils.isBlank(type), ErrorCode.PARAMS_ERROR);
        if (searchTypeEnum == null) {
            // CompletableFuture并发实现
            CompletableFuture<Page<UserVO>> userFuture = CompletableFuture.supplyAsync(() -> {
                UserQueryRequest userQueryRequest = new UserQueryRequest();
                userQueryRequest.setUserName(searchText);
                Page<UserVO> userPage = userDataSource.doSearch(searchText,pageNum,pageSize);
                return userPage;
            });

            CompletableFuture<Page<PostVO>> postFuture = CompletableFuture.supplyAsync(() -> {
                PostQueryRequest postQueryRequest = new PostQueryRequest();
                postQueryRequest.setSearchText(searchText);
                Page<PostVO> postPage = postDataSource.doSearch(searchText,pageNum,pageSize);
                return postPage;
            });
            CompletableFuture<Page<Image>> imageFuture = CompletableFuture.supplyAsync(() -> {
                Page<Image> imagePage = imageDataSource.doSearch(searchText,pageNum,pageSize);
                return imagePage;
            });
            //并发执行完成后，才能执行下面代码。缺点：存在短板效应
            CompletableFuture.allOf(userFuture, postFuture, imageFuture).join();
            try {
                Page<UserVO> userVOPage = userFuture.get();
                Page<PostVO> postVOPage = postFuture.get();
                Page<Image> imagePage = imageFuture.get();
                SearchVO searchVO = new SearchVO();
                searchVO.setImageVOList(imagePage.getRecords());
                searchVO.setPostVOList(postVOPage.getRecords());
                searchVO.setUserVOList(userVOPage.getRecords());
                return ResultUtils.success(searchVO);
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "获取数据异常");
            }
        } else {
            SearchVO searchVO = new SearchVO();
            DataSource<?> dataSource=dataSourceRegistry.getDataSourceType(type);
            Page<?> page = dataSource.doSearch(searchText, pageNum, pageSize);
            searchVO.setDataSource(page.getRecords());
            return ResultUtils.success(searchVO);
        }
    }
}
