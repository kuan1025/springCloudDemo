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

    //这代码是从生产者主启动类copy过来的
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
