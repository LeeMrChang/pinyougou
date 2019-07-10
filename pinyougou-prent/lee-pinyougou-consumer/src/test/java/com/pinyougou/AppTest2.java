package com.pinyougou;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit test for simple App.
 */

@ContextConfiguration("classpath:spring-consumer.xml")  //初始化spring容器
@RunWith(SpringRunner.class)
public class AppTest2 {

   /* @Autowired //注入消费者
    private Defla*/

    @Test
    public void daoMessage()throws Exception{

        Thread.sleep(1000000);
    }

}

