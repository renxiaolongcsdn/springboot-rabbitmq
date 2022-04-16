package com.rxl.controller;

import com.rxl.javaoperation.MqOperationComsume;
import com.rxl.javaoperation.MqOperationProductor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description
 */
@RestController
public class JavaOperationController {

    @Autowired
    MqOperationProductor mqOperationProductor;

    @Autowired
    MqOperationComsume mqOperationComsume;



    //模拟单消费者消费
    @GetMapping("/test")
    public void test() throws IOException, TimeoutException {
        mqOperationProductor.SendMessage();
    }


    //模拟多消费者发送消息
    @GetMapping("/test2")
    public void test2() throws IOException, TimeoutException {
        mqOperationProductor.sendMessage2();
    }

    //模拟第三种模型 广播
    @GetMapping("/test3")
    public void test3() throws IOException, TimeoutException {
        mqOperationProductor.sendMessage3();
    }

    //模拟第四种模型 路由
    @GetMapping("/test4")
    public void test4() throws IOException, TimeoutException {
        mqOperationProductor.sendMessage4();
    }
    //模拟第5种模型 路由
    @GetMapping("/test5")
    public void test5() throws IOException, TimeoutException {
        mqOperationProductor.sendMessage5();
    }

    //模拟第5种模型 动态路由
    @GetMapping("/test6")
    public void test6() throws IOException, TimeoutException {
        mqOperationProductor.sendMessage6();
    }


}
