# Ride Sharing Service

### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.3.6/maven-plugin)
* [Spring Boot Actuator](https://docs.spring.io/spring-boot/3.3.6/reference/actuator/index.html)
* [Gateway](https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway-server-mvc.html)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.3.6/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Web](https://docs.spring.io/spring-boot/3.3.6/reference/web/servlet.html)

## 项目介绍
本项目使用微服务框架实现了出租车合乘服务概念的表示，设计了如下三个微服务：

* passenger-management
* driver-management
* billing
### passenger-management
主要功能：
* 用户注册登录
* 提交乘车请求
* 追踪司机位置
* 获取历史账单
* 结账
* 查询司机信息

### driver-management
主要功能：
* 司机注册登录
* 查看请求列表
* 接单
* 更新位置
* 完成行程
* 获取历史账单

### billing
主要功能：
* 计费
* 提供历史账单
* 新增账单

## 微服务间通信
本项目使用RESTful API进行微服务间的通信。

* 在passenger-management服务的查询司机信息功能中和driver-management服务进行通信。
* 在passenger-management服务的获取历史账单和结账功能中和billing服务进行通信。
* 在driver-management服务的完成行程功能中和billing服务进行通信，司机完成行程后自动生成该次行程的账单。

## 网关配置
本项目使用Spring Cloud Gateway进行网关配置。
使用Eureka进行服务发现和注册。

## 测试结果
JUnit单元测试和集成测试还未编写完成……

启动后所有微服务全部成功注册到Eureka。可以正确转发路由，允许前端跨域访问。



##待优化
*用户鉴权：密码加密，使用JWT安全机制进行登录。
*

