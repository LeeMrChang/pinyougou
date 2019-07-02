package com.lee.dubbo.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.lee.dubbo.service.TestService;



/**
 * @ClassName:TestServiceImpl
 * @Author：Mr.lee
 * @DATE：2019/06/21
 * @TIME： 11:57
 * @Description: TODO
 */
@Service  //可不用
public class TestServiceImpl implements TestService {

    @Override
    public String seyHello() {
        return "Hello!";
    }
}
