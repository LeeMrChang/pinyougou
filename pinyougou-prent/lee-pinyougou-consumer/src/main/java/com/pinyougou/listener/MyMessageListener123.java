package com.pinyougou.listener;

import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;

/**
 * @ClassName:MyMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/07
 * @TIME： 15:09
 * @Description: TODO
 */
public class MyMessageListener123 implements MessageListenerConcurrently {

    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {

        try {
            //判断如果消息体不等于空,则遍历
            if (list != null) {

                for (MessageExt messageExt : list) {
                    //1.获取消息体
                    System.out.println("topic"+messageExt.getTopic()+";tag"+messageExt.getTags()+";keys:"+messageExt.getKeys());

                    //2.获取到消息体的字符串
                    byte[] body = messageExt.getBody();
                    //字节数组需要转换成字符串
                    String s = new String(body);
                    //3.打印
                    System.out.println(s);
                }
            }
            //如果正常，则消费成功
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
            //如果出现异常，请稍等一会儿再消费
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }

}
