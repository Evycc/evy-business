CREATE TABLE IF NOT EXISTS `trace_slow_sql`
(
    `tss_id`              bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tss_req_ip`          varchar(15)         DEFAULT NULL COMMENT '请求机器IP',
    `tss_slow_sql`        varchar(10240)      DEFAULT NULL COMMENT '慢sql',
    `tss_take_time`       int(8)              DEFAULT NULL COMMENT '慢sql耗时,单位ms',
    `tss_explain`         varchar(1200)       DEFAULT NULL COMMENT '优化建议',
    `tss_explain_content` varchar(1000)       DEFAULT NULL COMMENT 'mysql执行计划explain',
    `gmt_create`          datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`          datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tss_id`),
    KEY `tss_index_take_time` (`tss_take_time`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000000084
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE慢SQL记录表';

CREATE TABLE IF NOT EXISTS `trace_services_info` (
                                       `tsi_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
                                       `tsi_service_bean_name` varchar(60) DEFAULT NULL COMMENT '服务名bean名称,单个,手动维护',
                                       `tsi_service_name` varchar(60) DEFAULT NULL COMMENT '服务名,单个,应用启动时更新',
                                       `tsi_service_path` varchar(600) DEFAULT NULL COMMENT '服务路径,格式:方法名@路径,通过|分割,多个,应用启动时更新',
                                       `tsi_provider` varchar(60) DEFAULT NULL COMMENT '服务服务者应用名,单个,手动维护',
                                       `tsi_consumer` varchar(60) DEFAULT NULL COMMENT '服务消费者应用名,通过|分割,多个,手动维护',
                                       `tsi_provider_names` varchar(240) DEFAULT NULL COMMENT '服务发布者应用名@IP,多个,通过|分割,应用启动时更新',
                                       `tsi_consumer_names` varchar(240) DEFAULT NULL COMMENT '服务消费者应用名@IP,多个,通过|分割,应用启动时更新',
                                       `tsi_timeout` int(11) DEFAULT '30000' COMMENT '服务调用超时时间,单位ms,默认30s',
                                       `gmt_modify` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
                                       `gmt_create` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
                                       PRIMARY KEY (`tsi_id`),
                                       UNIQUE KEY `tsi_service_bean_name` (`tsi_service_bean_name`,`tsi_provider`),
                                       KEY `tsi_index1_p` (`tsi_provider`),
                                       KEY `tsi_index2_m` (`tsi_consumer`)
) ENGINE=InnoDB AUTO_INCREMENT=1000007965 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='TRACE_服务鉴权信息表';

