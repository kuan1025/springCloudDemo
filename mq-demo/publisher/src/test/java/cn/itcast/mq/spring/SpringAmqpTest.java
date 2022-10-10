package cn.itcast.mq.spring;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

/**
 * 五種模型
 * 1. 基本 queue - consumer
 * 2. workQueue   queue-兩個consumer
 * 3. fanout      exchange-發送給每個queue
 * 4. Direct Exchange  exchange-發給指定的queue
 * 5. TopicExchange 與4類似 差在TopicExchange由多個單字組成，並以.分割
 */
@EnableRabbit
@RunWith(SpringRunner.class)
@SpringBootTest
public class SpringAmqpTest {
    @Autowired
    private RabbitTemplate rabbitTemplate;  //注入進來的RabbitTemplate並不存在於我們的專案裡，是spring的jar提供的

    //傳送訊息
    @Test
    public void testSendMessage2simpleQueue() {

        // queue 還是要先存在，否則送不到
        String queueName = "simple.queue";
        String message = "hello, spring amqp";
        rabbitTemplate.convertAndSend(queueName, message);
    }


    @Test
    public void workQueue() throws InterruptedException {
        String queueName = "simple.queue";
        String message = "hello, message-";
        for (int i = 1; i <= 50; i++) {
            rabbitTemplate.convertAndSend(queueName, message + i);
            Thread.sleep(20);   //方便看執行過程，間隔傳訊時間
            System.out.println("已送出：" + i + "個訊息到queue");
        }
    }


    //測試FanoutQueue的發送隊列：
    /*
        可以發現，FanoutQueue增加了一層exchange，可以多個queue對應多個consumer。
    */
    @Test
    public void testSendFanoutExchange(){
        //exchange
        String exchangeName = "fanout";
        //msg
        String message = "hello, every one!";
        //送到exchange
        //三個參數，分別是exchangeName、 routingKey(先不管，後面會講到) 、訊息
        rabbitTemplate.convertAndSend(exchangeName, "", message);
    }


    //測試DirectQueue
    //比起FanoutQueue，DirectQueue並不是發給every one，而是發給BindingKey相同的queue
    @Test
    public void testSendDirectExchange(){
        //exchange
        String exchangeName = "direct.fanout";
        String message = "hello, blue!";
        //send to exchange
        //三個參數，分別是exchangeName、 routingKey(含指定key的queue) 、訊息
        rabbitTemplate.convertAndSend(exchangeName, "blue", message);
    }


    //測試TopicQueue
    //比起DirectQueue，TopicQueue的唯一区别就是在DIrectQueue的基础上支持通配符
    @Test
    public void testSendTopicExchange(){
        //exchange
        String exchangeName = "topic.fanout";
        String message = " news ！！！！！！！！！！！！！";

//        rabbitTemplate.convertAndSend(exchangeName, "Taiwan.news", message); //listenTopicQueue1和listenTopicQueue2都能收到
        rabbitTemplate.convertAndSend(exchangeName, "Taiwan.123", message);   //only listenTopicQueue1能收到
//        rabbitTemplate.convertAndSend(exchangeName, "japan.news", message);   //only listenTopicQueue2能收到
    }

    @Test
    public void testSendObjectQueue(){
        Map<String,Object> msg = new HashMap<>();
        msg.put("name","Damian");
        msg.put("age",23);
        rabbitTemplate.convertAndSend("Object.queue",msg);
    }


}
