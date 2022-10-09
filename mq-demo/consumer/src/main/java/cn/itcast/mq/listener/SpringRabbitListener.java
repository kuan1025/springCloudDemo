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

import java.util.Map;

@Component
public class SpringRabbitListener {

    //注释掉简单队列的代码，用的时候可以展开，但是要注释掉其他的代码
    /*@RabbitListener(queues = "simple.queue")
    public void listenSimpleQueue(String msg){
        System.out.println("消费者已经接受到消息："+msg);
    }*/

    //----------------WorkQueue开始-----------------
    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue1(String msg) throws InterruptedException {
        System.out.println("消费者1已经接受到消息：" + msg);
        Thread.sleep(20);
    }

    @RabbitListener(queues = "simple.queue")
    public void listenWorkQueue2(String msg) throws InterruptedException {
        System.out.println("消费者2........已经接受到消息：" + msg);
        Thread.sleep(200);  //这里的速度比消费者1慢
    }
    //----------------WorkQueue结束-----------------

    //----------------------FanoutExchange监听侧开始----------------------
    @RabbitListener(queues = "fanout.queue1")
    public void listenFanoutQueue1(String msg) {
        System.out.println("消费者1接受到fanoutqueue1消息：" + msg);
    }

    @RabbitListener(queues = "fanout.queue2")
    public void listenFanoutQueue2(String msg) {
        System.out.println("消费者2接受到fanoutqueue2消息：" + msg);
    }
    //----------------------FanoutExchange监听侧结束----------------------


    //----------------------DirectExchange监听侧开始----------------------
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue1"),     //监听队列
            //绑定交换机 交换机名称是 itcast.direct 交换机类型是 direct（Sprng为我们提供了枚举ExchangeTypes,点进去看看就知道了）
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "blue"})) //指定两个BindingKey
    public void listenDirectQueue1(String msg) {
        System.out.println("消费者接收到direct.queue1的消息：" + msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "direct.queue2"),
            exchange = @Exchange(name = "itcast.direct", type = ExchangeTypes.DIRECT),
            key = {"red", "yellow"})) //这里和上面不同，指定了BindingKey为 red 和 yellow
    public void listenDirectQueue2(String msg) {
        System.out.println("消费者接收到direct.queue2的消息：" + msg);
    }
    //----------------------DirectExchange监听侧结束----------------------

    //----------------------TopicExchange监听侧开始----------------------
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue1"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = {"china.#"})) //和DirectExchange唯一的区别就是这里支持通配符
    public void listenTopicQueue1(String msg) {
        System.out.println("消费者接收到topic.queue1的消息：" + msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "topic.queue2"),
            exchange = @Exchange(name = "itcast.topic", type = ExchangeTypes.TOPIC),
            key = {"#.news"})) //和DirectExchange唯一的区别就是这里支持通配符
    public void listenTopicQueue2(String msg) {
        System.out.println("消费者接收到topic.queue2的消息：" + msg);
    }
    //----------------------TopicExchange监听侧结束----------------------


    //----------------------监听发过来的json开始----------------------
    @RabbitListener(queues = "object.queue")
    public void listenObjectQueue(Map<String, Object> msg) {
        System.out.println("接收到object.queue消息：" + msg);
    }
    //----------------------监听发过来的json结束----------------------


}
