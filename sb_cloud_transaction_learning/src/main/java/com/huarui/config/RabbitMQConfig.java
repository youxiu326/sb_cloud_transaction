package com.huarui.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ配置
 * Created by youxiu326 on 2019/3/28.
 */
@Configuration
public class RabbitMQConfig {

    //添加选课任务交换机
    public static final String LEARNING_EXCHANGE = "learning_exchange";

    //添加选课消息队列
    public static final String LEARNING_ADD_QUEUE = "learning_add_queue";

    //完成添加选课消息队列
    public static final String LEARNING_ADD_FINISH_QUEUE = "learning_add_finish_queue";

    //添加选课路由key
    public static final String LEARNING_ADD__KEY = "learning_add_key";

    //完成添加选课路由key
    public static final String LEARNING_ADD_FINISH__KEY = "learning_add_finish_key";

    //交互机配置
    @Bean(LEARNING_EXCHANGE)
    public Exchange EX_DECLARE() {
        return ExchangeBuilder.directExchange(LEARNING_EXCHANGE).durable(true).build();
    }

    //声明添加选课队列
    @Bean(LEARNING_ADD_QUEUE)
    public Queue QUEUE_ADD() {
        Queue queue = new Queue(LEARNING_ADD_QUEUE,true,false,true);
        return queue;
    }

    //声明添加选课完成队列
    @Bean(LEARNING_ADD_FINISH_QUEUE)
    public Queue QUEUE_ADD_FINISH() {
        Queue queue = new Queue(LEARNING_ADD_FINISH_QUEUE,true,false,true);
        return queue;
    }

    /**
     * 绑定添加选课队列到交换机 .
     */
    @Bean
    public Binding binding_queue_add_processtask(@Qualifier(LEARNING_ADD_QUEUE) Queue queue, @Qualifier(LEARNING_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LEARNING_ADD__KEY).noargs();
    }

    /**
     * 绑定添加选课完成队列到交换机 .
     */
    @Bean
    public Binding binding_queue_finish_processtask(@Qualifier(LEARNING_ADD_FINISH_QUEUE) Queue queue, @Qualifier(LEARNING_EXCHANGE) Exchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(LEARNING_ADD_FINISH__KEY).noargs();
    }

}
