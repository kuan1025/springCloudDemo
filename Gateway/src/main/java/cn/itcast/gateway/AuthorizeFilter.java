package cn.itcast.gateway;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局客製化filter 用來寫邏輯較複雜的filter，default filter 寫較簡單的
 */

//@Order(-1) // filter chain order 越小越高 也可透過介面Order定義
@Component
public class AuthorizeFilter implements GlobalFilter , Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1. 獲取參數
        ServerHttpRequest request = exchange.getRequest();
        MultiValueMap<String, String> queryParams = request.getQueryParams();

        //2. 參數是否有authorization
        String auth = queryParams.getFirst("authorization"); // 取得第一個有authorization參數
        //3. 判斷參數有adminㄇ
        if("admin".equals(auth)){
            //4. 通過
           return chain.filter(exchange);
        }
        //5. 攔截
        // 401 狀態碼 未認證
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
