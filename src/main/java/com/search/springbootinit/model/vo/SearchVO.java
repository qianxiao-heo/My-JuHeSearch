package com.search.springbootinit.model.vo;

import com.search.springbootinit.model.entity.Image;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 聚合搜索试图
 *
 
 
 */
@Data
public class SearchVO implements Serializable {

    private List<UserVO> userVOList;

    private List<PostVO> postVOList;

    private List<Image> imageVOList;

    private List<?> dataSource;

    private static final long serialVersionUID = 1L;
}
