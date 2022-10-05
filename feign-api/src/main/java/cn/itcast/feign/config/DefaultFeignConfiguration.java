//TODO 全部都註解掉。因爲已經取出了公用的jar包：feign-api，取出的過程見11-Feign-實現Feign最佳實踐


package cn.itcast.feign.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;


public class DefaultFeignConfiguration {


/**
     * 定義feign 級別
     * NONE, 不紀錄日誌 （預設）。
     * BASIC, 只紀錄請求方法和URL以及響應狀態code和runtime。
     * HEADERS, 紀錄response header , request header。
     * FULL, 紀錄response header , request header, body。
     * @return
     */

    @Bean
    public Logger.Level logLevel(){
        return Logger.Level.BASIC;
    }
}

