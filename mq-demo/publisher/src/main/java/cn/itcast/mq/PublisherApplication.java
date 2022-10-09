package cn.itcast.mq;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PublisherApplication {
    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class);
    }

    //为什么要在发送者方声明配置？因为发消息的序列化是生产者行为
    //为什么在主启动类里声明该配置？因为主启动类也是配置类：
        //一个@SpringBootApplication就包含了以下三个注解：
        //@Configuration(@SpringBootConfiguration实质就是一个@Configuration）
        //@EnableAutoConfiguration
        //@ComponentScan
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();  //用json工具转json
    }

}
