package com.haojg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haojg.entity.Demo;

import java.util.List;

public interface DemoService extends IService<Demo> {

    boolean save(Demo demo);
    List<Demo> getDemoList();
}
