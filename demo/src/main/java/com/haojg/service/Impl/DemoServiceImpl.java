package com.haojg.service.Impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haojg.entity.Demo;
import com.haojg.mapper.DemoMapper;
import com.haojg.service.DemoService;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class DemoServiceImpl extends ServiceImpl<DemoMapper, Demo> implements DemoService {
    @Override
    public boolean save(Demo entity) {
        return super.save(entity);
    }

    @Override
    public List<Demo> getDemoList() {
        return baseMapper.selectList(Wrappers.<Demo>lambdaQuery());
    }

}
