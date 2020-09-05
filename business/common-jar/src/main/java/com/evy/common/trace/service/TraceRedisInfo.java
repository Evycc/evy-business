package com.evy.common.trace.service;

import com.evy.common.command.domain.factory.CreateFactory;
import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.db.DBUtils;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.infrastructure.tunnel.po.TraceRedisPO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;
import io.lettuce.core.resource.ClientResources;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnection;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * 链路跟踪<br/>
 * redis : 统计redis内存、集群IP、日志地址、健康情况 | 配置 : evy.trace.redis.flag={0开启|1关闭}<br/>
 * @Author: EvyLiuu
 * @Date: 2020/6/27 16:06
 */
public class TraceRedisInfo {
    /**
     * 监控的redis集合,格式host:port:password<br/>
     * 分隔符:||<br/>
     * password支持enc加密
     */
    private static final String TRACE_REDIS_LIST = "evy.trace.redis.list";
    private static final String REDIS_PRPO = "evy.trace.redis.flag";
    private static final String TEACE_REDIS_INSERT = "com.evy.common.trace.repository.mapper.TraceMapper.redisInfoInsert";
    private static final String SENTINEL_LISTEREN = "__sentinel__:hello";
    private static final String BIND_IP = "bind";
    private static final String LOG_PATH = "logfile";
    private static final String CONFIG_PATH = "config_file";
    private static final String TCP_PORT = "tcp_port";

    private static final String CONN_CLIENTS = "connected_clients";
    private static final String BLOCKED_CLIENTS = "blocked_clients";
    private static final String CONN_MAX = "maxclients";

    //因为RDB文件只用作后备用途，建议只在Slave上持久化RDB文件，而且只要15分钟备份一次就够了，只保留save 900 1这条规则。
    private static final String RDB_SAVE_TYPE = "save";
    private static final String RDB_LAST_SAVE_STATUS = "rdb_last_bgsave_status";
    private static final String RDB_FILE = "dbfilename";
    private static final String AOF_ENABLE = "aof_enabled";
    private static final String AOF_SAVE_TYPE = "appendfsync";
    private static final String AOF_LAST_SAVE_STATUS = "aof_last_bgrewrite_status";
    private static final String AOF_FILE = "appendfilename";
    private static final String AOF_RDB_ENABLE = "aof-use-rdb-preamble";
    private static final String FORK_LAST_USEC = "latest_fork_usec";

    private static final String SYS_MEMORY = "total_system_memory";
    private static final String USED_MEMORY = "used_memory";
    private static final String USED_MEMORY_RSS = "used_memory_rss";
    private static final String MEMORY_PEAK = "used_memory_peak";
    //used_memory_rss/used_memory  used_memory大于1g时具有参考意义
    private static final String MEM_FRAGMENTATION_RATIO = "mem_fragmentation_ratio";

    private static final String CLUSTER_ENABLE = "cluster_enabled";
    private static final String CLUSTER_CONFIG = "cluster-config-file";
    private static final String DB = "db";
    private static final String KEYS_HITS = "keyspace_hits";
    private static final String KEYS_MISS = "keyspace_misses";

    private static final String ROLE = "role";
    private static final String SLAVE = "slave";

    private static HashMap<String, RedisConnection> REDIS_CONN_MAP;
    private static final HashMap<String, RedisConnection> REDIS_SENTINEL_CONN_MAP = new HashMap<>(8);
    private static HashMap<String, String> REDIS_HOST_PASS;

    static {
        initRedisClientList();
        shutdownConn();
    }

