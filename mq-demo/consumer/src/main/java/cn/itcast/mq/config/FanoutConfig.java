package cn.itcast.mq.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//声明
@Configuration
//注意，这是一个配置类，类要加@Configuration , 方法要加 @Bean
//注意，这是一个配置类，类要加@Configuration , 方法要加 @Bean
//注意，这是一个配置类，类要加@Configuration , 方法要加 @Bean
//注意，这是一个配置类，类要加@Configuration , 方法要加 @Bean
public class FanoutConfig {

    public Logger logger = LoggerFactory.getLogger(FanoutConfig.class);
    @Bean
    public Queue simpleQueue(){
        return new Queue("simple.queue");
    }
    //声明一个队列，我们准备往这个队列里扔进任意对象(Object)
    @Bean
    public Queue objectQueue(){
        return new Queue("object.queue");
    }

    //itcast.fanout 声明交换机
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange("itcast.fanout");
    }

    //fanout.queue1 声明队列1
    @Bean
    public Queue fanoutQueue1(){
        return new Queue("fanout.queue1");
    }

    //绑定队列1到交换机.既然要绑定，那就应该形参传入两个参数，分别是队列1和交换机
    @Bean
    public Binding fanoutBinding1(Queue fanoutQueue1, FanoutExchange fanoutExchange){
         return BindingBuilder.bind(fanoutQueue1).to(fanoutExchange);
    }

    //fanout.queue2 声明队列2
    @Bean
    public Queue fanoutQueue2(){
        return new Queue("fanout.queue2");
    }

    //绑定队列2到交换机.
    @Bean
    public Binding fanoutBinding2(Queue fanoutQueue2, FanoutExchange fanoutExchange){
        //logger.info("已绑定队列1和队列2到交换机");
        return BindingBuilder.bind(fanoutQueue2).to(fanoutExchange);
    }
}
