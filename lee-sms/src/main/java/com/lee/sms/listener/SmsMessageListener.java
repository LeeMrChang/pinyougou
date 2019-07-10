package com.lee.sms.listener;

import com.alibaba.fastjson.JSON;

import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.lee.sms.utils.SmsUtils;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.List;
import java.util.Map;

/**
 * @ClassName:SmsMessageListener
 * @Author：Mr.lee
 * @DATE：2019/07/09
 * @TIME： 10:32
 * @Description: TODO
 */
//配置消費著的接受消息類  監聽消息類
public class SmsMessageListener implements MessageListenerConcurrently {


    /**監聽消息
     * 消費者接受消息的方法
     * @param list
     * @param consumeConcurrentlyContext
     * @return
     */
    @Override
    public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> list, ConsumeConcurrentlyContext consumeConcurrentlyContext) {
/**1.監聽消息，獲取消息躰
 * 2.調用阿里大魚 的API 發送短信
 *
 */
        try {
            //判斷消息如果不為空
            if (list != null) {
                //便利消息躰
                for (MessageExt msg : list) {
                    //1。獲取消息躰，是字節，先轉成字符串
                    byte[] body = msg.getBody();
                    String str = new String(body);

                    //2.將字符串轉成map
                    Map map = JSON.parseObject(str,Map.class);

                    System.out.println(map);

                    //3.調用阿里大魚的APi，發送短信
                    //將封裝好數據的map傳入到sms中
                    SendSmsResponse sms = SmsUtils.sendSms(map);
                }
            }

            //消息正常發送的情況
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //如果出現異常
        return ConsumeConcurrentlyStatus.RECONSUME_LATER;
    }
}