CREATE TABLE IF NOT EXISTS `trace_redis_health`
(
    `trh_id`                         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `trh_app_ip`                     varchar(24)         DEFAULT NULL COMMENT '应用机器ip',
    `trh_redis_ip`                   varchar(24)         DEFAULT NULL COMMENT '当前机器IP:端口',
    `trh_redis_flag`                 char(1)             DEFAULT '0' COMMENT '主从标志,0主,1从',
    `trh_slave_ip`                   varchar(260)        DEFAULT NULL COMMENT '从机IP,最多记录十台机器',
    `trh_cluster_type`               varchar(12)         DEFAULT NULL COMMENT 'redis类型',
    `trh_rdb_open`                   tinyint(1)          DEFAULT '0' COMMENT 'redis是否开启rdb,1:是,0:否',
    `trh_rdb_file`                   varchar(30)         DEFAULT NULL COMMENT 'redis RDB文件名',
    `trh_rdb_save_type`              varchar(100)        DEFAULT NULL COMMENT 'redis RDB持久化规则',
    `trh_aof_open`                   tinyint(1)          DEFAULT '0' COMMENT 'redis是否开启aof,1:是,0:否',
    `trh_aof_file`                   varchar(30)         DEFAULT NULL COMMENT 'redis AOF文件名',
    `trh_aof_save_type`              varchar(12)         DEFAULT NULL COMMENT 'redis AOF持久化规则',
    `trh_aof_rdb_open`               tinyint(1)          DEFAULT '0' COMMENT 'redis是否开启混合持久化,1:是,0:否',
    `trh_memory_count`               varchar(14)         DEFAULT '0' COMMENT 'redis服务器总内存,单位kb',
    `trh_memory_available_count`     varchar(14)         DEFAULT '0' COMMENT 'redis占用内存,单位kb',
    `trh_memory_peak`                varchar(14)         DEFAULT '0' COMMENT 'redis内存峰值,单位kb',
    `trh_memory_fragmentation_ratio` varchar(4)          DEFAULT '0' COMMENT 'redis内存碎片率,used_memory_rss/used_memory,大于1,表示分配的内存超过实际使用的内存,数值越大,碎片率越严重,小于1时,表示发生了swap,即可用内存不够,used_memory大于1G时有参考价值,建议开启activedefrag yes压缩回收内存',
    `trh_keyspace_ratio`             varchar(4)          DEFAULT '0' COMMENT 'redis命中率,keyspace_misses/keyspace_hits,0.9表示命中率90%',
    `trh_keys_count`                 varchar(80)         DEFAULT '0' COMMENT 'redis key总数',
    `trh_last_rdb_status`            varchar(10)         DEFAULT 'ok' COMMENT 'redis最近一次rdb持久化状态,ok:成功',
    `trh_last_aof_status`            varchar(10)         DEFAULT 'ok' COMMENT 'redis最近一次aof持久化状态,ok:成功',
    `trh_last_fork_usec`             varchar(14)         DEFAULT '0' COMMENT 'redis最近一次fork阻塞时间,单位:微秒',
    `trh_conn_total_count`           varchar(6)          DEFAULT '0' COMMENT 'redis最大连接数',
    `trh_conn_cur_count`             varchar(6)          DEFAULT '0' COMMENT 'redis当前连接数',
    `trh_conn_block_count`           varchar(6)          DEFAULT '0' COMMENT 'redis阻塞连接数',
    `trh_log_path`                   varchar(200)        DEFAULT NULL COMMENT '当前机器redis日志路径',
    `trh_config_path`                varchar(100)        DEFAULT NULL COMMENT '当前机器redis配置文件路径',
    `trh_sentinel_monitor`           varchar(240)        DEFAULT NULL COMMENT 'redis对应的sentinel',
    `trh_sentinel_config_path`       varchar(1000)       DEFAULT NULL COMMENT 'sentinel配置文件路径,以||分割',
    `gmt_create`                     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`                     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`trh_id`),
    UNIQUE KEY `trh_redis_ip` (`trh_redis_ip`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000054012
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE_Redis健康监控表';

CREATE TABLE IF NOT EXISTS `trace_mq_flow`
(
    `tmf_id`                        bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tmf_topic`                     varchar(24)         DEFAULT NULL COMMENT 'topic',
    `tmf_req_ip`                    varchar(15)         DEFAULT NULL COMMENT '生产者IP',
    `tmf_tag`                       varchar(24)         DEFAULT NULL COMMENT 'tag',
    `tmf_msg_id`                    varchar(64)         DEFAULT '0' COMMENT 'msgID',
    `tmf_mq_content`                blob COMMENT 'MQ消息体',
    `tmf_resp_ip`                   varchar(15)         DEFAULT NULL COMMENT '消费者IP',
    `tmf_producer_start_timestamp`  datetime(3)         DEFAULT NULL COMMENT 'msg请求开始时间戳,精确到ms',
    `tmf_consumer_end_timestamp`    datetime(3)         DEFAULT NULL COMMENT 'msg请求结束时间戳,精确到ms',
    `tmf_consumer_takeup_timestamp` int(8)              DEFAULT NULL COMMENT '消费耗时,单位ms',
    `gmt_create`                    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`                    datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tmf_id`),
    UNIQUE KEY `tmf_index3_u_msg_id` (`tmf_msg_id`),
    KEY `tmf_index2_topic` (`tmf_topic`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000009610
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE_MQ链路表';

CREATE TABLE IF NOT EXISTS `trace_http_flow`
(
    `thf_id`            bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `thf_req_ip`        varchar(15)         DEFAULT NULL COMMENT '请求方机器IP',
    `thf_url`           varchar(100)        DEFAULT NULL COMMENT '响应方url',
    `thf_takeup_time`   int(8)              DEFAULT NULL COMMENT 'http耗时,单位ms',
    `thf_req_timestamp` datetime            DEFAULT NULL COMMENT 'http最近一次请求时间',
    `thf_result_sucess` tinyint(1)          DEFAULT NULL COMMENT 'http请求结果,1成功,0失败',
    `thf_input`         varchar(1000)       DEFAULT NULL COMMENT 'http请求参数',
    `thf_result`        varchar(1000)       DEFAULT NULL COMMENT 'http响应',
    `gmt_create`        datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`        datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`thf_id`),
    KEY `thf_index_takeup_time` (`thf_takeup_time`),
    KEY `thf_index2_url` (`thf_url`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000000083
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE_HTTP请求记录表';

CREATE TABLE IF NOT EXISTS `trace_app_thread_info`
(
    `tati_id`                   bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tati_app_ip`               varchar(15)          DEFAULT NULL COMMENT '应用ip',
    `tati_thread_id`            varchar(8)           DEFAULT NULL COMMENT '应用线程ID',
    `tati_thread_name`          varchar(100)         DEFAULT NULL COMMENT '应用线程名称',
    `tati_thread_status`        varchar(20)          DEFAULT NULL COMMENT '应用线程状态',
    `tati_thread_avail_byte`    varchar(20) NOT NULL COMMENT '线程占用大小,单位byte',
    `tati_thread_blocked_count` int(8)               DEFAULT '0' COMMENT '死锁阻止线程进入的总数',
    `tati_thread_blocked_mtime` varchar(14)          DEFAULT '0' COMMENT '死锁累计运行时间,单位:毫秒',
    `tati_thread_blocked_name`  varchar(100)         DEFAULT NULL COMMENT '正在等待进行的,死锁的线程名',
    `tati_thread_blocked_id`    varchar(8)           DEFAULT NULL COMMENT '正在等待进行的,死锁的ID',
    `tati_thread_waited_count`  int(8)               DEFAULT '0' COMMENT '线程等待累计总数',
    `tati_thread_waited_mtime`  varchar(14)          DEFAULT '0' COMMENT '线程等待累计运行时间,单位:毫秒',
    `tati_thread_max_count`     int(8)               DEFAULT '0' COMMENT 'Java虚拟机启动以来创建和启动的线程总数',
    `tati_thread_stack`         blob COMMENT '应用线程状态,String类型占用字节计算:40+2*len,34行堆栈信息大约6kb,Blob最大65kb',
    `gmt_create`                datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`                datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `tati_thread_start_mtime`   varchar(14)          DEFAULT '0' COMMENT '线程运行时间,单位:毫秒',
    PRIMARY KEY (`tati_id`),
    UNIQUE KEY `tati_index1_u_ip_tid_name` (`tati_app_ip`, `tati_thread_id`, `tati_thread_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1004966130
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE_应用线程信息表';

CREATE TABLE IF NOT EXISTS `trace_app_mermory_info`
(
    `tami_id`                     bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tami_app_ip`                 varchar(15)         DEFAULT NULL COMMENT '应用ip',
    `tami_cpu_count`              int(11)             DEFAULT NULL COMMENT '应用cpu可用处理数',
    `tami_cpu_load`               varchar(6)          DEFAULT NULL COMMENT '应用cpu使用率%',
    `tami_sys_memory`             varchar(14)         DEFAULT NULL COMMENT '应用系统内存,单位kb',
    `tami_sys_avail_memory`       varchar(14)         DEFAULT NULL COMMENT '应用系统可用内存,单位kb',
    `tami_app_use_memory`         varchar(14)         DEFAULT NULL COMMENT '应用占用内存,单位kb',
    `tami_app_heap_max_memory`    varchar(14)         DEFAULT NULL COMMENT '应用堆内存,最大内存,单位kb',
    `tami_app_heap_min_memory`    varchar(14)         DEFAULT NULL COMMENT '应用堆内存,最小内存,单位kb',
    `tami_app_heap_use_memory`    varchar(14)         DEFAULT NULL COMMENT '应用堆内存,占用内存,单位kb',
    `tami_app_nonheap_max_memory` varchar(14)         DEFAULT NULL COMMENT '应用非堆内存,最大内存,单位kb',
    `tami_app_nonheap_min_memory` varchar(14)         DEFAULT NULL COMMENT '应用非堆内存,最小内存,单位kb',
    `tami_app_nonheap_use_memory` varchar(14)         DEFAULT NULL COMMENT '应用非堆内存,占用内存,单位kb',
    `gmt_create`                  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`                  datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tami_id`),
    KEY `tami_index1_ip` (`tami_app_ip`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000060363
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='TRACE_应用内存信息表';

CREATE TABLE IF NOT EXISTS `td_router`
(
    `tr_id`             bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tr_router_id`      varchar(30) NOT NULL COMMENT '路由器(RouteDefinition)id',
    `tr_router_uri`     varchar(30) NOT NULL COMMENT '路由器(RouteDefinition)uri,lb://服务名,forward:#跳转链接#',
    `tr_predicate_name` varchar(20) NOT NULL COMMENT '路由器类型(PredicateDefinition),如Path,Query,支持|分割',
    `tr_predicate_args` varchar(200)         DEFAULT NULL COMMENT 'PredicateDefinition参数,JSON形式,支持|分割',
    `tr_filters_name`   varchar(30)          DEFAULT NULL COMMENT 'FilterDefinition,如StripPrefix,支持|分割',
    `tr_filters_args`   varchar(200)         DEFAULT NULL COMMENT 'FilterDefinition参数,JSON形式,支持|分割',
    `tr_pattern`        varchar(30) NOT NULL COMMENT '路由规则',
    `tr_order`          varchar(3)  NOT NULL DEFAULT '0' COMMENT '优先级,数值越低优先级越高',
    `gmt_create`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`        datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tr_id`),
    UNIQUE KEY `index_tbs_batch_name` (`tr_router_id`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000000009
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='网关动态路由信息表';

CREATE TABLE IF NOT EXISTS `td_batch_msg`
(
    `tbs_id`           bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tbs_batch_name`   varchar(30) NOT NULL COMMENT '批次接口名',
    `tdb_batch_status` varchar(20) NOT NULL COMMENT '批次执行状态 11:MQ发送成功 61:批次执行中 71:批次执行成功',
    `tdb_add_date`     datetime             DEFAULT NULL COMMENT '批次交易日期',
    `gmt_create`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`       datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tbs_id`),
    KEY `index_gmt_create` (`gmt_create`),
    KEY `index_tbs_batch_name` (`tbs_batch_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000000001
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='批次执行信息表';

CREATE TABLE IF NOT EXISTS `public_deploy_info`
(
    `pdi_id`                  bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pdi_deploy_seq`          varchar(64)         DEFAULT NULL COMMENT '部署配置信息唯一序列,一对多pdi_seq',
    `pdi_seq`                 varchar(64)         DEFAULT NULL COMMENT '部署记录序列,多条',
    `pdi_user_seq`            varchar(64)         DEFAULT NULL COMMENT '用户信息唯一序列,一对多pdi_deploy_seq',
    `pdi_project_name`        varchar(16)         DEFAULT NULL COMMENT '部署应用项目',
    `pdi_app_name`            varchar(64)         DEFAULT NULL COMMENT '部署应用名称',
    `pdi_git_path`            varchar(100)        DEFAULT NULL COMMENT '项目git路径',
    `pdi_git_brchan`          varchar(64)         DEFAULT NULL COMMENT '项目git分支名',
    `pdi_stage_flag`          varchar(4)          DEFAULT NULL COMMENT 'jar部署阶段 0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败 2a:服务启动成功 2b:服务启动中 2c:服务启动失败',
    `pdi_switch_junit`        char(1)             DEFAULT '1' COMMENT '编译时是否进行junit单测,1否 0是',
    `pdi_switch_batch_deploy` char(1)             DEFAULT '1' COMMENT '编译时是否进行分批部署,1否 0是',
    `pdi_jar_path`            varchar(120)        DEFAULT NULL COMMENT '编译后jar包路径',
    `pdi_jvm_param`           varchar(240)        DEFAULT NULL COMMENT '部署jvm参数',
    `pdi_target_host`         varchar(120)        DEFAULT NULL COMMENT '部署目标服务器,||分割',
    `pdi_remarks`             varchar(120)        DEFAULT NULL COMMENT '部署备注',
    `pdi_build_log`           varchar(4096)       DEFAULT NULL COMMENT '编译部署失败记录大字段',
    `gmt_create`              datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`              datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`pdi_id`),
    KEY `pem_u_index1` (`pdi_user_seq`, `pdi_seq`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000002680
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='部署信息表';

CREATE TABLE IF NOT EXISTS `public_error_map`
(
    `pem_id`         bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `pem_error_code` varchar(20)         DEFAULT NULL COMMENT '错误码,唯一',
    `pem_error_msg`  varchar(60)         DEFAULT NULL COMMENT '错误描述',
    `gmt_create`     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`     datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`pem_id`),
    UNIQUE KEY `pem_u_index1` (`pem_error_code`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000002624
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='错误码表';

CREATE TABLE IF NOT EXISTS `public_log_flow`
(
    `plf_id`         bigint(20)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `plf_srcSendNo`  varchar(64)          DEFAULT NULL COMMENT '交易流水',
    `plf_code`       varchar(60)          DEFAULT NULL COMMENT '交易接口名',
    `plf_trace_id`   varchar(60) NOT NULL,
    `plf_serverIp`   varchar(64)          DEFAULT NULL COMMENT '服务器IP',
    `plf_reqContent` varchar(2048)        DEFAULT NULL COMMENT '交易正文',
    `plf_errorCode`  varchar(30)          DEFAULT NULL COMMENT '错误码',
    `plf_errorMsg`   varchar(60)          DEFAULT NULL COMMENT '错误信息',
    `gmt_create`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`     datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    `plf_clientIp`   varchar(64)          DEFAULT NULL COMMENT '交易客户端IP',
    PRIMARY KEY (`plf_id`),
    KEY `plf_index_srcSendNo` (`plf_srcSendNo`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000122696
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='公共流水表';

CREATE TABLE IF NOT EXISTS `service_limit_info`
(
    `sli_id`                bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
    `sli_service_bean_name` varchar(60)         DEFAULT NULL COMMENT '服务名bean名称,对应trace_services_info#tsi_service_bean_name',
    `sli_service_name`      varchar(60)         DEFAULT NULL COMMENT '服务名,对应trace_services_info#tsi_service_name',
    `sli_qps_limit`         smallint(6)         DEFAULT '-1' COMMENT '限流qps,大小:0-65535',
    `sli_fallback`          varchar(1000)       DEFAULT NULL COMMENT '降级地址,允许为服务名,固定链接',
    `gmt_create`            datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`            datetime   NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`sli_id`),
    UNIQUE KEY `sli_index1_bean_name` (`sli_service_bean_name`),
    KEY `sli_index2_srv_name` (`sli_service_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1000002665
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='服务限流信息表';

CREATE TABLE IF NOT EXISTS `td_batch`
(
    `tdb_id`          bigint(10)  NOT NULL AUTO_INCREMENT COMMENT '主键',
    `tdb_batch_name`  varchar(30) NOT NULL COMMENT '批次接口名',
    `tdb_batch_topic` varchar(20) NOT NULL COMMENT '批次topic',
    `tdb_batch_tag`   varchar(20) NOT NULL COMMENT '批次tag',
    `tdb_batch_desc`  varchar(30) NOT NULL COMMENT '批次名',
    `tdb_start_time`  datetime             DEFAULT NULL COMMENT '批次最近一次开始时间',
    `tdb_end_time`    datetime             DEFAULT NULL COMMENT '批次最近一次结束时间',
    `gmt_create`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `gmt_modify`      datetime    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
    PRIMARY KEY (`tdb_id`),
    UNIQUE KEY `uid_batch_name` (`tdb_batch_name`)
) ENGINE = InnoDB
  AUTO_INCREMENT = 1003
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci COMMENT ='批次信息表';