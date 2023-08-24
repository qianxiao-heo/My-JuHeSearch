package com.search.springbootinit.datasoure;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.search.springbootinit.model.dto.user.UserQueryRequest;
import com.search.springbootinit.model.vo.UserVO;
import com.search.springbootinit.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 用户服务实现
 *
 
 
 */
@Service
@Slf4j
public class UserDataSource implements DataSource<UserVO> {

    @Resource
    private UserService userService;

    @Override
    public Page<UserVO> doSearch(String SearchText, long pageNum, long pageSize) {
        UserQueryRequest userQueryRequest=new UserQueryRequest();
        userQueryRequest.setUserName(SearchText);
        userQueryRequest.setCurrent(pageNum);
        userQueryRequest.setPageSize(pageSize);
        Page<UserVO> userVOPage = userService.listUserByPage(userQueryRequest);
        return userVOPage;
    }
}
