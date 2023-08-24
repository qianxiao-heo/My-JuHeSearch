package com.search.springbootinit.datasoure;

import com.search.springbootinit.model.enums.SearchTypeEnum;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Component
public class DataSourceRegistry {
    @Resource
    private ImageDataSource imageDataSource;

    @Resource
    private PostDataSource postDataSource;

    @Resource
    private UserDataSource userDataSource;

    private Map<String, DataSource<T>> typeDataSourceMap;

    @PostConstruct
    public void doInit(){
        typeDataSourceMap=new HashMap(){{
            put(SearchTypeEnum.POST.getValue(),postDataSource);
            put(SearchTypeEnum.USER.getValue(),userDataSource);
            put(SearchTypeEnum.PICTURE.getValue(),imageDataSource);
        }};
    }

    public DataSource<T> getDataSourceType(String type){
        if (typeDataSourceMap==null){
            return null;
        }
        return typeDataSourceMap.get(type);
    }
}
