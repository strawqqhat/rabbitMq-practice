package com.southwind.test.rabbitmq;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * rabbitmq的入门程序
 */
public class Producer03_routing {

    // 两个队列和交换机
    private static final String QUEUE_INFORM_EMAIL = "QUEUE_INFORM_EMAIL";
    private static final String QUEUE_INFORM_SMS = "QUEUE_INFORM_SMS";
    private static final String EXCHANGE_ROUTING_INFORM = "EXCHANGE_ROUTING_INFORM";
    private static final String ROUTINGKEY_EMAIL = "inform_email";
    private static final String ROUTINGKEY_SMS = "inform_sms";

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
            channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
            channel.queueDeclare(QUEUE_INFORM_SMS, true, false, false, null);
            // 声明交换机
            /**
             * 参数：String exchange, String type
             * 1. 交换机名称
             * 2. 交换机类型
             * fanout:对应的rabbitMQ的工作模式是publish/subscribe
             * direct:对应路由模式
             * topic:对应Topics工作模式
             * headers:对应headers工作模式
             */
            channel.exchangeDeclare(EXCHANGE_ROUTING_INFORM, BuiltinExchangeType.DIRECT);
            // 交换机和队列进行绑定 String queue, String exchange, String routingKey
            /**
             * 参数：
             * 1. 队列名称
             * 2. 交换机名称
             * 3. 路由key,作用是交换机根据路由key的值将消息转发到指定队列中，在发布/订阅模式中为空字符串
             */
            channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL);
            channel.queueBind(QUEUE_INFORM_SMS, EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS);
            // 发送消息
            // 参数：String exchange, String routingKey, BasicProperties props, byte[] body
            /**
             * 1. exchange，交换机，如果不指定使用mq默认的交换机,设置为“”
             * 2. routingKey， 路由key来将消息发送到指定的队列,如果使用默认交换机，routingKey设置为队列的名称
             * 3. props，消息的属性
             * 4. body, 消息内容
             */
            for(int i = 0; i < 5; i++) {
                // 发送消息指定routingkey
                String message = "send inform message to user";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_EMAIL, null, message.getBytes());
                System.out.println("send to mq" + message);
            }

            for(int i = 0; i < 5; i++) {
                // 发送消息指定routingkey
                String message = "send inform message to user";
                channel.basicPublish(EXCHANGE_ROUTING_INFORM, ROUTINGKEY_SMS, null, message.getBytes());
                System.out.println("send to mq" + message);
            }


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
