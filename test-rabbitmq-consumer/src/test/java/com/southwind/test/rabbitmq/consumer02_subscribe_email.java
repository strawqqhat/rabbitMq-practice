package com.southwind.test.rabbitmq;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 入门程序消费者
 */
public class consumer02_subscribe_email {

    private static final String QUEUE_INFORM_EMAIL = "QUEUE_INFORM_EMAIL";
    private static final String EXCHANGE_FANOUT_INFORM = "EXCHANGE_FANOUT_INFORM";

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

        // 参数：String queue, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        /**
         * 1. 队列名称
         * 2. durable是否持久化，如果持久化，mq服务重启后队列还在
         * 3. exclusive是否独占连接，队列只允许在该连接中访问，如果连接关闭队列自动删除,如果将此参数设为true可以用于临时队列的创建
         * 4. autoDelete自动删除，队列不再使用时自动删除此队列，如果将此参数和exclusive参数设置为true就可以实现临时队列
         * 5. arguments参数，可以设置一个队列的扩展参数，存活时间等
         */
        channel.queueDeclare(QUEUE_INFORM_EMAIL, true, false, false, null);
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
        channel.exchangeDeclare(EXCHANGE_FANOUT_INFORM, BuiltinExchangeType.FANOUT);
        // 交换机和队列进行绑定 String queue, String exchange, String routingKey
        /**
         * 参数：
         * 1. 队列名称
         * 2. 交换机名称
         * 3. 路由key,作用是交换机根据路由key的值将消息转发到指定队列中，在发布/订阅模式中为空字符串
         */
        channel.queueBind(QUEUE_INFORM_EMAIL, EXCHANGE_FANOUT_INFORM, "");

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
        channel.basicConsume(QUEUE_INFORM_EMAIL, true, defaultConsumer);
    }
}
