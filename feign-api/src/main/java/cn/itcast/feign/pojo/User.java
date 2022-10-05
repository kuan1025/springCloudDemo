//TODO 全部都注释掉了。因为已经抽取出了公共的jar包：feign-api，抽取的过程见11-Feign-实现Feign最佳实践


package cn.itcast.feign.pojo;

import lombok.Data;

@Data
public class User {
    private Long id;
    private String username;
    private String address;
}
