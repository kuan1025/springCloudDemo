//TODO 全部都註解掉。因爲已經取出了公用的jar包：feign-api，取出的過程見11-Feign-實現Feign最佳實踐


package cn.itcast.feign.clients;


//Feign config class（宣告configuration = DefaultFeignConfiguration.class）：

import cn.itcast.feign.pojo.User;;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("userservice")
public interface UserClient {


    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") long id);

}


