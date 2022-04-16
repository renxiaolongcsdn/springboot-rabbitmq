package com.rxl.config;

import com.rabbitmq.client.Connection;
import com.rxl.facotry.MqFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description
 */
@Component
public class MqClient {

    //@Value("${mq.host}")
    @Value("${spring.rabbitmq.host}")
    String host;

    //@Value("${mq.port}")
    @Value("${spring.rabbitmq.port}")
    String port;

    //@Value("${mq.pwd}")
    @Value("${spring.rabbitmq.password}")
    String pwd;

    //@Value("${mq.userName}")
    @Value("${spring.rabbitmq.username}")
    String userName;

    //@Value("${mq.virtualHost}")
    @Value("${spring.rabbitmq.virtual-host}")
    String virtualHost;

    //创建连接
    @Bean
    public Connection getConn() {
        //创建连接
        try {
            //创建mq连接工厂对象
            MqFactory connectionFactory = MqFactory.getInstance();
            connectionFactory.setHost(host);
            connectionFactory.setPort(Integer.parseInt(port));
            connectionFactory.setUsername(userName);
            connectionFactory.setPassword(pwd);
            connectionFactory.setVirtualHost(virtualHost);
            Connection connection = connectionFactory.newConnection();
            return ObjectUtils.isEmpty(connection) ? null : connection;
        } catch (Exception e) {
            throw new RuntimeException("mq 客户端连接失败！"+e.getMessage());
        }

    }
}
