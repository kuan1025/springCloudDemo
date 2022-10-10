package cn.itcast.mq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConsumerApplication.class, args);
    }

    // SpringAMQP 用messageConverter 做序列化和反序列化 用json工具改變預設的序列化方式
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();  //用json工具轉json
    }
}
