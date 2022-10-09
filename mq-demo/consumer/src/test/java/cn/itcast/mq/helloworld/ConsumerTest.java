package cn.itcast.mq.helloworld;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

public class ConsumerTest {

    public static void main(String[] args) throws IOException, TimeoutException {
        // 1.建立連線
        ConnectionFactory factory = new ConnectionFactory();
        // 1.1.連接參數，分别是：ip、port、vhost、用戶名稱、密碼
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");    //vitural host：虛擬主機，是對queue、exchange等資源的邏輯分組
        factory.setUsername("root");
        factory.setPassword("password");
        // 1.2.建立連線
        Connection connection = factory.newConnection();

        // 2.建立Channel
        Channel channel = connection.createChannel();

        // 3.建立queue
        String queueName = "simple.queue";
        channel.queueDeclare(queueName, false, false, false, null);

        // 4.訂閱message

        channel.basicConsume(queueName, true, new DefaultConsumer(channel){
            //回調函數的機制（非同步），只有隊列有值，才會執行下面的程式碼。
            //所以執行順序是：先執行後面的 System.out.println("等待接收消息。。。。");
            //後執行 System.out.println("接收到消息：【" + message + "】");
            //回調機制可以不阻塞後續的程式碼
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                // 5.處理消息
                String message = new String(body);
                System.out.println("接收到消息：【" + message + "】");
            }
        });
        System.out.println("等待接收消息。。。。");
    }
}
