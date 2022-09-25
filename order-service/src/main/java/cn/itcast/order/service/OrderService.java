package cn.itcast.order.service;

import cn.itcast.order.mapper.OrderMapper;
import cn.itcast.order.pojo.Order;
import cn.itcast.order.pojo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.client.RestTemplate;
//import top.daji.feign.clients.UserClient;
//import top.daji.feign.pojo.User;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;


//    @Autowired
//    private UserClient userClient;


//    public Order queryOrderById(Long orderId) {
    // 1.查询订单
//        Order order = orderMapper.findById(orderId);

//        // 2、用feign远程调用
//        User user = userClient.findById(order.getUserId());
//
//        //3、 封装user到Order
//        order.setUser(user);

    // 4.返回
//        return order;
//    }


    @Autowired
    private RestTemplate restTemplate;

    public Order queryOrderById(Long orderId) {
        // 1. 查詢訂單
        Order order = orderMapper.findById(orderId);

        // 2、利用restTemplate發http請求，查User
        // 2.1 路徑
        // 2.1.1 尚未使用eureka寫法
//        String url = "http://localhost:8081/user/"+order.getUserId();
        // 2.1.2 使用eureka寫法
        String url = "http://userservice/user/" + order.getUserId();

        // 2.2 發http請求,第二个参数是返回類型，可以返回json也可以返回物件
        User user = restTemplate.getForObject(url, User.class);
        //3、 封装user到Order
        order.setUser(user);

        // 4.返回
        return order;
    }
}
