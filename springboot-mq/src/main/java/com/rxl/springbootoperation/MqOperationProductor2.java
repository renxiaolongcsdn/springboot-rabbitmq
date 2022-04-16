package com.rxl.springbootoperation;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description java 实现操作 mq 生产者
 */
@Component
public class MqOperationProductor2 {

    @Autowired
    RabbitTemplate rabbitTemplate;

    //第一种模型
    public void sendMessage(){
        rabbitTemplate.convertAndSend("shello","你好");
    }



}
