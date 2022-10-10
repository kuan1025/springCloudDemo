package cn.itcast.mq.listener;

import org.junit.runner.RunWith;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalTime;
import java.util.Map;

@Component
public class SpringRabbitListener {

    //註釋掉簡單隊列的程式碼，用的時候可以展開，但是要註釋掉其他的程式碼
    @RabbitListener(queues="simple.queue")
    public void listenSimpleQueue(String msg){
        System.out.println("receive the message from simple.queue => [ " + msg +" ] ");
    }

    //----------------WorkQueue-----------------
    // 結論 ： 1. 預設情況訊息會被平分，因為preFetch機制，訊息會先被均分(預先取得)，consumer分別做處理
    //        2. 可以調整prefetch的數量(yml配置)，確保做完才取下一個


    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue1(String msg) throws InterruptedException {
        System.out.println("consumer1接受到訊息： [ " + msg + " ] "+ LocalTime.now());
        Thread.sleep(20); // 每秒處理50 unit
    }



    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue2(String msg) throws InterruptedException {
        System.err.println("consumer2接受到訊息： [ " + msg + " ] "+ LocalTime.now());
        Thread.sleep(200);   // 每秒處理5 unit
    }
    //----------------WorkQueue結束-----------------

    //----------------------FanoutExchange----------------------
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String msg) {
        System.out.println("consumer 接受到fanout.queue1 訊息：" + msg);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String msg) {
        System.out.println("consumer 接受到fanout.queue2 訊息：" + msg);
    }
    //----------------------FanoutExchange結束----------------------

    //----------------------DirectExchange----------------------

    /**
     * 用bean組態建立exchange queue 有點麻煩 ( 參考FanoutConfig.java
     * 這裡示範 在listener建立上述組件
     * 順序 1. bind 2. queue 3. exchange 4. 指定key
     */
    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(name = "direct.queue1"),
            exchange = @Exchange(name="direct.fanout",type = ExchangeTypes.DIRECT),
            key = {"red","blue"}
    ) )
    public void ListenDirectQueue1(String msg){
        System.out.println("consumer 接受到direct.queue1 訊息：" + msg);
    }

    @RabbitListener(bindings =@QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name="direct.fanout",type = ExchangeTypes.DIRECT),
            key = {"red","yellow"}
    ) )
    public void ListenDirectQueue2(String msg){
        System.out.println("consumer 接受到direct.queue2 訊息：" + msg);
    }



    //----------------------DirectExchange結束----------------------




    //----------------------TopicExchange----------------------

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "topic.fanout",type = ExchangeTypes.TOPIC),
            key = "Taiwan.#"
    ) )
    public void listenTopicQueue1(String msg) {
        System.out.println("consumer topic.queue1 訊息：" + msg);
    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "topic.fanout",type = ExchangeTypes.TOPIC),
            key = "#.news"
    ) )
    public void listenTopicQueue2(String msg) {
        System.out.println("consumer topic.queue2 訊息：" + msg);
    }
    //----------------------TopicExchange結束----------------------


    //---------------------json----------------------
    @RabbitListener(queues = "Object.queue")
    public void listenObjectQueue(Map<String, Object> msg) {
        System.out.println("接收到Object.queue消息：" + msg);
    }
    //----------------------json結束----------------------


}
