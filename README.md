# 前言

歡迎大家的指教！！，**點個Star** ⚆_⚆

程式碼和資料參考來源：
[黑馬 2021.8 SpringCloud*+RabbitMQ+Docker+Redis+搜索+分布式](https://www.bilibili.com/video/BV1LQ4y12價格7n4?from=search&seid=10991461656716219000)

近期碰到microservice的專案，下班、週末自學spring cloud，希望這份筆記有幫助到想學spring cloud的人



# 一、微服務學習架構

從獨立架構到微服務架構，需要一些“中間技術”實踐，其中重要的部分包括：

* registery server ：Eureka 、Zookeeper、Nacos
* server gateway：Zuul 、Gateway
* microservice remote control ： RestTemplate、Feign
* container tech : Docker
* message queue : MQ（多種方式實現）
* load balance : Ribbon 、 Nginx
* 分散式架構收尋：ElasticSearch



**本 repo SpringCloud版本：**

![](https://img-blog.csdnimg.cn/dc1f12a86ce84ca9a537e72a33d24617.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)




# 二、microservice remote control Demo——RestTemplate基本使用

<font color="#35BDB2">**程式碼位置：springCloudDemo 下的order-service 和 user-service**</font>

核心程式碼如下：跨server remote 調用
![ ](https://img-blog.csdnimg.cn/f17e762619e24eda9fa30f2df4656c5f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
總結：RestTemplate微服調用用方式

基於RestTemplate發起的http請求遠端調用

http請求做遠端調用，只要知道呼叫方的ip、port、controller path、request parameter。

# 三、Eureka註冊

<font color="#35BDB2">**程式碼位置：在 springCloudDemo下的eureka-server（註冊的是order-service 和 user-service）**</font>

**Eureka的作用：**

* consumer該如何取得supplier具體資訊?
  * supplier啟動時向eureka註冊自己的資訊
  * eureka保存這些資訊
  * consumer根據server name 向eureka取提supplier info
* 如果有多個supplier,consumer該如何選擇?
  * supplier利用load Balance 演算法，從server list中挑選一個
* consumer如何確認supplier是否正常運行?
  * supplier會每隔30秒向EurekaServer發送server狀態，報告是否正常運行
  * eureka會更新紀錄server list 資訊，運行不正常的supplier會被剔除
  * consumer就可以取到最新的資訊

**注意：**

Eureka自己也是一個microservice，Eureka啟動時，要把自己也註冊進去。這是因為如果後續建構Eureka 群集時做資料交流：

```yml
server:
  port: 10086 
spring:
  application:
    name: eurekaserver # eureka server name
eureka:
  client:
    service-url:  # eureka的位址
      defaultZone: http://127.0.0.1:10086/eureka
```

上段程式碼，defaultZone，將自己也這註冊進去了。效果如下圖：

![](https://img-blog.csdnimg.cn/9ac4515cc7ca49da84b96fa5f77f59a4.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)



# 四、Ribbon

**兩個疑問：**

* 如果有多個supplier，服務調用方如何判斷調用哪個server呢？
* 而且服務調用方為何不用寫死supplier的url（ip和port），只需要寫server name 即可？為什麼我們只輸入了server name就可以訪問了呢？
  （```String url = "http://userservice/user/" + order.getUserId();    //由於已經在Eureka裡面配置了server，這裡只需要寫配置的server name 即可```）

這都是Ribbon的負載均衡做到的，**針對問題一**，通過跟debug得知，Ribbon是通過幾種不同的負載均衡算法實現的這一個機制（比如[輪尋演算法](https://blog.csdn.net/jasonliuvip/article/details/25725541)）；針對問題二，Ribbon會根據server name 去Eureka註冊中心獲取服務，如下兩個圖所示：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/3a63e86645fc4c5ea72fd8922a316023.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/74da1efa07cc413fb31cf917dd7a0822.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**Ribbon 負載均衡策略**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/971f44a376a6479ca683d716a9fcc2bc.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
RoundRobin —— 意為輪詢，操作系統也有類似的概念（CPU時間片輪轉）

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/64db29d8532942a0a9d58fa22cc9661d.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
可以使用如下程式碼配置對某個服務的負載均衡策略(在 application.yml裡配置)

```yml
userservice: # 給某個微服務配置負載均衡規則，這裡是userservice為例
  ribbon:
    NFLoadBalancerRuleClassName: com.netflix.loadbalancer.RandomRule # 負載均衡規則

```

**Ribbon開啟eager load**
Ribbon預設是採用lazy load，即第一次訪問時才會去創建LoadBalanceClient，請求時間會很長。

而eager load則會在專案啟動時創建，降低第一次訪問的耗時，通過下面配置開啟eager load：



```yaml
ribbon:
  eager-load:
    enabled: true # 開啟eager load
    clients:
      - userservice # 指定eager load的server name稱
      - xxxxservice # 如果需要指定多個，需要這麼寫
```

# 六、Nacos註冊中心

和前面的Eureka、Zookeeper是同類型技術

## 6.1 安裝啟動

下載地址：https://github.com/alibaba/nacos/releases
本文選用1.4.1版本

解壓完成後，cd到nacos的bin目錄下，然後輸入命令：
```startup.cmd -m standalone```

關閉的话，如果是linux系統，就運行shutdown.sh即可

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e8417079376d45f2bbad5d9b5956b360.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
出現如上圖所示界面，說明啟動成功。通過上圖也可知它的預設port是8848（國人做的註冊中心果然不一樣 8848氪金手機~）

輸入地址http://127.0.0.1:8848/nacos 即可訪問主頁，用戶名和密碼都是nacos
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b5d74b0a898b4575a028a9b594bf0715.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)<font color="#35BDB2">**核心程式碼位置：在 module 01-cloud-demo 下註冊了order-service 和 user-service，同時註解掉了兩個 module的Eureka程式碼（包括pom.xml也註解了，畢竟是同類技術）**</font>

**注意**，必須將之前的Eureka程式碼和pom都註解掉，而且把SpringCloud也註解掉（因為已經用了SpringCloudAlibaba），否則有可能報：```APPLICATION FAILED TO START```這個錯誤

**對比之前的Eureka，我們是在idea裡面專門啟動了一個Eureka的工程，所以 Eureka不需要下載，就可以通過port號訪問Eureka的註冊中心。而Nacos是 下載並運行的，所以不需要在idea啟動某個 module，直接通過運行Nacos的startup.cmd即可通過port號訪問Nacos的註冊中心。**

## 6.2 Nacos自定義負載均衡策略

也是使用的Ribbon，下面一個例子將Nacos配置成同集群優先的負載均衡策略：

預設的`ZoneAvoidanceRule`並不能實現根據同集群優先來實現負載均衡。

Nacos中提供了一個`NacosRule`的實現，可以優先從同集群中挑選實例。

1）給order-service配置集群資訊

修改order-service的application.yml文件，加入集群配置：

```sh
spring:
  cloud:
    nacos:
      server-addr: localhost:8848
      discovery:
        cluster-name: HZ # 集群名稱
```



2）修改負載均衡規則

修改order-service的application.yml文件，修改負載均衡規則：

```yaml
userservice:
  ribbon:
    NFLoadBalancerRuleClassName: com.alibaba.cloud.nacos.ribbon.NacosRule # 負載均衡規則 
```

配置完成之後，就可以實現同集群優先的 負載均衡了


## 6.3 Nacos實現配置動態更新

有兩種方式，都在程式碼中配置了，具體位置在：
<font color="#35BDB2">**核心程式碼位置：在 module 01-cloud-demo 下 user-service，第一種方式是通過組態檔方式(PatternProperties.java)；第二種方式是通過注解@Value("${yaml裡定義的鍵值對}")的方式**</font>

* **動態更新注意點：**
  你在Nacos中配置的：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/820f5805d8a141dd9f8221cbecbdf142.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)你在bootstrap.yaml裡配置的：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ad56853609284a6c81b5a39b1dd94912.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)**這兩張圖應該是一致的，注意 ```-```和```.```的區別！！！**

* **動態更新優先級**
  Nacos帶環境的配置 > Nacos不帶環境的配置 > 本地yaml文件配置<br>
  很好理解，Nacos帶環境可以理解為專属化配置(開發環境和生產環境)、肯定優先於Nacos不帶環境的全局配置；本地yaml文件配置則肯定低於Nacos的配置。


## 6.4 Nacos集群

<font color="#35BDB2">**位置：在 module 01-cloud-demo 下根目錄，有一個叫Nacos集群搭建.md的文件**</font>

**注意點：修改兩個組態檔：**

* 修改cluster.conf
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/3216cabcbd0e4331951b0539c31398ea.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
* 修改Nacos的application.properties（不是你的application.properties）

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7ed93f53205e4c219fa102ee94f26c75.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b55826b031c6459b9bbeb324e3f4ed62.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

修改完成後保存即可。

* **使用Nginx對Nacos做反向代理**
  這裡需要Nginx前置知識，可以看我以下這一篇文章：[Nginx入門：通俗理解反向代理和負載均衡，簡單配置Nginx](https://blog.csdn.net/weixin_44757863/article/details/120117645?spm=1001.2014.3001.5501)


**如果你的Nacos配置集群死活報下圖的錯誤：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ea27fbe5628245fe942f600536830f5e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
請檢查你的MySQL版本，需要在5.7及以上，而且在8.0以下（比較苛刻）


# 七、Feign遠端調用

<font color="#35BDB2">**核心程式碼位置：如下圖所示：**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e1853dfb5e9c4a5485114679a3a23621.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2">**order-service會引入上圖的feign-api，實現遠端調用**</font>


## 7.1 還原事故現場

由於上一章（第六章）做了Nacos集群，但是整個第七章是基於單體的註冊中心。所以要把集群恢復成單體。

* nacos不使用集群啟動，恢復你standalone環境，主要是修改組態檔的nacosport
* 這樣做的目的是讓微服務註冊進註冊中心。你用nacos還原事故現場也行，用eureka還原事故現場也行。反正能還原即可。
* 打開你的資料庫服務

**引入feign版本報錯bug問題解决：**
我手工指定了一個版本，版本號是：

```xml
		<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-openfeign</artifactId>
            <version>2.2.9.RELEASE</version>
        </dependency>
```

## 7.2 Feign自定義配置

分為組態檔方式和程式碼方式。

* 組態檔方式：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/5aaa257c34124aaab2afce888042f5d6.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
* 程式碼方式（新建一個配置類）：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/2ac0d09d02bd4992a3094feb92032897.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**我們採用的是程式碼方式，並全局生效（新建一個配置類）**

## 7.3 Feign性能優化

底層的客戶端實現是：

* URLConnection:預設實現，不支援連線池
* Apache HttpClient: 支援連線池（常用）
* OKHttp：支援連線池

第一種方式是預設的，不支援連線池。所以這裡的性能優化指的是：換成第二種方式或者第三種方式。

**其中第二種方式 Apache HttpClient 常用於模擬postman的樣式，發送一個form-data樣式的post請求，也可在這個post請求裡上傳文件。我們也採用的是這種方式**

性能優化步骤：
1、引入jar包：

```xml
<!--HttpClient－-->
        <dependency>
            <groupId>io.github.openfeign</groupId>
            <artifactId>feign-httpclient</artifactId>
            <version>10.1.0</version>
        </dependency>
```

2、在組態檔yml中配置：

```yaml
feign:
  httpclient:
    enabled: true # 支援HttpClient的開關
    max-connections: 200 # 最大連線數
    max-connections-per-route: 50 # 單個路徑的最大連線數
```

<font color="#35BDB2">**這裡的改動都是在order-service module下**</font>

## 7.4 Feign最佳實践

打成jar包方式：

[java中的JAR包](https://yuanyu.blog.csdn.net/article/details/87880736)

**1、在專案中加入單獨的jar包步骤：**

寫好自己的maven專案後，執行clean package，即可得到一個jar包

**2、在專案中引入單獨的jar包圖解：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/f47ab4999cd64a438feab33e6448c4a0.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
上圖其實是在專案的根目錄創建了一個叫lib的文件夾，裡面存著自定義jar包。然後即可引入。
**3、針對1和2的補充，有的時候沒必要非得打jar包，可以寫一個子 module引入呀，如下圖所示：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c70c5c3d414a48bcb6cf25a5e83cdca3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
這塊看不懂，可以自行搜索maven的jar包引入方式和順序


# 八、統一Gateway

## 8.1 概述

三大功能：

* 身份認證和權限校驗
* 服務路由、負載均衡
* 請求限流

在SpringCloud中gateway技術包括兩種：gateway和zuul
其中Zuul是基於Servlet的實現，属於阻塞式編程，而Gateway則是基於SPring5中提供的WebFlux，属於響應式編程的實現，具備更好的性能。


## 8.2 搭建gateway服務

<font color="#35BDB2">**核心程式碼位置：如下圖**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/be6e7d1d82314bfeacce87206dbe1455.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

步骤：

* 新建 module
* 編寫組態檔yml：
  * 註冊進nacos的配置
  * gateway自身的port號
  * gateway路由配置

```yaml
server:
  port: 10010
spring:
  application:
    name: gateway
  cloud:
    nacos:
      server-addr: 127.0.0.1:8848 # nacos地址
    gateway:
      routes:
        - id: user-service # 路由標識，必須唯一
          uri: lb://userservice # 路由的目標地址 lb就是負載均衡，後面跟著是server name稱
          predicates: # 路由斷言，判斷請求是否符合規則
            - Path=/user/** # 路徑斷言，判斷路徑是否是以/user開header，如果是則符合規則
        - id: order-service
          uri: lb://orderservice
          predicates:
            - Path=/order/**
```

除了上面這些，還可以配置路由過濾器。後面會講到。

配置完畢後，啟動你的gateway服務和你的user-service和order-service服務，即可通過gateway訪問到user-service和order-service

**工作原理總結**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/df20e314d2424af8bcc4efb3c551040d.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 8.3 路由過濾

### 8.3.1 斷言工廠：對請求進行過濾

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4d122d3cf2b440a0a76c16c73f18ef03.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
如果你不會寫匹配表達式，可以去spring官網查：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c85c9d15bdaa489788bdd07ac6e1e10e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
如果你的請求不符合路由斷言，那你的請求就會被拒绝，返回一個404. 我們可以通過配置路由斷言工廠的方式來過濾某些請求。

### 8.3.2 過濾器GatewayFilter：對請求和響應進行過濾

**它和8.3.1講述的斷言工廠一樣，都配置在yaml裡**

* GatewayFilter 和 8.3.1講述的斷言工廠的區別：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/db954776f58a444286a95d7c146a7820.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
* 與斷言工廠類似，spring也為我們提供了過濾器工廠：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/df41c0072a4b4c67b2224666c1bc73ba.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/bcc775ae0a6c4d7e86e84369fc8b7ea2.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
GatewayFilter可以針對某一類路由標識單獨配置，也可以配置成全局配置（所有路由id都生效），具體可自行百度，<font color=red>但是過濾器鏈執行順序有变化，可以看8.8.4详解</font>


### 8.3.3 全局過濾器GlobalFilter：可以自定義過濾邏輯程式碼實現


![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e6f92d484adb4d17a71183dc2145963a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c2c2d5168a554df09dfaa51f3c64a7bd.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


**案例正確執行的效果圖：**
不加參數被過濾器攔截：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d05fa12cb3e941bdb49e7726573b9146.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
加了參數，不被攔截，正確獲得響應！

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7d6e8cd1ac4747438efc8225bf6a605e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


### 8.3.4 過濾器鏈執行順序

原理：關鍵詞 適配器模式

順序：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/48f6472abd1947a380e8fe44be917a16.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 8.4 gateway跨域問題處理

域名不一致就是跨域：

* 域名不同 比如www.baidu.com  和 www.bilibili.com
* 域名相同，port不同

跨域是一個前端的概念，瀏覽器禁止請求的發起者和服務端發生跨域ajax請求，該請求會被瀏覽器攔截。
解决方案：CORS


之所以之前的user-service調用order-service不存在跨域，是因為不是ajax請求。因為這是一個瀏覽器行為，只有ajax請求會被攔截

**處理方法：**
簡單配置即可：

```yaml
spring:
  cloud:
    gateway:
      globalcors: # 全局的跨域處理
        add-to-simple-url-handler-mapping: true # 解决options請求被攔截問題
        corsConfigurations:
          '[/**]':
            allowedOrigins: # 允許哪些網站的跨域請求
              - "http://localhost:8090"
              - "http://www.leyou.com"
            allowedMethods: # 允許的跨域ajax的請求方式
              - "GET"
              - "POST"
              - "DELETE"
              - "PUT"
              - "OPTIONS"
            allowedHeaders: "*" # 允許在請求中攜帶的header資訊
            allowCredentials: true # 是否允許攜帶cookie
            maxAge: 360000 # 這次跨域檢測的有效期
```


如果想要演示，需要啟動一個前端工程模擬一個ajax請求。

# 九、Docker

Docker命令居多，可以看我下面兩張思维導圖，包含了概念理解和常用命令。

## 9.1 Docker概念

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/72cecd0d7be34eb8a8002d09dab2378c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 9.2 Docker常用命令

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/df5b634e325e493e82eae96fde10e86f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)



# 十、MQ(Message Queue)消息隊列

## 10.1 概述

* **事件驅動架構的概念**：
  MQ是事件驅動架構的實現形式，MQ其實就是事件驅動架構的Broker。
* **非同步應用場景：**
  如果是傳統软件行业：虽然不需要太高並發，但是涉及到和其它系統做對接，我方系統處理速度(50ms)远快於對方系統處理速度(1-3s)，為了兼顧用戶的體驗，加快單据處理速度，故引入MQ。
  用戶只用點击我方系統的按鈕，我方按鈕發送到MQ即可給用戶返回處理成功資訊。背後交由對方系統做處理即可。至於處理失败，補償機制就不是用戶體驗要考慮的事情了，這樣可以大大提升用戶體驗。

* **非同步傳輸優缺點：**
  * **優點：**
    * 耦合度低
    * 吞吐量提升
    * 故障隔離
    * 流量削峰
  * **缺點：**
    *	依賴於MQ的可靠性，安全性，吞吐能力（因為加了一層MQ，當然高度依賴它）
    *	業務複雜了，業務沒有明顯的流程線，不好追蹤管理




* **MQ常見技術介绍：**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7558cd92dccc4826af84499d842f3c6f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 10.2 RabbitMQ安裝

<font color="#35BDB2">**如何安裝，見下圖文件：RabbitMQ部署指南.md**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ca7f48ad8365402cbf6458fbbad2ce6c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

執行MQ容器的命令和簡單說明：

```shell
docker run \
 -e RABBITMQ_DEFAULT_USER=root \  #用戶名
 -e RABBITMQ_DEFAULT_PASS=root \  # 密碼
 --name mq \	
 --hostname mq1 \	# 主機名，將來做集群部署要用
 -p 15672:15672 \	# port映射，映射RabbitMQ管理平台port
 -p 5672:5672 \		# port映射，消息通信port
 -d \	# 後台運行
 rabbitmq:3-management	# 鏡像名稱
```

```#```號不被識別，下面提供一個沒有```#```的版本

```shell
docker run \
 -e RABBITMQ_DEFAULT_USER=root \
 -e RABBITMQ_DEFAULT_PASS=root \
 --name mq \
 --hostname mq1 \
 -p 15672:15672 \
 -p 5672:5672 \
 -d \
 rabbitmq:3-management
```

最後在瀏覽器地址栏輸入：```你的port號:15672```
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c9239f13c1ec4ac987e9fb2cd7ca3825.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
如果看到上圖頁面，就說明成功了！

**虚擬主機，租戶隔離的概念，重要！！！**
vitural host：虚擬主機，是對queue、exchange等資源的邏輯分組

## 10.3 常見消息模型

### 10.3.1 簡單隊列模型

<font color="#35BDB2">**核心程式碼位置：下圖所示**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/f72dbed6a94940f1b18ed884d78a751a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


## 10.4 Spring AMQP

**概述**

AMQP（Advanced Message Queuing Protocol），是用於在應用程序之间傳递業務資訊的開放標准，該協議與語言和平台無關，更符合微服務中獨立性的要求

SpringAMQP就是Spring基於AMQP定義的一套API规范。

**使用Spring AMQP實現簡單隊列模型步骤：**

**以 supplier 為例：**

由於這玩意已被spring托管了，<font color=red>所以對比之前rabbitmq demo的方式，不需要在程式碼裡寫配置了，直接在spring的application.yml裡寫組態檔即可.</font>

配置如下：

```yaml
# 1.1.設置連線參數，分別是：主機名、port號、用戶名、密碼、vhost
spring:
  rabbitmq:
    host: 127.0.0.1
    port: 5672
    username: root
    password: root
    virtual-host: /
```

然後編寫測試類，以及測試程式碼,位置如下圖所示：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/f3c36a7a22c94e9ebccab16871c44e3e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
** consumer 一側，和 supplier 類似。不再赘述，如下圖進行配置即可：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7073034629fa47b89bd7a2307e0370c3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
至於如何啟動 consumer  一側？如下圖所示：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ce3dd90f169d42229173743f00226582.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

### 10.3.2 WorkQueue模型

之所以 10.3.2 放在 10.4章，因為demo模型的演示，今後就是以 Spring AMQP為例了

**概述**
其實就是一個隊列，绑定了多個 consumer ，一條消息只能由一個 consumer 進行消費，預設情况下，每個 consumer 是輪詢消費的。**區別於下文的發布-訂閱模型（該模型允許將同一消息發給多 consumer ）**

**案例：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/598f5466c8e443f29c353bc2f71dfec3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


### 10.3.3 發布-訂閱模型

**概念**
允許將同一個消息發給多個 consumer 。
其實就是加了一層交換機而已，如下圖所示：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c88c35592357458d9b0932bdedfe7cfb.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**交換機類型有很多，下文逐一介绍。下圖表示了各交換機類型的继承關係**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/db1a88a9c42c42b7a035348408b4e21a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**最後，交換機只能做消息的轉發而不是存储，如果將來路由（交換機和消息隊列queue的連線稱作路由）沒有成功，消息會丢失**


#### A. Fanout Exchange

<font color="#35BDB2">**位置如下圖，注意一定要放在consumer包下，因為是 consumer 消費行為：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d0b56db948ba47cc9c3655e9527eb040.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2">** supplier 加入程式碼位置如下圖：**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/27b2105188e34cd886a357f7b109afe4.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**隊列绑定成功後，打開mq可視化頁面，會看到如下圖所示：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b0e9c238693d4e258557bc512bfccabf.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**寫好程式碼後，分別啟動生產方，消費方，即可看到調試成功資訊輸出：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/6eacca917042472b90ab78ad7aaa0dd2.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


**概念**：
這種模型中 supplier 發送的消息所有 consumer 都可以消費。

**案例：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/8be81c5bec23424bb865f5b06f41128a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**總結：workQueue模式和FanoutQueue模式區別：**
**P代表 supplier ，C代表 consumer  X代表交換機，红色部分代表消息隊列**
workQueue:
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e96dd02ffd2046f7aaedd6e18c7a293a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
FanoutQUeue:
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/54a58bed69e8421aa74da4bff96a9d88.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
可以發現，FanoutQueue增加了一層交換機，可以多個隊列對應多個 consumer 。**而且比起WorkQueue，FanoutQueue supplier 是先發送到交換機; 而WorkQueue是直接發送到隊列**





#### B. Direct Exchange

**概念**：DirectExchange 會將接收到的消息根據規則路由到指定的queue，因此稱為路由模式，如下圖所示：

**P代表 supplier ，C代表 consumer  X代表交換機，红色部分代表消息隊列**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e0cafe9aa34947eaa6d7f7eb060f8912.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

* 每一個queue都會與Exchange設置一個BindingKey
* 將來發布者發布消息時，會指定消息的RoutingKey
* Exchange將消息路由到BingingKey與RoutingKey一致的隊列
* 實際應用時，可以绑定多個key。
* **如果所有queue和所有Exchange绑定了一樣的key，那 supplier 所有符合key的消息 consumer 都會消費。如果這樣做，那DirectExchange就相當於FanoutExchange了（Direct可以模擬Fanout的全部功能）**


案例如圖：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/eb2253f89e4e4e83b5319558b7158a32.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


<font color="#35BDB2">** consumer 加入程式碼位置如下圖：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b7ade1f045c14eb0b065972b4022f016.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2">**發送隊列加入程式碼位置如下圖：**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c8d114895fff469e80debf3d809f124d.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)



**這次的案例，我們用注解的方式聲明隊列和绑定交換機，之前Fanout的Demo是手寫了個配置類。** 直接在監聽隊列裡面聲明如下圖注解即可：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/77bbef34813f4a3fa0d1b3e29314336c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
上圖的@QueueBinding點進去：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/6b05334f6484439dbf92e31e30a01d00.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
上面的key是個數組，可以寫多個key。

寫完程式碼後啟動 consumer 的SpiringBoot主啟動類（報錯資訊不用管），然後進入rabbitMQ可視化控制台，出現下圖則說明配置成功：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/48670dfbf1464952b0733b59fd8da52b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
随後運行發送隊列的Test程式碼，打開 consumer 的控制台，出現如下圖輸出，則說明案例測試通過：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a42fceef10944bb89abb48f5b8339491.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)



#### C. Topic Exchange

**概念： 和上面的Direct Exchange及其相似：**

（下圖來源於Java旅途 ，作者大尧）
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/8f10612746cc4e038190bd31b9d2186b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**案例：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b037530521e148cca2e86391146e6b1a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2">**發送隊列、 consumer 的加入程式碼位置和上面的DirectExchange位置一致，就在DirectExchange程式碼下面。**</font>

寫完程式碼後啟動 consumer 的SpiringBoot主啟動類（報錯資訊不用管），然後進入rabbitMQ可視化控制台，出現下圖則說明配置成功：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d5e0ac7df3814297bc520ed193826a9f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

### 10.3.4 消息轉換器

**引入：**

在之前的案例中，我們發送到隊列的都是String類型，但是實際上，我們可以往消息隊列中扔進去任何類型。我們看下圖，convertAndSend這個方法，第三個參數也是Object。這說明可以發送任何類型給消息隊列:
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/8f395fc365ae4f6e834ff6ddea1ade73.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**案例：**
創建一個隊列，向該隊列扔一個任意物件（Object類型）


<font color="#35BDB2">**創建隊列位置、發送隊列的加入程式碼位置如下圖**</font>
創建隊列位置：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e65474811ce54d09884d6b96fd825f4b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
發送：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a342bea892a849d48c387bfad6dfd2d4.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)





**寫完程式碼後啟動發送的Test，去看RabbitMQ控制台，發現我們發過來的物件在内部被序列化（ObjectOutPutStream）了，如下圖所示：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b4c457b8ac8d49c69c4da66b091f95ec.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
如果不知道什麼是ObjectOutPutStream可自行百度：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/0f19c50dd3f247719e50fca0eac5bbac.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


<font color=red>上面說的ObjectOutPutStream這個序列化方式，缺點很多（性能差、長度太長、安全性有問題）。我們可以在這裡調優一下，推薦JSON的序列化方式。於是引出了這一節的正文：自定義消息轉換器(覆蓋了原有的Bean配置)：</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/0cb0a74cd3d64249bd7a5ac84b8b0d5e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2">**聲明配置位置如下圖**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4ab28bea08e64515a64e934deb9a541a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**配置了消息轉換器轉換成json，然後重複之前的步骤，使用發送者發送一條消息到隊列，發送完成後打開RabbitMQ控制台，出現如下圖所示：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c4493d201a3a4912a925f9633be299ab.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**該物件被成功序列為json格式了！！！！！**

* 對剛才發送過來的json格式消息進行接收，需要修改 consumer 一側的程式碼。並不複雜，如下圖所示：
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/633227ad1a1c421a9622ba76eaa0a079.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  <font color="#35BDB2">** consumer 配置、監聽消息位置如下2圖：**</font>
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/3f40dca35a3c48b6a344683d6d08ea45.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ef12bca5250749e5b3671f4860052f12.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**總結**

* 消息序列化和反序列化使用MessageConverter實現
* SpringAMQP的消息序列化預設底層是使用JDK的序列化
* 我們可以手動配置成其它的序列化方式（覆蓋MessageConverter配置Bean），推薦json
* **發送方和接收方必須使用相同的MessageConverter**


# 十一、ElasticSearch分布式搜索

## 11.1 ES基礎概念

**ES概述：**

ELK（Elastic Stack）是以Elastic為核心的技術棧，如下圖所示：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/bf0acf1f27ee4018a93993c1b8079d17.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

ElasticSearch底層是Lucene（側面說明了ES和Hadoop千丝万缕的關係）

推薦下面一篇文章：深入浅出大資料（From Zhihu）https://zhuanlan.zhihu.com/p/54994736
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/59c9cbf7ae554ac2954effb7ad645df6.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
這個Lucene使用java寫成的，其實就是個jar包，我們引入之後就可以使用這個Lucene的API。而ES就是基於Lucene的二次開發，對其API進行進一步封裝：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a7a5b6e56b784939804f056217b9eec9.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


**倒排索引基礎概念：**

先了解傳統MySQL的正向索引：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/812d954caf544561a674431c069ab148.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
倒排索引基本概念：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/56cc6d54606e474a8c500aca48715e90.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

這個倒排索引其實和生活中字典相當像，你拿到一本字典的目錄，肯定不會傻到先找頁碼，你肯定是先大略看一眼目錄的關鍵字，然後找到關鍵字之後，去看關鍵字旁邊的頁碼，最後再根據頁碼翻到書對應的那一頁。

**倒排索引其實就是上面的例子。**

然而MySQL這種正向索引，就是基於文件id創建索引，查詢詞條的時候必須先找到文件，然後根據文件内容判斷是否包含詞條。

倒排索引正式一點的說法就是：對文件内容分詞，對詞條創建索引，並紀錄詞條所在文件的資訊，查詢時先根據詞條查詢文件id，然後根據id找到該文件。


**文件和詞條的概念：**

每一條資料就是一個文件，對文件的内容分詞，得到的詞語就是詞條。

**ES 和 MySQL 概念對比**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c78cf0e77d71431aad5c728d25c21218.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/315c4aba06ce4899bfb3931e2e7f5220.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)



## 11.2 安裝部署ES

<font color="#35BDB2">**見課前資料的：安裝elasticsearch.md**</font>



使用docker容器化部署，這裡針對啟動容器命令解析一下：



> -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" 配置堆内存（JVM）。因為ES底層是java實現的，所以要配置jvm内存大小。預設值是1T，對於輕量級服務器太大了，所以適當减少為512M(但是不能再弄少了，再少的话可能跟著影片走，會出現内存不足的問題)
>
> -e "discovery.type=single-node"	單點模式運行（區別於集群模式運行）
>
> 兩個-v參數：資料卷挂載，分別是資料保存目錄(data)，和插件目錄(plugins)
>
> --network es-net 將ES容器加入到剛剛創建的docker網络中
>
> -p 9200:9200 和 -p 9300:9300   是暴露的port，9200是用戶訪問的http協議port，9300是ES容器節點互連的port
>
> elasticsearch:7.12.1 鏡像名稱

```shell
docker run -d \
	--name es \
    -e "ES_JAVA_OPTS=-Xms512m -Xmx512m" \
    -e "discovery.type=single-node" \
    -v es-data:/usr/share/elasticsearch/data \
    -v es-plugins:/usr/share/elasticsearch/plugins \
    --privileged \
    --network es-net \
    -p 9200:9200 \
    -p 9300:9300 \
elasticsearch:7.12.1
```

* **安裝部署kibana（資料可視化界面）**

  黑馬官方的kibana的tar包有問題，建議自己從docker hub拉下來鏡像。但是拉下來之前要注意 [ES 和 kibana的版本對應關係](https://www.elastic.co/cn/support/matrix#matrix_compatibility)：
  找到對應版本後（我已經找好了），執行命令：

  ```docker pull kibana:7.12.1```

  從官網拉下來，這個過程比較慢，慢慢等



* **什麼是分詞器？為什麼要安裝分詞器？**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e44dda640dd1455e97df3e29501879d1.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/24f6dc46dc4e4bfd8748126a28a1b817.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  分詞器我們選擇IK分詞器（來源於github，專門適配了中文）
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/47600b486b4446e29de7facaf979c567.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  該分詞器的具體安裝也在文件裡有寫。

* **分詞器總結**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/8ed9d0a5aa2f4ad49d72d39391426043.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## [Debug] 停止ES容器（或是重启Linux）後，如何恢復Docker網络：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b80654a90def4bb7bdf4863391830cd6.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 11.3 索引庫操作

<font color=red>先給出ES官方幫助文件地址：</font>
https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html

索引庫相當於MySQL中的Table。具體操作有兩個：

* Mapping映射属性
* 索引庫的CRUD

**先介绍Mapping映射属性：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/3fda7a4827b443e6bfd70d7ccffe2b11.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

* **創建索引庫**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/8f73234bd8c341cba45615494d2cd1ce.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  **一個簡單的創建索引庫的語句：**

```json
# 創建索引庫
PUT /heima
{
  "mappings": {
    "properties": {
      "info": {
        "type": "text",
        "analyzer": "ik_smart"
      },
      "email": {
        "type": "keyword",
        "index": false
      },
      "name": {
        "type": "object",
        "properties": {
          "firstName": {
            "type": "keyword"
          },
          "lastName": {
            "type": "keyword"
          }
        }
      }
    }
  }
}
```

* **查看、修改、刪除索引庫**

查看索引庫：GET /索引庫名
刪除索引庫：DELETE /索引庫名

修改索引庫從設計上被禁止了，索引庫和mapping一旦創建無法修改，但是可以加入新的字串 **(該字串必須是全新的字串)** 。

它們的語法如下：

```json
# 查詢
GET /heima

# 修改（必須加入一個全新的字串）
PUT /heima/_mapping
{
  "properties":{
    "age":{
      "type": "integer"
    }
  }
}

# 刪除
DELETE /heima

```

## 11.4 文件操作

索引庫相當於資料庫的table，文件就相當於資料庫的行。

* **加入文件**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7f7d436f65554018a6441ba2f05c2164.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

```json
# 插入一個文件
POST /heima/_doc/1
{
  "info": "黑馬程序员java講师",
  "email": "112837@qq.com",
  "name":{
    "firstName":"云",
    "lastName":"赵"
  }
}
```

* **查看、刪除文件**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c583a89bb2f845d2a3beb99582298403.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

```json
# 查詢
GET /heima/_doc/1

# 刪除
DELETE /heima/_doc/1
```

每次寫操作的時候，都會使得文件的```"_version" ```字串+1

* **修改文件方式1 全量修改**
  它會刪除舊文件，新增新文件

語法：和新增的語法完全一致，只不過新增是POST，全量修改是PUT
示例：

```json
# 插入一個文件
PUT /heima/_doc/1
{
  "info": "黑馬程序员java講师",
  "email": "112837@qq.com",
  "name":{
    "firstName":"云",
    "lastName":"赵"
  }
}
```

如果id在索引庫裡面不存在，並不會報錯，而是直接新增，如果索引庫存在該紀錄，就會先刪掉該紀錄，然後增加一個全新的。

* **修改文件方式2 增量修改**
  只修改某紀錄的指定字串值
  語法：

```json
# 局部修改文件字串
# 第三行，必須跟一個doc
POST /heima/_update/1
{
  "doc": {  
    "email":"lbwnb@qq.com"
  }
}

```

**文件操作總結**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/692724749e0048ef884288935c848fd3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 11.5 RestClient操作索引庫和文件

* **概念**
  ES官方為各種語言操作ES提供了客戶端API，用來操作ES。其實本质都是組裝ES語句，通過http請求發送給ES。 官方文件地址：[https://www.elastic.co/guide/en/elasticsearch/client/index.html](https://www.elastic.co/guide/en/elasticsearch/client/index.html)
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/118af580fdca4badb41f2baa6e9922aa.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  可以看到有很多語言的版本。

* **案例和程式碼位置**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d3e6458549904580962360da5219d77e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color="#35BDB2">**程式碼位置(大量程式碼寫在測試類中)，該案例需要導入資料庫，資料庫執行腳本位置同程式碼目錄：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a4d03333157b4cb1a707b0c89ff4c96f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

* **編寫DSL語句，創建索引庫（相當與MySQL中建表）**

語句如下：

```json
# 酒店的mapping
PUT /hotel
{
  "mappings": {
    "properties": {
      "id":{
        "type": "keyword"         
      },
      "name":{
        "type": "text"
        , "analyzer": "ik_max_word",
        "copy_to": "all"
      },
      "address":{
        "type": "keyword"
        , "index": false
      },
      "price":{
        "type": "integer"
      },
      "score":{
        "type": "integer"
      },
      "brand":{
        "type": "keyword",
        "copy_to": "all"
      },
      "city":{
        "type": "keyword"
      },
      "starName":{
        "type": "keyword"
      },
      "business":{
        "type": "keyword",
        "copy_to": "all"
      },
      "location":{
        "type": "geo_point"
      },
       "pic":{
        "type": "keyword"
        , "index": false
      },
      "all":{
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}
```

有時候可能會疑惑，同樣的一個文本型字串，有的用text，有的用keyword。到底怎麼選擇呢？首先要了解索引和分詞的概念：

* 索引(參與搜索，排序篩選等操作)
* 分詞（把詞看作一個整體還是把詞用某種規則分開）
  * 比如 ： 上海，北京這種字串，不需要分詞（這種字串在一個整體才有意義，分詞就乱套了）
  * "震驚！卢bw將於2022年複出" 這種就需要分詞搜索，既然要分詞了，肯定要選擇分詞器。
    了解了上面的概念，再看一下下圖（[圖來源於博客园——瘦风的南墙](https://www.cnblogs.com/shoufeng/p/10692113.html)）：
    ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b8f237b1462345179d64e47fde88cd58.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
    **備注1：index如果設置成false，則既不參與索引也不參與分詞。
    備注2：索引庫的id總是被要求成keyword（也就是String）類型，即使資料庫的主鍵id可能是int**

字串參數（用於聚合）：copy to ;
地理位置特殊資料類型：geo_point


**使用RestClient操作文件（索引庫相當於資料庫的table，文件就相當於資料庫的行。），全都寫在demo程式碼中，還是那句话：Java的API本質質都是組裝ES語句，通過http請求發送給ES。**

## 11.6 DSL查詢語法

<font color="red">先給出幫助文件，幫助文件永远是學東西最准確的方式：</font>
[https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)

* **快速入門---簡單查詢：**
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/02fa4129aadb4e5bad8af1c9f5e0cd36.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
  ![在這裡插入圖片描述](https://img-blog.csdnimg.cn/6d5c5342bf5448a382f6422ed306ae1e.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/cb4a90e4c5fe4e119326d3cc5eddad33.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

全文檢索查詢例：

```json
# match 和 multi_match
GET /hotel/_search
{
  "query": {
    "match": {
      "address": "如家外滩"
    }
  }
}
GET /hotel/_search
{
  "query": {
    "multi_match": {
      "query": "外滩如家",
      "fields": ["brand","name","business"]
    }
  }
}

```

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c8b4e3be70134d0e994b8e2e488ed195.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


精確查詢例：

```json
# 精確查詢（term查詢）
GET /hotel/_search
{
  "query": {
    "term": {
      "city": {
        "value": "上海"
      }
    }
  }
}

# 精確查詢(范围range)
GET /hotel/_search
{
  "query": {
    "range": {
      "price": {
        "gte": 100,
        "lte": 300
      }
    }
  }
}
```

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a43a98b0b86845cfb518cdb2e980399b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ed0c14ff4f314be0a3394d0c871763d9.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

地理查詢例：

```json
# distance查詢
GET /hotel/_search
{
  "query": {
    "geo_distance":{
      "distance": "5km",
      "location": "31.21, 121.5"
    }
  }
}
```

* **快速入門---打分算法：**


**打分算法（重點）：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/9229ac2c2e074862b4bc5920325e6f0b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
對預設算分方式進行修改：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/fc8abb02838242b2b41a15aef46a138a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**組合查詢-function score 對應的Java RestClient程式碼：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/29f12161630e406cb50c815b088ff8cb.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/63dae70fa5c04db1982cd1dd5b66b553.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
上面例子的查詢語句：

```json
GET /hotel/_search
{
  "query": {
    "function_score": {
      "query": {
        "match": {
          "address": "外滩"
        }
      },
      "functions": [
        {
          "filter": {
            "term": {
              "brand": "如家"
            }
          },"weight": 10
        }
      ]
    }
  }
}


```

* **快速入門---複合查詢：**

複合查詢可以將其它簡單查詢組合起來，實現更複雜的搜索邏輯。

**Boolean Query**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b2dde62523f34b37be5920e74ab2a2c4.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**注意，算分條件越多，性能就會越差。所以能使用filter的就別使用must，能不算分就不算分**

**案例：搜索名字包含“如家”，價格不高於400，在坐標31.21，121.5周围10km范围内的酒店**
參考答案：

```json
GET /hotel/_search
{
  "query": {
    "bool": {
      "must": [
        {"match": {
          "name": "如家"
        }}
      ],
      "must_not": [
        {"range": {
          "price": {
            "gt": 400
          }
        }}
      ],
      "filter": [
        {
          "geo_distance": {
            "distance": "100km",
            "location": {
              "lat": 31.21,
              "lon": 121.5
            }
          }
        }
      ]
    }
  }
}
```

* **快速入門---搜索結果處理：**

搜索結果的處理主要包括**排序、分頁、高亮**。預設ES是根據得分排序的，但是你如果指定了按某種字串排序，就會按你指定的方法排序。

<font color=green>**A.排序**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/1e52d0f9dcc94dec877621b5db6caf2f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
案例：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e4d99d436623458782a3068a1cd48fe9.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
查詢語句實現：

```json
# sort排序
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "score": "desc"
    },
    {
      "price": "asc"
    }
  ]組合查詢-function score 對應的Java RestClient程式碼：

}
```



案例2：

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4283212e06bd475e875b9066a0776df7.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

查詢語句實現2：

```json
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 31.03,
          "lon": 121.61
        }, 
        "order": "asc"
        , "unit": "km"
      }
    }
  ]
}

```

**地理位置排序對應的java restclient程式碼：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/dd35fa91230445af808a900ddc30dafb.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


注意：一旦指定了某種排序之後，ES就會放弃打分。因為打分沒意義了：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/946688ae22094adc843c35eac1774a41.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color=green>**B.分頁**</font>

ES預設情况只返回10條資料，如果想返回更多條資料，則需修改分頁參數。

分頁語法（有點像MySQL的limit）：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/197fba5a1a48439196fcbc8abff4101f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

示例：

```json
GET /hotel/_search
{
  "query": {
    "match_all": {}
  },
  "sort": [
    {
      "price": "asc"
    }
  ],
  "from": 20
  , "size": 5
}

```

**分頁出現的問題：ES底層是倒排索引，不利於分頁，所以分頁查詢是一種邏輯上的分頁。比如現在要查從990開始，截取10條資料（990～1000這10條），對ES來講，是先查出來0～1000條資料，查出來之後邏輯分頁截取10條給你。這麼做如果是單體，最多只是效率問題，但是如果是集群，就會坏事。如下圖所示：**

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a31e485aa2494ef8afb6edb516d898fc.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
針對只能查詢10000條結果的解决方案：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/5aae009cf3ff43a9bcdea4efe79fd6cd.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color=green>**C.高亮**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/da5f2586b1424e27bf01ebf0d9f30e4a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
示例：

```json
# 高亮查詢,預設情况下ES搜索字串必須與高亮字串一致
GET /hotel/_search
{
  "query": {
    "match": {
      "name": "如家"
    }
  },"highlight": {
    "fields": {
      "name": {
        
      }
    }
  }
}

```

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a1eb38f7e1e6483599c5ed837e1a20d1.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**總結：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/afc2dae21e7843a18b9a9715b3ba7db3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 11.7 Java RestClient查詢語法

要構建查詢條件，只要記住一個類：QueryBuilders。
要構建搜索DSL，只需記住一個API：SearchRequest的source()方法（支援鏈式編程）

<font color="#35BDB2">**核心程式碼位置：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/fd8aff9154de4d30888a99231a411a55.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


這裡只有一個注意點：高亮結果的解析，比較麻烦。程式碼要配合下圖理解：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/9d85524ad3d646d7949f87ec57e25f7c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

## 11.8 ES综合案例：黑馬旅游

<font color="#35BDB2">**程式碼位置：就是11.7那個類，直接啟動SpringBoot主啟動類，然後訪問localhost:8089即可訪問到前端頁面**</font>

**要實現的功能：**

* 酒店搜索和分頁
* 酒店結果過濾
* 我周邊的酒店
* 酒店競價排名

<font color = red>影片可能出現的bug：</font>

**bug1 : 如果前端顯示异常（搜索不生效），根據前端debug資訊，修改index.html的第417行程式碼修改成如下圖所示：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/6ed22c19ee134d7db2487297aa1e3c02.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**bug2: 黑馬旅游網的酒店競價排名實現不了**

由於在影片裡創建索引庫裡並沒有創建isAD這個字串，我們需要手動追加該字串。在kibana控制台執行如下程式碼即可修複：

```json
# 給索引庫新增一個叫isAD的字串，類型是boolean類型
PUT /hotel/_mapping
{
  "properties":{
    "isAD":{
      "type": "boolean"
    }
  }
}

# 給索引庫id為45845的紀錄賦值，讓其isAD字串為true（用於測試廣告競價排名，該紀錄會靠前）
POST /hotel/_update/45845
{
  "doc": {  
    "isAD":true
  }
}


GET hotel/_doc/45845
```

## 11.9 ES資料聚合

聚合，類似於MySQL的group by（對資料的統計分析和計算）。聚合不能是text類型，不能分詞

聚合一共有几十種，在官方文件可以查到，但是主要分為三大類：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/1a2af6a69b014cdd83a6e6b85b51b8f5.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
管道聚合 可以理解為linux的 ```| ```


<font color=green>**1、Bucket聚合**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/ae0b731aadf04285864803433c3ddfaf.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
查詢實例：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7a2930bcb56b4ad5bf7957c6762f5d52.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**上圖圖例的結果是由count進行降序排列的，如果想讓其升序排列，只需如下程式碼：**

```json
# 聚合功能
GET hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand",
        "size": 10,
        "order": {
          "_count": "asc"  #結果按照count升序排列
        }
      }
    }
  }
}
```

**限定聚合范围：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/abceccff540b4672b25484a6b3347dcc.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color=green>**2、Metrics聚合**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b07cac5c1e8e4089909e1d88e493115d.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
示例：

```json
# 嵌套聚合metric
GET hotel/_search
{
  "size": 0,
  "aggs": {
    "brandAgg": {
      "terms": {
        "field": "brand",
        "size": 10,
        "order": {
          "scoreAgg.avg": "asc"   # 根據下面的子聚合結果的avg進行升序排序
        }
      },
      "aggs": {
        "scoreAgg": {
          "stats": {
            "field": "score"
          }
        }
      }
    }
  }
}
```


<font color="#35BDB2"> **使用Java Restclient實現上面几種聚合方式，位置如下：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4ffddee1ed8d4a6684d9b3ab0f55e719.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


Java Restclient對應Json的圖例：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e5a68600a1d047768461802ee34403f6.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
Java程式碼對應結果解析的圖例：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/cad994cb410d48e39c2170c01c3dd571.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color=green>**3、聚合案例：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/1b87f0c83b954fbda7c515c01156e5b1.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2"> **案例位置同上面的 ES综合案例：黑馬旅游**</font>



## 11.10 ES資料補全

比如你在京東輸入 sj 這兩個字母，搜索框就會猜測出你想輸入手機。這個就是資料補全

<font color = green>**安裝資料補全分詞器：**</font>

分詞器在課前資料裡有
![2](https://img-blog.csdnimg.cn/d61478102c474f80ac887905d4f8dba5.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
測試你的分詞器是否生效：

```json
POST _analyze
{
  "text": ["卢本伟"],
  "analyzer": "pinyin"
}
```

<font color = green>**自定義配置分詞器：**</font>
概念：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/498f57dfffdb4b9fb9051921a314674f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a19cc8d123dd41bdb72311be35cd829a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/6962ae0af8f940a9be873a3eb5bcb3c8.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)




將下圖位置的自定義配置分詞器的第一段粘贴至kibana控制台，即可完成自定義配置：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/7acce0deaf1c44409ca65fa6bedd10d1.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color = green>**Completion Suggester查詢實現自動補全：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4f8fd74fbcc54fb69b077c901d5348fc.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

Completion Suggester語法：

```json
// 自動補全查詢
GET /test/_search
{
  "suggest": {
    "title_suggest": {
      "text": "s", // 關鍵字
      "completion": {
        "field": "title", // 補全字串
        "skip_duplicates": true, // 跳過重複的
        "size": 10 // 獲取前10條結果
      }
    }
  }
}
```


**總結：**
自動補全對字串的要求：
類型是completion類型；字串值是多詞條的數組。


![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d8bd52cfcbb7404b9bcaab2cca653b4a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color = green>**案例：實現hotel索引庫的自動補全、拼音搜索功能：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/08119d87dd8f4aa9b2df738ec5a1e977.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
找到下圖位置，複製粘贴進kibana控制台並且執行（這一步是重建酒店資料索引庫，在此之前要刪掉原有的酒店資料索引庫）：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/081a98285e09459ea27608dd6825ee8f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**注意事项：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/f4084449413d4ae09d3d090f382dc56a.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**在Java程式碼中重新定義轉換實體的操作，定義一個新的字串suggestion，並且在kibana控制台進行測試：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/72fa9af805f14716bc938552d67fd2b9.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**經過上面一番操作後，類型為completion類型的suggestion字串就有了我們想要自動補全的例子，然後執行下面的查詢語句：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/17f7f8a2dafb4ae2b858dad9896c04ef.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**至此，自動補全、拼音搜索的demo已成功展示！**

<font color = green>**對上圖的DSL語句在Java RestAPI裡面進行發送：**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/296f7da2bd8644adbc992f65ab9e941d.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/4bdbdcbed11b45348910d9a545b6330b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color="#35BDB2"> **使用Java Restclient實現上面自動補全方式，位置如下：**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/bbfff38280db4fcdb8c152205751b379.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
案例效果：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/dd6532ecf21b49869765b150728469fe.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


## 11.11 ES與MySQL之间資料同步（面試常問）

<font color=green>概念</font>

ES中的酒店資料來自於MySQL索引庫，因此mysql資料發生改变時，ES的值也會跟著改变，這個就是ES和MySQL的資料同步。

思考：在微服務中，操作MySQL的業務和操作ES的業務可能在不同的微服務上，這種情况應該怎麼實現資料同步呢？

**解决方案：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/e062fb55290749ee9d242edaca921856.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/a9ca39bfe3a94e05a9f7cf9da21d3f88.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/5097975d4dda4a899b54c023afc41d5c.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color=green>**案例：利用MQ實現mysql與es的資料同步**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b8da451311bb4af2ae03a34e535d5255.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
**思路：**
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/c194c76f31e341a8abcac23a22c59cd8.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color="#35BDB2"> **資料同步案例後台管理頁面程式碼位置如下圖（資料庫就用之前的ES综合案例：黑馬旅游）：**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/51fe4795bc8744879db54376714ff9a5.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
<font color="#35BDB2"> **資料同步案例前端顯示程式碼就是之前的ES综合案例：黑馬旅游。前後端的微服務是分離的，port號也不同。**</font>
**實際上，這個專案hotel-admin專案相當於 supplier ，负责發送資料庫增刪改消息；hotel-demo(之前的黑馬旅游前端專案)相當於 consumer ，负责監聽消息並更新ES中的資料。**

**這樣就實現了在微服務中，操作MySQL的業務和操作ES的業務在不同的微服務上的跨服務資料同步**

用心跟著程式碼走，這個案例是完全可以做完並實現**影片全部功能**的，沒有一句废话多余。

## 11.12 搭建高可用ES集群

<font color=green>**概念**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/fc48643142594e20971dffe703c44e0f.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

<font color=green>**搭建ES集群**</font>

<font color="#35BDB2">**位置同之前的elasticsearch.md，找到該文件第四節:部署ES集群**</font>

<font color=green>**集群脑裂問題**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/1ca5b70d07034da5a0f6147fc8289baf.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/3c83060e1b8d4d1ca2c9c92cc9dfdaa3.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
脑裂問題：一個集群出現了2個主節點：
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/723f3eb9e31e4d04a3dc73eac4563e69.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b1c04e6322ef42b8be7ceb00a640833b.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


<font color=green>**集群分布式存储和分布式查詢**</font>
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/d49d5cf987a24abb92c9cc29fa521064.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/b11c659312e64c6ab9861ac35224d459.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)
![在這裡插入圖片描述](https://img-blog.csdnimg.cn/33a84c29c3f0493cbb9b31cf332790c8.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)


<font color=green>**集群故障轉移**</font>

![在這裡插入圖片描述](https://img-blog.csdnimg.cn/1385287e2a0241eeaa690bdbbcfc4630.png?x-oss-process=image/watermark,type_ZHJvaWRzYW5zZmFsbGJhY2s,shadow_50,text_Q1NETiBAQOWkp-WQiQ==,size_20,color_FFFFFF,t_70,g_se,x_16)

**集群故障轉移總結：**

* Master掛掉後，EligibleMaster選舉為新的主節點
* master節點監控分片，節點狀態，將故障節點的分片轉移到正常節點，確保資料安全。

# 後記

黑馬 SpringCloud 2021 基礎篇筆記和程式碼已更新完畢，不得不說黑馬的這套課程的確是良心之作，而且官方居然還開源出來讓大家都可以學習，實在是難能可贵。

**如果大家在學習基礎篇的同時有疑問，歡迎在评论區讨论和留言，也可以關注我，日後我還會陸續更新完高級篇和面試篇。**



