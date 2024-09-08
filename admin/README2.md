# xh校友圈
## 介绍

校园社区平台，集帖子、论坛、推文、二手闲置为一体的信息互动平台，为在校师生提供交友、闲置、求助等服务。
项目有帖子、推文、闲置等模块。其中帖子模块的功能已经开发完成。推文和闲置两个模块的评论功能未完善（这两个功能的评论设计和帖子模块的评论功能类似，我偷懒就没做了）


## 技术选型
### 系统环境
1. jdk8
2. Maven3
3. mysql5.7及以上
4. redis3.2及以上
5. rabbitmq
### 主框架
1. Spring Boot 2.7.3
2. Spring Security 5.7.3
3. mybatis-plus 3.5.1

## 模块介绍

### xlf-auth(认证模块)
模块整合了springSecurity框架,目前有3种认证方式(账号密码认证,微信认证,jwt认证),可根据具体的需求进行扩展

![img.png](img.png)

### xlf-common(公共模块)
    放一些常量,注解,枚举,异常,工具类等
### xlf-framework(核心模块)
    做项目的核心配置
### xlf-system(系统模块)
    写业务代码
### xlf-interface(接口模板)
    启动类在这里
## 安装教程
### 准备工作
    JDK >= 1.8 (推荐1.8版本)
    Mysql >= 5.7.0 (推荐5.7版本)
    Redis >= 3.0
    Maven >= 3.0
    rabbitmq(没有版本限制，建议使用docker安装)
### 导入依赖
    使用maven加载依赖（你要说你不会那就过分了）
### 导入数据库
    mysql创建数据库campus（编码建议设置utf8mb4)，然后将campus.sql导入
### 修改配置文件
#### 必要配置
    配置文件在xlf-interface模块下的resources目录下的application.yml
1. 修改数据库连接，编辑xlf-interface模块下的resources目录下的application-druid.yml
```yaml
spring:
        datasource:
        url: jdbc:mysql://localhost:3306/campus?characterEncoding=utf-8&serverTimezone=GMT%2B8
        username: 账号
        password: 密码
        driver-class-name: com.mysql.cj.jdbc.Driver

```

2. 修改小程序配置
```yaml

#小程序登录配置
wx:
  url: https://api.weixin.qq.com/sns/jscode2session
  appId: 从https://mp.weixin.qq.com/获取
  appSecret: 从https://mp.weixin.qq.com/获取
```
3. 修改七牛云oss对象储存设置
```yaml
#七牛云oss上传配置（需要在七牛云的官网获取，每个用户一个月有10G的免费额度(https请求除外))
qiniu:
  accessKey: xxx
  secretKey: xxx
  bucket: xxx
```
4. 修改本地上传配置
```yaml
#本地文件上传配置
upload-plus:
  #  访问根路径
  localUrl: /preview/file
  # 上传根路径
  localPath: D:/test
#  localPath: /home/images
```
5. 修改rabbitmq连接
```yaml
rabbitmq:
    host: xxxx
    port: 5672
    username: xxx
    password: xxx
```
6. 小程序评论订阅的模板配置(看小程序开发文档教程)

在xlf-system模块的com/xlf/system/mq/builder包下配置,这里我用到了建造者模式提高了可读写和可扩展性.稍微读一下包下的类就可以看懂

### 启动后端项目
    启动xlf-interface模块下的XlfInterfaceApplication类里面的main方法
