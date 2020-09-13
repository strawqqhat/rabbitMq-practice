package com.southwind.test.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import rabbitmq.config.RabbitmqConfig;


/**
 * rabbitmq的入门程序
 */

@SpringBootTest
@RunWith(SpringRunner.class)
public class Producer05_topics_springboot {

    @Autowired
    RabbitTemplate rabbitTemplate;

    // 使用rabbitTemplate发送消息
    @Test
    public void testSendEmail(){
        String message = "send email messgae to user";
        /**
         * 参数
         * 1. 交换机名称
         * 2. routingKey
         * 3. message
         */
        rabbitTemplate.convertAndSend(RabbitmqConfig.EXCHANGE_TOPICS_INFORM,"inform.email",message);

    }

}
