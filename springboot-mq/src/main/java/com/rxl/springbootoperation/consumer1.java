package com.rxl.springbootoperation;

import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author ren.xiaolong
 * @date 2022/4/16
 * @Description
 */
@Component
@RabbitListener(queuesToDeclare = @Queue("shello"))
public class consumer1 {


    //@RabbitHandler 声明是一个回调函数
    @RabbitHandler
    public void receive1(String message){
        System.out.println("message = " + message);
    }
}
