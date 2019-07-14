package com.haojg.controller;

import com.haojg.entity.Demo;
import com.haojg.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Classname DemoController
 * @Description 用户测试控制类
 * @Author 李号东 lihaodongmail@163.com
 * @Date 2019-05-26 17:36
 * @Version 1.0
 */
@RestController
public class DemoController {

    @Autowired
    private DemoService demoService;

    @GetMapping("/select")
    public List<Demo> select() {
        return demoService.getDemoList();
    }

    @GetMapping("/insert")
    public Boolean insert(Demo Demo) {
        return demoService.save(Demo);
    }

}
