package cn.itcast.order;


import cn.itcast.feign.clients.UserClient;
import cn.itcast.feign.config.DefaultFeignConfiguration;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.RandomRule;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@MapperScan("cn.itcast.order.mapper")
@SpringBootApplication
//clients: 跨專案注入bean spring也要掃描,clients 掃描單一class檔
//defaultConfiguration: Feign java config 全局有效(如果想局部有效就宣告在UserClient class 上方)：
@EnableFeignClients(clients = UserClient.class,defaultConfiguration = DefaultFeignConfiguration.class)
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