    /**
     * 释放连接
     */
    private static void shutdownConn() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            if (!CollectionUtils.isEmpty(REDIS_CONN_MAP)) {
                REDIS_CONN_MAP.values().forEach(RedisConnection::close);
            }
            if (!CollectionUtils.isEmpty(REDIS_SENTINEL_CONN_MAP)) {
                REDIS_SENTINEL_CONN_MAP.values().forEach(RedisConnection::close);
            }
        }));
    }

    /**
     * 构建REDIS_CONN_MAP  key 格式host:port
     *
     * @param host host
     * @param port port
     * @return host:port
     */
    private static String buildRedisMapKey(String host, String port) {
        return host + BusinessConstant.COLON_STR + port;
    }

    /**
     * 初始化配置好的redis连接集合
     */
    private static void initRedisClientList() {
        Optional.ofNullable(AppContextUtils.getForEnv(TRACE_REDIS_LIST))
                .ifPresent(rl -> {
                    String[] list = rl.split(BusinessConstant.SPLIT_DOUBLE_LINE, -1);
                    REDIS_CONN_MAP = new HashMap<>(list.length);
                    REDIS_HOST_PASS = new HashMap<>(list.length);
                    for (String rs : list) {
                        String[] temp = rs.split(BusinessConstant.COLON_STR, -1);
                        String host;
                        String port;
                        String password = BusinessConstant.EMPTY_STR;
                        if (temp.length == 2) {
                            //无密码
                            host = temp[0];
                            port = temp[1];
                        } else if (temp.length == 3) {
                            //存在密码
                            host = temp[0];
                            port = temp[1];
                            password = temp[1];
                        } else {
                            continue;
                        }
                        RedisConnection redisConnection = CreateFactory.returnRedisConn(host, Integer.parseInt(port), password);
                        if (Objects.nonNull(redisConnection) && !redisConnection.isClosed()) {
                            REDIS_CONN_MAP.put(buildRedisMapKey(host, port), redisConnection);
                            REDIS_HOST_PASS.put(buildRedisMapKey(host, port), password);
                        }
                    }
                });
    }

    /**
     * 统计redis服务器健康情况
     */
    public static void executeRedisInfo() {
        try {
            Optional.ofNullable(AppContextUtils.getForEnv(REDIS_PRPO))
                    .ifPresent(flag -> {
                        if (BusinessConstant.ZERO.equals(flag)) {
                            REDIS_CONN_MAP.forEach((k, v) -> {
                                Optional.ofNullable(v)
                                        .ifPresent(redisConnection -> {
                                            if (redisConnection instanceof LettuceConnection && !redisConnection.isClosed()) {
                                                try {
                                                    String all = "ALL";
                                                    String master = "master";
                                                    Properties properties = redisConnection.serverCommands().info(all);

                                                    //获取服务器及从服务器IP信息
                                                    int port = Integer.parseInt(properties.getProperty(TCP_PORT));
                                                    String trhRedisIp = redisConnection.getConfig(BIND_IP).getProperty(BIND_IP);
                                                    String trhRedisFlag = properties.getProperty(ROLE);
                                                    String trhSlaveIp = BusinessConstant.EMPTY_STR;
                                                    if (master.equals(trhRedisFlag)) {
                                                        //如果服务器为master,则获取其名下slave
                                                        trhSlaveIp = getSlavesForPrpo(properties);
                                                    }
                                                    String trhLogPath = redisConnection.getConfig(LOG_PATH).getProperty(LOG_PATH);
                                                    String trhConfigPath = properties.getProperty(CONFIG_PATH);

                                                    //redis连接信息
                                                    String trhConnTotalCount = redisConnection.getConfig(CONN_MAX).getProperty(CONN_MAX);
                                                    String trhConnCount = properties.getProperty(CONN_CLIENTS);
                                                    String trhConnBlockCount = properties.getProperty(BLOCKED_CLIENTS);

                                                    //获取持久化标志
                                                    String trhRdbSaveType = redisConnection.getConfig(RDB_SAVE_TYPE).getProperty(RDB_SAVE_TYPE);
                                                    String trhRdbFile = redisConnection.getConfig(RDB_FILE).getProperty(RDB_FILE);
                                                    boolean trhRdbOpen = !StringUtils.isEmpty(trhRdbSaveType);
                                                    String trhLastRdbStatus = properties.getProperty(RDB_LAST_SAVE_STATUS);
                                                    boolean trhAofOpen = Integer.parseInt(properties.getProperty(AOF_ENABLE, BusinessConstant.ZERO)) != BusinessConstant.ZERO_NUM;
                                                    boolean trhAofRdbOpen = redisConnection.getConfig(AOF_RDB_ENABLE).getProperty(AOF_RDB_ENABLE, BusinessConstant.NO).equals(BusinessConstant.YES);
                                                    String trhAofFile = redisConnection.getConfig(AOF_FILE).getProperty(AOF_FILE);
                                                    String trhAofSaveType = trhAofOpen ? redisConnection.getConfig(AOF_SAVE_TYPE).getProperty(AOF_SAVE_TYPE) : null;
                                                    String trhLastAofStatus = properties.getProperty(AOF_LAST_SAVE_STATUS);
                                                    String trhLastForkUsec = properties.getProperty(FORK_LAST_USEC);

                                                    //获取内存信息
                                                    String trhMemoryCount = properties.getProperty(SYS_MEMORY);
                                                    String trhMemoryAvailableCount = properties.getProperty(USED_MEMORY);
                                                    String trhMemoryPeak = properties.getProperty(MEMORY_PEAK);
                                                    String trhMemoryFragmentationRatio = properties.getProperty(MEM_FRAGMENTATION_RATIO);

                                                    //KEYS
                                                    String trhKeysCount = getKeysCount(properties);
                                                    //(keyspace_hits+keyspace_misses)/keyspace_hits
                                                    int keyspaceHits = Integer.parseInt(properties.getProperty(KEYS_HITS, BusinessConstant.ZERO));
                                                    int keyspaceMisss = Integer.parseInt(properties.getProperty(KEYS_MISS, BusinessConstant.ZERO));
                                                    int keyspaceCount = keyspaceHits + keyspaceMisss;
                                                    String trhKeyspaceRatio = keyspaceCount == BusinessConstant.ZERO_NUM ?
                                                            BusinessConstant.ONE : String.valueOf((keyspaceHits + keyspaceMisss) / keyspaceHits);

                                                    //集群
                                                    String[] var = returnSentinelMonitor(trhRedisIp, port);
                                                    String trhSentinelMonitor = var[0];
                                                    String trhSentinelConfigPath = var[1];
                                                    String trhClusterType = BusinessConstant.ONE.equals(properties.getProperty(CLUSTER_ENABLE, BusinessConstant.ONE)) ?
                                                            "cluster" : !StringUtils.isEmpty(trhSentinelMonitor) ? "sentinel" :
                                                            master.equals(trhRedisFlag) && !StringUtils.isEmpty(trhSlaveIp) ? "replication" : "redis";

                                                    TraceRedisPO traceRedisPo = TraceRedisPO.createRedis(buildRedisMapKey(trhRedisIp, String.valueOf(port)), trhRedisFlag, trhSlaveIp, trhClusterType, trhRdbOpen, trhAofOpen,
                                                            trhAofRdbOpen, trhRdbFile, trhRdbSaveType, trhAofFile, trhAofSaveType, trhMemoryCount,
                                                            trhMemoryAvailableCount, trhMemoryPeak, trhMemoryFragmentationRatio, trhKeyspaceRatio,
                                                            trhKeysCount, trhLastRdbStatus, trhLastAofStatus, trhLastForkUsec, trhConnTotalCount,
                                                            trhConnCount, trhConnBlockCount, trhLogPath, trhConfigPath, trhSentinelMonitor, trhSentinelConfigPath);

                                                    DBUtils.insert(TEACE_REDIS_INSERT, traceRedisPo);
                                                } catch (Exception e) {
                                                    CommandLog.error("executeRedisInfo异常", e);
                                                }
                                            }
                                        });
                            });
                            //通过界面sentinel连接对应IP ?
                            //通过界面修改redis配置 ? 如何连接其他IP ?
                        }
                    });
        } catch (Exception e) {
            CommandLog.errorThrow("executeRedisInfo Error!", e);
        }
    }

    /**
     * 获取当前redis的sentinel ip信息
     *
     * @param host redis ip
     * @param port redis post
     * @return 0 : sentinel ip信息 ip:port||格式  1 : sentinel配置文件信息 configPath||格式
     */
    private static String[] returnSentinelMonitor(String host, int port) {
        final String[] result = new String[2];
        long waitTime = 3000L;
        int limit = 3;

        if (!StringUtils.isEmpty(host)) {
            LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                    .clientResources(AppContextUtils.getBean(ClientResources.class)).build();
            RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration(host, port);
            if (!StringUtils.isEmpty(REDIS_HOST_PASS.get(host + port))) {
                configuration.setPassword(RedisPassword.of(CommandUtils.encodeEnc(REDIS_HOST_PASS.get(host + port))));
            }
            LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfiguration);

            //初始化Factory
            factory.afterPropertiesSet();
            factory.initConnection();

            final Object object = new Object();
            int[] var1 = new int[]{1};
            long start = System.currentTimeMillis();

            synchronized (object) {
                //获取Sentinel列表
                ReactiveRedisTemplate<String, String> template = new ReactiveRedisTemplate<>(factory, RedisSerializationContext.fromSerializer(new StringRedisSerializer()));
                template.listenTo(ChannelTopic.of(SENTINEL_LISTEREN))
                        .subscribe(message -> {
                            CommandLog.info("host : {} port : {} mess : {}", host, port, message);
                            String mess = message.getMessage();
                            String[] var2 = mess.split(BusinessConstant.COMMA, -1);

                            if (var2.length > BusinessConstant.ZERO_NUM) {
                                String sentienlHost = var2[0] + BusinessConstant.COLON_STR + var2[1];
                                if (StringUtils.isEmpty(result[0]) || !result[0].contains(sentienlHost)) {
                                    //未存储的sentinel ip
                                    if (!StringUtils.isEmpty(result[0])) {
                                        result[0] += BusinessConstant.DOUBLE_LINE;
                                        result[0] += sentienlHost;
                                    } else {
                                        result[0] = sentienlHost;
                                    }
                                } else {
                                    if (var1[0]++ >= limit) {
                                        CommandLog.info("shutdown");
                                        object.notify();
                                    }
                                }
                            }
                        });

                try {
                    //等待获取Sentinel列表
                    //4s收不到回应,表示未被sentinel监控
                    object.wait(waitTime);
                } catch (InterruptedException ignore) {
                }
            }

            CommandLog.info("耗时:{}ms", System.currentTimeMillis()-start);
            if (!StringUtils.isEmpty(result[0])) {
                String[] sentinelHosts = result[0].split(BusinessConstant.SPLIT_DOUBLE_LINE, -1);
                Arrays.stream(sentinelHosts)
                        .map(h -> h.split(BusinessConstant.COLON_STR, -1))
                        .forEach(strings -> {
                            String configPath = BusinessConstant.EMPTY_STR;

                            LettuceConnection redisConnection = (LettuceConnection) REDIS_SENTINEL_CONN_MAP.get(buildRedisMapKey(strings[0], String.valueOf(strings[1])));
                            try {
                                if (Objects.isNull(redisConnection) || redisConnection.isClosed()) {
                                    redisConnection = CreateFactory.returnRedisConn(strings[0], Integer.parseInt(strings[1]), null);
                                }
                                configPath = Objects.requireNonNull(redisConnection.serverCommands().info("server")).getProperty(CONFIG_PATH);
                            } catch (Exception e) {
                                CommandLog.errorThrow("获取Sentinel失败", e);
                            }
                            REDIS_SENTINEL_CONN_MAP.put(buildRedisMapKey(strings[0], String.valueOf(strings[1])), redisConnection);


                            if (StringUtils.isEmpty(result[1])) {
                                result[1] = configPath;
                            } else {
                                result[1] += BusinessConstant.DOUBLE_LINE;
                                result[1] += configPath;
                            }
                        });
            }

            factory.destroy();
        }

        return result;
    }

    /**
     * 解析并返回slave的host、post
     *
     * @param slave redis info中的slave字符串
     * @return 0 : host 1 : port
     */
    private static String[] parseSlaveHostAndPost(String slave) {
        String var1 = "ip=";
        String var2 = "port=";
        String[] result = new String[2];

        if (!StringUtils.isEmpty(slave)) {
            String[] var3 = slave.split(BusinessConstant.COMMA, -1);
            for (String s : var3) {
                if (s.contains(var1)) {
                    result[0] = s.replaceAll(var1, BusinessConstant.EMPTY_STR);
                } else if (s.contains(var2)) {
                    result[1] = s.replaceAll(var2, BusinessConstant.EMPTY_STR);
                }
            }
        }

        return result;
    }

    /**
     * 获取redis keys总数
     *
     * @param properties redis info all
     * @return 返回以db0:keys=561,expires=0,avg_ttl=0||格式字符串
     */
    private static String getKeysCount(Properties properties) {
        int var1 = BusinessConstant.ZERO_NUM;
        StringBuilder var2 = new StringBuilder();
        String var4 = DB + (var1++);
        String var3 = properties.getProperty(var4);

        while (true) {
            if (var3 != null) {
                var2.append(var4).append(BusinessConstant.DOUBLE_LINE).append(var3);
            }

            var4 = DB + (var1++);
            var3 = properties.getProperty(var4);
            if (var3 != null) {
                var2.append(BusinessConstant.DOUBLE_LINE);
            } else {
                break;
            }
        }

        return var2.toString();
    }

    /**
     * 获取slaves
     *
     * @param properties redis info all
     * @return 返回以host:post||格式字符串
     */
    private static String getSlavesForPrpo(Properties properties) {
        int var1 = BusinessConstant.ZERO_NUM;
        StringBuilder var2 = new StringBuilder();
        String var3 = properties.getProperty(SLAVE + (var1++));
        String[] var4;

        while (true) {
            var4 = parseSlaveHostAndPost(var3);

            if (!StringUtils.isEmpty(var4[0]) && !StringUtils.isEmpty(var4[1])) {
                var2.append(var4[0]).append(BusinessConstant.COLON_STR).append(var4[1]);
            }

            var3 = properties.getProperty(SLAVE + (var1++));
            if (var3 != null) {
                var2.append(BusinessConstant.DOUBLE_LINE);
            } else {
                break;
            }
        }

        return var2.toString();
    }
}
