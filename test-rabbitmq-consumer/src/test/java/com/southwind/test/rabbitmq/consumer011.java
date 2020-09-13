package com.southwind.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序消费者
 */
public class consumer011 {

    private static final String QUEUE = "hello world";

    public static void main(String[] args) throws IOException, TimeoutException {
        // 通过连接工厂和mq创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // 设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个mq
        connectionFactory.setVirtualHost("/");
        // 建立新连接
        Connection connection = connectionFactory.newConnection();
        // 创建会话通道
        Channel channel = connection.createChannel();

        // 监听队列
        /**
         * 参数明细
         * String queue, boolean autoAck, Consumer callback
         * 1. 队列名称
         * 2. 自动回复,当消费者接收到消息后要告诉mq消息已接收，如果将此参数设为true表示会自动回复，否则需要编程实现回复
         * 3. 消费方法，当消费者接收到消息后要执行的方法
         */
        channel.queueDeclare(QUEUE, true, false, false, null);

        // 实现消费方法
        DefaultConsumer defaultConsumer = new DefaultConsumer(channel){
            // 接收到消息后此方法将被调用

            /**
             *
             * @param consumerTag 消费者标签，标识消费者，在监听队列时设置
             * @param envelope 信封
             * @param properties 属性
             * @param body  消息内容
             * @throws IOException
             */
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                String exchange = envelope.getExchange();
                long deliveryTag = envelope.getDeliveryTag();

                String message = new String(body, "utf-8");
                System.out.println("receive message: " + message);
            }
        };
        channel.basicConsume(QUEUE, true, defaultConsumer);
    }
}
