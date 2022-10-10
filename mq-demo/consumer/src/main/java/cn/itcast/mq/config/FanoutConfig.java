package cn.itcast.mq.config;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//宣告
//注意，這是一個組態檔，類要加@Configuration , 方法要加 @Bean
@Configuration

public class FanoutConfig {

    public Logger logger = LoggerFactory.getLogger(FanoutConfig.class);

    //--------------------- 示範 exchange 連接兩個 Queue 的模型 -----------------------------
    //fanout 建立exchange
    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange("fanout");
    }

    //fanout.queue1
    @Bean
    public Queue fanoutQueue1() {
        return new Queue("fanout.queue1");
    }

    // 綁定 fanoutExchange 和 fanoutQueue1
    @Bean
    public Binding fanoutBinding1(Queue fanoutQueue1, FanoutExchange fanoutExchange) {
        return BindingBuilder
                .bind(fanoutQueue1)
                .to(fanoutExchange);
    }


    //fanout.queue2
    @Bean
    public Queue fanoutQueue2() {
        return new Queue("fanout.queue2");
    }

    // 綁定 fanoutExchange 和 fanoutQueue1
    @Bean
    public Binding fanoutBinding2(Queue fanoutQueue2, FanoutExchange fanoutExchange) {
        return BindingBuilder
                .bind(fanoutQueue2)
                .to(fanoutExchange);
    }

    //----------------------------------------------------------------

    @Bean
    public Queue objectQueue(){
        return new Queue("Object.queue");
    }

}
