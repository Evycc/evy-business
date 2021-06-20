# evy-deploy-centre持续集成中心
## 项目简介
- 通过简单的配置，实现对SpringBoot应用的自动化部署能力
- 提供应用健康信息收集（服务器应用内存、Cpu、线程等信息）
- 提供应用链路跟踪功能（交易链路、MQ链路跟踪）
- 应用中间件健康信息收集（慢SQL、Redis健康信息监控）
- 提供基于SpringCloud Gateway的服务网关能力

## 功能演示网站

## 功能简介
1. 自动化部署及版本回滚
   ![自动化部署](https://images.gitee.com/uploads/images/2021/0505/221312_40adadd2_1842243.jpeg "自动化部署.jpg")

2. 全链路查询
   ![全链路查询](https://images.gitee.com/uploads/images/2021/0505/221329_a6427166_1842243.jpeg "全链路.jpg")

3. 慢SQL查询
   ![慢SQL查询](https://images.gitee.com/uploads/images/2021/0505/221348_989a944b_1842243.jpeg "慢SQL.jpg")

4. 服务鉴权限流
   ![输入图片说明](https://images.gitee.com/uploads/images/2021/0505/221407_c1039cac_1842243.jpeg "服务治理1.jpg")
   ![输入图片说明](https://images.gitee.com/uploads/images/2021/0505/221417_3a826696_1842243.jpeg "服务治理2.jpg")

5. MQ链路查询
   ![MQ链路查询](https://images.gitee.com/uploads/images/2021/0505/221603_f2eceef7_1842243.jpeg "MQ链路查询.jpg")

6. 服务器内存信息监控
   ![服务器内存监控](https://images.gitee.com/uploads/images/2021/0505/221650_a5e916ef_1842243.jpeg "内存信息.jpg")

7. 应用线程信息收集
   ![应用线程信息](https://images.gitee.com/uploads/images/2021/0505/221752_792f9142_1842243.jpeg "线程信息.jpg")

8. Redis健康信息收集
   ![Redis健康信息收集](https://images.gitee.com/uploads/images/2021/0505/221806_e3a7c60d_1842243.jpeg "Redis服务器监控.jpg")

# common-jar通用框架
## 统一框架模板，提供通用服务功能
### 日志模块
1.详细的日志展示
```
2021-05-05 17:45:18.307 [INFO ] [http-nio-8080-exec-10] [traceIdf4399f002fff000] [192.168.152.135] [app.ReviewDeployStatusService#reviewStatus] - Service return ReviewStatusOutDTO{stageFlag='2a'}
2021-05-05 17:45:18.308 [INFO ] [http-nio-8080-exec-10] [traceIdf4399f002fff000] [192.168.152.135] [app.ReviewDeployStatusService#reviewStatus] - End Service Flow--[5ms]
```
2.封装日志工具
```
CommandLog.info("记录到日志流水结果:{}", isSuccess);
2021-05-05 17:45:18.332 [INFO ] [pool-1-thread-3] [traceIdf4399f002fff000] [192.168.152.135] [basic.BaseRabbitMqConsumer=>event.TraceLogEvent#execute] - 记录到日志流水结果:1
```
3.通过增加注解@TraceLog,交易流水自动记录入库
```
@TraceLog
public class AutoDeployAppService extends AutoDeployService {}
```
### MQ模块
1.封装MQ发送及消费功能,实现单向消息、事务消息、定时消息、消息重发等功能。目前暂时支持RabbitMQ

2.发送者示例
```
MQ消息发送 : mqSender.sendAndConfirm(TRACELOG_TOPIC, TRACELOG_TAG, BusinessConstant.EMPTY_STR, json);
```

3.消费者示例
```
MQ消费 : 通过注解@AnnoMqConsumer,并发布为Spring Bean实例
@AnnoMqConsumer : topic = RabbitMQ的exchange tag = RabbitMQ的queue

@Component
@AnnoMqConsumer(listen = {
        @AnnoMqConsumer.AnnoMqConsumerModel(queue = "topic-tracelog", tag = "tag-tracelog"),
        @AnnoMqConsumer.AnnoMqConsumerModel(queue = "topic-tracelog1", tag = "tag-tracelog2")
})
public class AutoDeployAppService extends AutoDeployService {
}
```

### 数据库模块
1.封装数据库工具,支持jdbc及Mybatis,支持批量DDL操作(异常自动回滚)
```
单DDL : DBUtils.selectOne(QRY_STAGE_FOR_SEQ, qryPo);
批量DDL : DBUtils.insertBatch(BATCH_SQL, List);
批量DDL(支持增删改) : DBUtils.batchAny(cList.stream()
                             .map(updatePo -> DBUtils.BatchModel.create(UPDATE_SERVICES_CONSUMER, updatePo, DBUtils.BatchType.UPDATE))
                             .collect(Collectors.toList())
```

### 链路跟踪模块
1.收集应用内存使用率、线程信息,清晰展示线程阻塞、死锁情况

2.查询、收集MQ消息消费链路、耗时情况

3.慢SQL收集、及优化建议

4.Eureka服务发布及消费情况收集(详情见evy-gateway服务网关,需要先进行服务鉴权配置)

5.Redis服务器健康信息收集

6.Http请求耗时及响应信息收集

7.提供实时查询线程死锁，heap dump功能

# evy-gateway服务网关
## 提供动态服务限流降级、服务鉴权、动态服务路由能力

### QPS限流、服务降级、服务鉴权

### 动态服务路由
1. 基于eureka，通过服务码进行动态路由
2. `统一对外调用入口 POST http://localhost:8081/EvyGateway`
   `通过上送参数serviceCode进行路由到对应应用 serviceCode: "evy.trace.threadQry.app"`

# common-agent-jar
## Jvm级Agent包,与common-jar搭配使用，链路跟踪基于该jar包

# 自动化部署注意事项
## 只支持Spring Boot应用，通过jar启动

# 部署指南
## 服务端（持续集成平台evy-deploy-centre）
1. 新建cdadmin用户(cdadmin用户需要有对/cdadmin目录的操作权限)
2. 服务器需要先安装jdk、maven，并设置环境变量
3. 配置ssh，将id_rsa.pub追加到authorized_keys文件中
4. ssh远程执行命令环境变量加载
```
vi /cdadmin/.bashrc
#添加行
. /etc/profile
```
5. 应用启动main类需命名为 com.evy.linlin.start.EvyStartApp
6. 安装ssh
```
#目标文件为 /home/cdadmin/.ssh/id_rsa
ssh-keygent
#需要配置到目标服务器的 /cdadmin/.ssh/authorized_keys 中
/home/cdadmin/.ssh/id_rsa.pub
vi /etc/ssh/ssh_config 

#ssh新机器时,弹出Are you sure you want to continue connecting,影响ssh脚本执行,设置为不提示
StrictHostKeyChecking no
UserKnownHostsFile=/dev/null

```

## 客户端（需要使用持续集成进行管理的应用）
1. 服务器需要先安装jdk，并设置环境变量
2. 新建cdadmin用户(cdadmin用户需要有对/cdadmin目录的操作权限)
3. 配置ssh
```
#目标文件为 /home/cdadmin/.ssh/id_rsa
ssh-keygent
将服务端服务器的id_rsa.pub 配置到 /cdadmin/.ssh/authorized_keys 中
```
