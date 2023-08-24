package com.search.springbootinit.job.once;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.search.springbootinit.model.entity.Post;
import com.search.springbootinit.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//若@Component取消注释，将作为组件注册到SpringBoot，项目运行时运行一次
//@Component
@Slf4j
public class GetPostData implements CommandLineRunner {

    @Resource
    private PostService postService;

    @Override
    public void run(String... args) {
        // 1.获取数据
        String json = "{\"current\":1,\"pageSize\":8,\"sortField\":\"createTime\",\"sortOrder\":\"descend\",\"category\":\"文章\",\"reviewStatus\":1}";
        String url="https://www.code-nav.cn/api/post/search/page/vo";
        String result = HttpRequest.post(url)
                .body(json)
                .execute()
                .body();
        // 2.Json转对象
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
        // 3.导入数据库
        boolean saveResult = postService.saveBatch(postList);
        // 4. 打印日志
        if (saveResult){
            log.info("成功获取帖子条数{}",postList.size());
        }else {
            log.error("获取帖子失败");
        }
    }
}
