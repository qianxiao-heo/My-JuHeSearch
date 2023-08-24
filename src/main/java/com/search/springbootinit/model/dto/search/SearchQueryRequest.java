package com.search.springbootinit.model.dto.search;

import com.search.springbootinit.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SearchQueryRequest extends PageRequest implements Serializable {

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 请求类型
     */
    private String type;

    private static final long serialVersionUID = 1L;
}