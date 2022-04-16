package com.rxl.facotry;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.util.ObjectUtils;

/**
 * @author ren.xiaolong
 * @date 2022/4/15
 * @Description 创建mq工厂使用懒汉模式 可以防止重复创建
 */
public class MqFactory extends ConnectionFactory {

    //创建私有对象
    private static MqFactory mqFactory;

    //建立私有构造函数
    private MqFactory() {

    }

    public static MqFactory getInstance() {
        //确保多线程情况下线程安全
        synchronized (MqFactory.class){
            if (ObjectUtils.isEmpty(mqFactory)) {
                mqFactory = new MqFactory();
            }
            return mqFactory;
        }
    }
}
