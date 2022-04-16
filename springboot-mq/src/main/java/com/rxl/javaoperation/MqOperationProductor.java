package com.rxl.javaoperation;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rxl.config.MqClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description java 实现操作 mq 生产者
 */
@Component
public class MqOperationProductor {


    @Autowired
    MqClient mqClient;

    //第一种模型直连模型 （点对点）   一次只能有一个消费者进行消费
    //生产消息
    public void SendMessage() throws IOException, TimeoutException {
        Connection conn = mqClient.getConn();
        //获取连接中的通道对象
        Channel channel = conn.createChannel();
        //通道绑定对应的消息队列
        //参数1 ：队列名 会自动在mq 界面常创建
        //参数2 ：是否持久化 true :代表会持久化mq下次重启后这个队列还会存在 false : 不会持久化mq下次重启后队列将会消失
        //参数3 ：是否独占队列  true:代表当前队列除了当前任务不允许别的任务使用该队列  false:代表当前队列除了当前任务允许别的任务使用该队列
        //参数4 : 是否在消费完成后并断开监听消费 才会 删除该队列
        //参数5 ： 额外附加参数
        channel.queueDeclare("hello", true, false, false, null);
        //发布消息
        //参数1 ： 交换机名称
        //参数2 ： 队列名
        //参数3 : 传递消息的额外设置 MessageProperties.PERSISTENT_BASIC 代表重启mq 时消息不会丢失
        //参数4 ： 消息的内容
        channel.basicPublish("", "hello", MessageProperties.PERSISTENT_BASIC, "hello mq".getBytes());
        //关闭通道
        channel.close();
        //关闭连接
        conn.close();
    }



    //第二种模型 工作队列模型，多个消费 可以进行消费
    public void sendMessage2() throws IOException, TimeoutException {
        //获取连接对象
        Connection conn = mqClient.getConn();
        //获取通道对象
        Channel channel = conn.createChannel();
        //通道对象进行绑定队列
        channel.queueDeclare("hello2",true,false,false,null);
        //发布消息到队列中
        for (int i = 0; i < 10; i++) {
            channel.basicPublish("","hello2",MessageProperties.PERSISTENT_BASIC,("hello mq"+i).getBytes());
        }
        //关闭通道
        channel.close();
        //关闭连接
        conn.close();
    }



    //第三种模型  广播模型
    //将消息发送到我们的交换机
    public void sendMessage3() throws IOException {
        //获取连接对象
        Connection conn = mqClient.getConn();
        //获取通道对象
        Channel channel = conn.createChannel();
        //声明交换机
        //参数1： 交换机名称
        //惨呼2： 交换机类型
        channel.exchangeDeclare("log","fanout");
        //发送消息到交换机
        for (int i = 0; i < 2; i++) {
            channel.basicPublish("log","",null,("hello"+i).getBytes());
        }
    }


    //第四种模型 路由模型   ===begin=============
    //指定部分消费者可以进行消费
    public void sendMessage4() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明交换机
        channel.exchangeDeclare("log2","direct");
        //声明路由key
        String routeKey="info";
        //生产消息到交换机
        channel.basicPublish("log2",routeKey,MessageProperties.PERSISTENT_BASIC,"hello route info".getBytes());
    }

    public void sendMessage5() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //声明交换机
        channel.exchangeDeclare("log2","direct");
        //声明路由key
        String routeKey="error";
        //生产消息到交换机
        channel.basicPublish("log2",routeKey,MessageProperties.PERSISTENT_BASIC,"hello route error".getBytes());
    }

    //第四种模型 路由模型   ===end=============


    //第五中模型 动态路由
    public void sendMessage6() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare("topics","topic");
        String routekey="user.find.query";
        channel.basicPublish("topics",routekey,MessageProperties.PERSISTENT_BASIC,"hello 动态路由".getBytes());
    }

}
