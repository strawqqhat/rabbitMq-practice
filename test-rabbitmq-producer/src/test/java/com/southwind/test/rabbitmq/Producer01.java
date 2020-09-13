package com.southwind.test.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq的入门程序
 */
public class Producer01 {

    // 队列
    private static final String QUEUE = "hello world";
    public static void main(String[] args) {

        // 通过连接工厂和mq创建连接
        ConnectionFactory connectionFactory = new ConnectionFactory();
        connectionFactory.setHost("127.0.0.1");
        connectionFactory.setPort(5672);
        connectionFactory.setUsername("guest");
        connectionFactory.setPassword("guest");
        // 设置虚拟机，一个mq服务可以设置多个虚拟机，每个虚拟机就相当于一个mq
        connectionFactory.setVirtualHost("/");
        // 建立新连接
        Connection connection = null;
        Channel channel = null;
        try {
            connection = connectionFactory.newConnection();
            // 创建会话通道,生产者和mq服务所有通信都在channel
            channel = connection.createChannel();
            // 声明队列,如果队列在mq中没有则要创建
            // 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
            /**
             * 1. 队列名称
             * 2. durable是否持久化，如果持久化，mq服务重启后队列还在
             * 3. exclusive是否独占连接，队列只允许在该连接中访问，如果连接关闭队列自动删除,如果将此参数设为true可以用于临时队列的创建
             * 4. autoDelete自动删除，队列不再使用时自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列
             * 5. arguments参数，可以设置一个队列的扩展参数，存活时间等
             */
            channel.queueDeclare(QUEUE, true, false, false, null);
            // 发送消息
            // 参数：String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 1. exchange，交换机，如果不指定使用mq默认的交换机,设置为“”
             * 2. routingKey， 路由key来将消息发送到指定的队列,如果使用默认交换机，routingKey设置为队列的名称
             * 3. props，消息的属性
             * 4. body, 消息内容
             */
            String message = "hello world 黑马程序员";
            channel.basicPublish("", QUEUE, null, message.getBytes());
            System.out.println("send to mq" + message);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭连接,先关闭通道，再关闭连接
            try {
                channel.close();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
