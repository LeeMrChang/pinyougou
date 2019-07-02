package com.lee.dubbo.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.lee.dubbo.service.TestService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName:TestController
 * @Author：Mr.lee
 * @DATE：2019/06/21
 * @TIME： 14:56
 * @Description: TODO
 */
//@Controller
//@RequestMapping("/test")
@RestController   //包含@ResponseBody注解
public class TestController {

    @Reference  //远程注入
    private TestService testService;

    @RequestMapping("/sayhello")
    public String sayHello(){

        return testService.seyHello();
    }

}
