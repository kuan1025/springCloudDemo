package cn.itcast.mq.helloworld;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class PublisherTest {
    @Test
    public void testSendMessage() throws IOException, TimeoutException {
        // 1.建立連線
        ConnectionFactory factory = new ConnectionFactory();
        // 1.1.連接參數，分别是：ip、port、vhost、用戶名稱、密碼
        factory.setHost("127.0.0.1");
        factory.setPort(5672);
        factory.setVirtualHost("/");     //vitural host：虛擬主機，是對queue、exchange等資源的邏輯分組
        factory.setUsername("root");
        factory.setPassword("password");
        // 1.2.建立連線
        Connection connection = factory.newConnection();

        // 2.建立Channel
        Channel channel = connection.createChannel();

        // 3.建立queue
        String queueName = "simple.queue";
        channel.queueDeclare(queueName, false, false, false, null);

        // 4.發送消息
        String message = "hello, rabbitmq!";
        channel.basicPublish("", queueName, null, message.getBytes());
        System.out.println("傳送成功：【" + message + "】");

        // 5.關閉資源
        channel.close();
        connection.close();

    }
}
