package com.search.springbootinit;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.search.springbootinit.model.entity.Image;
import com.search.springbootinit.model.entity.Post;
import com.search.springbootinit.service.PostService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@SpringBootTest
public class CrawLerTest {

    @Resource
    private PostService postService;

    @Test
    void testFetch() {
//        1.获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url="https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute()
                .body();
//        2.Json转对象
        Map map= JSONUtil.toBean(result,Map.class);
        JSONObject data = (JSONObject) map.get("data");
        JSONArray records=(JSONArray) data.get("records");
        List<Post> postList=new ArrayList<>();
        for (Object record : records){
            JSONObject temp =(JSONObject) record;
            Post post=new Post();
            post.setTitle(temp.getStr("title"));
            post.setContent(temp.getStr("content"));
            JSONArray tags=(JSONArray) temp.get("tags");
            List<String> tagList=tags.toList(String.class);
            post.setTags(JSONUtil.toJsonStr(tagList));
            post.setUserId(Long.parseLong(temp.getStr("userId")));
            post.setThumbNum(Integer.parseInt(temp.getStr("thumbNum")));
            post.setFavourNum(Integer.parseInt(temp.getStr("favourNum")));
            postList.add(post);
        }
//        3.导入数据库
        boolean b = postService.saveBatch(postList);
        Assertions.assertTrue(b);
    }
    @Test
    void fetchImg() throws IOException {
        String current="1";
        String url=String.format("https://cn.bing.com/images/search?q=小黑子&form=HDRSC2&first=%s",current);
        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select(".iuscp.isv");
        List<Image> imageList= new ArrayList<>();
        for (Element element : elements){
            // 取图片地址
            String m = element.select(".iusc").get(0).attr("m");
            Map imgUrl= JSONUtil.toBean(m,Map.class);
            String mUrl=(String) imgUrl.get("murl");
            //取标题
            String title=element.select(".inflnk").get(0).attr("aria-label");
            Image image=new Image();
            image.setTitle(mUrl);
            image.setUrl(mUrl);
            imageList.add(image);
        }
        System.out.println(imageList);
    }
}
