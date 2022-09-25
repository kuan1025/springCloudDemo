//TODO 全部都注释掉了。因为已经抽取出了公共的jar包：feign-api，抽取的过程见11-Feign-实现Feign最佳实践


/*
package cn.itcast.order.clients;

import cn.itcast.order.config.DefaultFeignConfiguration;
import cn.itcast.order.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//Feign配置类局部有效（声明configuration = DefaultFeignConfiguration.class）：
@FeignClient(name = "userservice",configuration = DefaultFeignConfiguration.class)
public interface UserClient {

    @GetMapping("/user/{id}")
    User findById(@PathVariable("id") Long id);
}
*/

