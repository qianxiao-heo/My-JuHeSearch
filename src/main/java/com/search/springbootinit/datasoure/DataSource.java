package com.search.springbootinit.datasoure;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface DataSource <T>{

    /**
     * 搜索接口规范
     *
     * @param SearchText 搜索关键词
     * @param pageNum 页数
     * @param pageSize 大小
     * @return T
     */
    Page<T> doSearch(String SearchText, long pageNum, long pageSize);
}
