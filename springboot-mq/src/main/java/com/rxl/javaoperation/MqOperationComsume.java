package com.rxl.javaoperation;
import com.rabbitmq.client.*;
import com.rxl.config.MqClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeoutException;

/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description  java 实现操作 mq 消费者
 */
@Component
public class MqOperationComsume {

    @Autowired
    MqClient mqClient;


    //第一种模型直连模型 （点对点）
    @Bean
    public void  comsume() throws IOException, TimeoutException {
        Connection conn = mqClient.getConn();
        //获取通道对象
        Channel channel = conn.createChannel();
        //通道绑定对应的消息队列
        channel.queueDeclare("hello",true,false,false,null);
        //消费消息
        //参数1： 消费那个队列
        //参数2 ：开启消费消息自动确认机制   true :开启后将会出现丢失消费数据的情况   false: 一般需要关闭我们的消息自动确认机制
        //参数2 设置为false 后需要设置每次都只能消费一个消息这样还没消费的消息将在我们的队列中  channel.basicQos(1);
        channel.basicQos(1);
        //参数3： 消费时的回调接口
        channel.basicConsume("hello",false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
              Arrays.asList(body).stream().forEach(ite->{
                    System.out.println(new String(ite));
                });
            }
        });

    }


    //====================测试多个消费者消费  begin ===================
    //由于消费者消费后就会删除队列中的任务所以不会担心重复消费的问题
    //默认多个消费者是平均消费的
    //第二种模型 工作队列模型，多个消费者可以进行消费一个工作队列中的内容
    @Bean
    public void comsume2() throws IOException {
        Connection conn = mqClient.getConn();
        //创建通道对象
        Channel channel = conn.createChannel();
        //通道绑定对应的消费队列
        channel.queueDeclare("hello2",true,false,false,null);
        //消费信息
        channel.basicQos(1); //每次只消费一个信息
        channel.basicConsume("hello2",false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                try {
                    Thread.sleep(2000);  //模拟消费延时
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 1===》"+new String(ite));
                });
                //手动确认消息是否消费
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
    }

    //第二种 模型 工作队列模型，多个消费者可以进行消费一个工作队列中的内容
    @Bean
    public void comsume3() throws IOException {
        Connection conn = mqClient.getConn();
        //创建通道对象
        Channel channel = conn.createChannel();
        //通道绑定对应的消费队列
        channel.queueDeclare("hello2",true,false,false,null);
        //消费信息
        channel.basicConsume("hello2",true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 2===》"+new String(ite));
                });
                //由于关闭了自动确认机制，所以需要手动确认
                //参数1： 需要手动确认的一个标识
                //参数2： 是否开启多条消息确认
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
    }

    //====================测试多个消费者消费  end ===================

    //==================测试 第三种模型 广播   begin===================
    @Bean
    public void consume4() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //绑定交换机
        //交换机的作用：通过交换机进行分发到 消费则的临时队列 从而消费者消费临时队列中的信息
        //参数1： 交换机名称
        //参数2L 交换机类型 广播
        channel.exchangeDeclare("log","fanout");
        //创建临时队列
        String queue = channel.queueDeclare().getQueue();
        //将临时队列绑定到交换机上
        //参数1：队列名称
        //参数2：交换机名称
        //参数3： 路由id 对于广播模型没有影响
        channel.queueBind(queue,"log","");
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 1===》"+new String(ite));
                });
            }
        });


    }


    @Bean
    public void consume5() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //绑定交换机
        //交换机的作用：通过交换机进行分发到 消费则的临时队列 从而消费者消费临时队列中的信息
        //参数1： 交换机名称
        //参数2L 交换机类型 广播
        channel.exchangeDeclare("log","fanout");
        //创建临时队列
        String queue = channel.queueDeclare().getQueue();
        //将临时队列绑定到交换机上
        //参数1：队列名称
        //参数2：交换机名称
        //参数3： 路由id 对于广播模型没有影响
        channel.queueBind(queue,"log","");
        //消费消息
        channel.basicConsume(queue,true,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 2===》"+new String(ite));
                });
            }
        });


    }

    //==================测试 第三种模型 广播   end===================



    //==================测试 第四种模型 route 路由 begin===================
    @Bean
    public void consume6() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //绑定交换机
        channel.exchangeDeclare("log2","direct");
        //创建临时队列
        String queue = channel.queueDeclare().getQueue();
        //将临时队列绑定到交换机
        //参数1  队列名   参数2 交换机名   参数3 路由key
        channel.queueBind(queue,"log2","info");
        //消费消息
        channel.basicQos(1); //设置每次消费一个
        //参数1 队列名   参数2 关闭自动确认已经消费消息
        channel.basicConsume(queue,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者route 1===》"+new String(ite));
                });
                //手动确认已经消费完成
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
    }


    @Bean
    public void consume7() throws IOException {
        //建立连接
        Connection conn = mqClient.getConn();
        //创建通道
        Channel channel = conn.createChannel();
        //绑定交换机
        channel.exchangeDeclare("log2","direct");
        //创建临时队列
        String queue = channel.queueDeclare().getQueue();
        //将临时队列绑定到交换机
        //参数1  队列名   参数2 交换机名   参数3 路由key
        channel.queueBind(queue,"log2","info");
        channel.queueBind(queue,"log2","error");
        channel.queueBind(queue,"log2","warn");
        //消费消息
        channel.basicQos(1); //设置每次消费一个
        //参数1 队列名   参数2 关闭自动确认已经消费消息
        channel.basicConsume(queue,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者route 2===》"+new String(ite));
                });
                //手动确认已经消费完成
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });
    }
    //==================测试 第四种模型 route 路由 end===================

    //第五种模型 动态路由
    @Bean
    public void consume8() throws IOException {
        Connection conn = mqClient.getConn();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare("topics","topic");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue,"topics","user.*");
        channel.basicQos(1);
        channel.basicConsume(queue,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 动态route 1===》"+new String(ite));
                });
                //手动确认已经消费完成
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });

    }

    @Bean
    public void consume9() throws IOException {
        Connection conn = mqClient.getConn();
        Channel channel = conn.createChannel();
        channel.exchangeDeclare("topics","topic");
        String queue = channel.queueDeclare().getQueue();
        channel.queueBind(queue,"topics","user.#");
        channel.basicQos(1);
        channel.basicConsume(queue,false,new DefaultConsumer(channel){
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                Arrays.asList(body).stream().forEach(ite->{
                    System.out.println("消费者 动态route 2===》"+new String(ite));
                });
                //手动确认已经消费完成
                channel.basicAck(envelope.getDeliveryTag(),false);
            }
        });

    }


}
