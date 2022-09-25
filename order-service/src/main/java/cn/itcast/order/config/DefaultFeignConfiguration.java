//TODO 全部都注释掉了。因为已经抽取出了公共的jar包：feign-api，抽取的过程见11-Feign-实现Feign最佳实践

/*
package cn.itcast.order.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;



public class DefaultFeignConfiguration {

    */
/**
     * 定义feign日志级别
     * NONE, 不记录日志 (默认)。
     * BASIC, 只记录请求方法和URL以及响应状态代码和执行时间。
     * HEADERS, 记录请求和应答的头的基本信息。
     * FULL, 记录请求和响应的头信息，正文和元数据。
     * @return
     *//*

    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
}
*/
