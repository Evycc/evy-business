# evy-business
领域驱动模型的脚手架，基于SpringCloud

## 1、[command-jar](https://github.com/Evycc/evy-business/tree/master/business/common-jar)
提供通用工具类，如`BaseCommandTemplate（统一对外接口）`，`CommandLog（统一日志工具类）`，`SequenceUtils(基于雪花算法的唯一序列)`，`FtpUtils（文服工具类）`，`MqSender（统一MQ发布者）`，`MqConsumer(统一MQ消费者)`等功能

## 2、[evy-gateway](https://github.com/Evycc/evy-business/tree/master/business/evy-gateway)
提供基于Redis的动态路由等功能

## 3、[evy-registry-center]
erurke注册中心
