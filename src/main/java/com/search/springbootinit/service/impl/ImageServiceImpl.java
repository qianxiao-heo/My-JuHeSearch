package com.search.springbootinit.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.search.springbootinit.common.ErrorCode;
import com.search.springbootinit.exception.BusinessException;
import com.search.springbootinit.model.entity.Image;
import com.search.springbootinit.service.ImageService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ImageServiceImpl implements ImageService {

    @Override
    public Page<Image> searchImage(String searchText, long pageNum, long pageSize) {
        long current= (pageNum-1) * pageSize;
        if(StringUtils.isBlank(searchText)){
            searchText="图片";
        }
        String url=String.format("https://cn.bing.com/images/search?q=%s&form=HDRSC2&first=%d",searchText,current);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据获取异常");
        }
        Elements elements = doc.select(".iuscp.isv");
        List<Image> imageList= new ArrayList<>();
        for (Element element : elements){
            // 取图片地址
            String m = element.select(".iusc").get(0).attr("m");
            Map imgUrl= JSONUtil.toBean(m,Map.class);
            String mUrl=(String) imgUrl.get("murl");//真实图片地址
            String turl=(String) imgUrl.get("turl");//裁剪图片地址
            //取标题
            String title=element.select(".inflnk").get(0).attr("aria-label");
            Image image=new Image();
            image.setTitle(title);
            image.setUrl(mUrl);
            image.setTUrl(turl);
            imageList.add(image);
            if (imageList.size() >= pageSize){
                break;
            }
        }
        Page<Image> imagePage=new Page<>(pageNum,pageSize);
        imagePage.setRecords(imageList);
        return imagePage;
    }
}
