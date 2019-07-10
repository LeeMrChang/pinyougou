package com.pinyougou;

import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.common.protocol.heartbeat.MessageModel;

import java.util.List;

/**
 * Hello world!
 */
public class App2 {
    public static void main(String[] args)throws Exception {


        //1.创建消费者  并创建消费者组
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group1");

        //2.设置nameserver地址
        consumer.setNamesrvAddr("127.0.0.1:9876");

        //3.设置消费模式 默认就是集群模式   CLUSTERING表示集群模式，还有另外一种叫做广播模式
        consumer.setMessageModel(MessageModel.BROADCASTING);

        //4.设置(订阅)消费主题,并执行消费的 标签  * 表示指定所有标签
        consumer.subscribe("TopicTest","*");//表示只要前面主题的信息，都接收

        //5.设置监听器  同时消费 不需要按住顺序来消费。注册一个监听器
        consumer.registerMessageListener(new MessageListenerConcurrently() {
                //返回一个状态
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                System.out.println("打印当前线程名称222:"+Thread.currentThread().getName());
                if(msgs!=null){
                    //获取消息，遍历消息
                    for (MessageExt msg : msgs) {
                        //获取消息体
                        byte[] body = msg.getBody();
                        //字节数组转字符串
                        String message = new String(body);
                        //打印
                        System.out.println(message+"%s %n"+msg.getTopic()+"%s %n"+msg.getTags());
                    }
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;//没有异常成功消费
                }
                //消费失败
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;//如果出现异常，返回等会儿消费
            }
        });

        //发布开始消费
        consumer.start();
        //收回连接，关闭资源 关闭socket通道,只有系统旦机了才会触发
        //consumer.shutdown();
    }
}
