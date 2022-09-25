package cn.itcast.order;

import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
//import org.springframework.cloud.client.loadbalancer.LoadBalanced;
//import org.springframework.cloud.openfeign.EnableFeignClients;
//import org.springframework.context.annotation.Bean;
//import org.springframework.web.client.RestTemplate;
//import top.daji.feign.clients.UserClient;
//import top.daji.feign.config.DefaultFeignConfiguration;

@MapperScan("cn.itcast.order.mapper")
@SpringBootApplication
//clients: 跨工程注入bean失败问题解决
//defaultConfiguration: Feign配置类全局有效(如果想局部有效就声明在UserClient这个类上)：
//@EnableFeignClients(clients = UserClient.class, defaultConfiguration = DefaultFeignConfiguration.class)
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }

    /**
     * restTemplate 用來在server間送http request
     * 使用eureka 後 可以搭配LoadBalanced 負載均衡
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    // 負載均衡有多種算法 可以自行調整 有兩種定義方式 1. 組態檔 2. java Bean
    // 隨機算法
//    @Bean
//    public IRule randomRule(){
//        return new RandomRule();
//    }


}