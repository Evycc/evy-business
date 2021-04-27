package com.evy.common.command.infrastructure.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 提供通用参数，使用与需要提供配置的对象也是Bean的情况<br/>
 * 注 : 注入该配置需要等待ApplicationContext初始化完成<br/>
 * 建议在class上添加注解@DependsOn(BeanNameConstant.APP_CONTEXT_UTILS)
 *
 * @Author: EvyLiuu
 * @Date: 2019/11/30 14:20
 */
@Component
@ConfigurationProperties(
        prefix = "evy.base"
)
public class BusinessProperties {

    /**
     * MQ消息中心
     */
    private BusinessProperties.mq mq;
    /**
     * 数据库配置
     */
    private BusinessProperties.database database;
    /**
     * 文服工具
     */
    private BusinessProperties.ftp ftp;
    /**
     * HttpClient配置
     */
    private BusinessProperties.http http;
    /**
     * udp配置
     */
    private BusinessProperties.udp udp;
    /**
     * 记录公共日志流水配置
     */
    private BusinessProperties.traceLog traceLog;
    /**
     * 日志信息配置
     */
    private BusinessProperties.log log;
    /**
     * 健康信息收集配置
     */
    private BusinessProperties.trace trace;

    /**
     * 构造参数
     */
    public BusinessProperties() {
        this.mq = new BusinessProperties.mq();
        this.database = new BusinessProperties.database();
        this.ftp = new BusinessProperties.ftp();
        this.http = new BusinessProperties.http();
        this.traceLog = new BusinessProperties.traceLog();
        this.log = new BusinessProperties.log();
        this.trace = new BusinessProperties.trace();
    }

    /**
     * 健康信息收集配置类
     */
    public static class trace {
        private BusinessProperties.trace.service service;
        private BusinessProperties.trace.database database;
        private BusinessProperties.trace.http http;
        private BusinessProperties.trace.redis redis;
        private BusinessProperties.trace.mq mq;
        private BusinessProperties.trace.thread thread;
        private BusinessProperties.trace.memory memory;

        public trace(){
            this.service = new BusinessProperties.trace.service();
            this.database = new BusinessProperties.trace.database();
            this.http = new BusinessProperties.trace.http();
            this.redis = new BusinessProperties.trace.redis();
            this.mq = new BusinessProperties.trace.mq();
            this.thread = new BusinessProperties.trace.thread();
            this.memory = new BusinessProperties.trace.memory();
        }

        /**
         * 收集应用内存信息配置
         */
        public static class memory {
            public memory() {}
            /**
             * 收集应用内存信息,默认关闭
             */
            private boolean flag = false;
            /**
             * 定时收集时间,默认1分钟,单位:秒
             */
            private int timing = 60;
            /**
             * 应用内存信息最多保留记录数,单位:分钟,默认保存一天 60 * 24
             */
            private int limit = timing * 24;

            public int getLimit() {
                return limit;
            }

            public void setLimit(int limit) {
                this.limit = limit;
            }

            public int getTiming() {
                return timing;
            }

            public void setTiming(int timing) {
                this.timing = timing;
            }
            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * 服务器线程信息收集配置
         */
        public static class thread {
            public thread() {}
            /**
             * 收集应用线程信息,默认关闭
             */
            private boolean flag = false;
            /**
             * 定时收集时间,默认1分钟,单位:秒
             */
            private int timing = 60;

            public int getTiming() {
                return timing;
            }

            public void setTiming(int timing) {
                this.timing = timing;
            }
            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * mq链路收集配置
         */
        public static class mq {
            public mq(){}
            /**
             * 统计mq请求信息,默认关闭
             */
            private boolean flag = false;
            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * redis健康信息收集配置
         */
        public static class redis {
            public redis(){}
            /**
             * redis健康信息收集,默认关闭
             */
            private boolean flag = false;
            /**
             * 需要监听的redis列表,格式host:port:password,分隔符:||,password支持enc加密
             */
            private String list;
            /**
             * 定时收集时间,默认1分钟,单位:秒
             */
            private int timing = 60;

            public int getTiming() {
                return timing;
            }

            public void setTiming(int timing) {
                this.timing = timing;
            }

            public String getList() {
                return list;
            }

            public void setList(String list) {
                this.list = list;
            }

            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * http请求统计
         */
        public static class http {
            public http() {}
            /**
             * 统计http请求总数,默认关闭
             */
            private boolean flag = false;

            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * 慢SQL
         */
        public static class database {
            public database(){}
            /**
             * 统计慢SQL,默认关闭,与common-agent-jar搭配使用<br/>
             * 在common-agent-jar通过jvm参数配置慢SQL记录时间,SLOW_SQL=1000,默认1s
             */
            private boolean flag = false;

            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        /**
         * 扫描服务列表
         */
        public static class service {
            public service(){}
            /**
             * 刷新服务列表间隔,默认1分钟,单位:秒
             */
            private int timing = 60;
            /**
             * 开启定时刷新服务列表,默认关闭
             */
            private boolean flag = false;

            public int getTiming() {
                return timing;
            }

            public void setTiming(int timing) {
                this.timing = timing;
            }

            public boolean isFlag() {
                return flag;
            }

            public void setFlag(boolean flag) {
                this.flag = flag;
            }
        }

        public BusinessProperties.trace.service getService() {
            return service;
        }

        public void setService(BusinessProperties.trace.service service) {
            this.service = service;
        }

        public BusinessProperties.trace.database getDatabase() {
            return database;
        }

        public void setDatabase(BusinessProperties.trace.database database) {
            this.database = database;
        }

        public BusinessProperties.trace.http getHttp() {
            return http;
        }

        public void setHttp(BusinessProperties.trace.http http) {
            this.http = http;
        }

        public BusinessProperties.trace.redis getRedis() {
            return redis;
        }

        public void setRedis(BusinessProperties.trace.redis redis) {
            this.redis = redis;
        }

        public BusinessProperties.trace.mq getMq() {
            return mq;
        }

        public void setMq(BusinessProperties.trace.mq mq) {
            this.mq = mq;
        }

        public BusinessProperties.trace.thread getThread() {
            return thread;
        }

        public void setThread(BusinessProperties.trace.thread thread) {
            this.thread = thread;
        }

        public BusinessProperties.trace.memory getMemory() {
            return memory;
        }

        public void setMemory(BusinessProperties.trace.memory memory) {
            this.memory = memory;
        }
    }

    /**
     * 日志配置
     */
    public static class log {
        private BusinessProperties.log.message message;

        public log(){
            this.message = new BusinessProperties.log.message();
        }

        /**
         * 日志消息配置
         */
        public static class message {
            public message(){}

            /**
             * 可打印的最大日志长度,默认1024,-1则不做限制
             */
            private int length = 1024;

            public int getLength() {
                return length;
            }

            public void setLength(int length) {
                this.length = length;
            }
        }

        public BusinessProperties.log.message getMessage() {
            return message;
        }

        public void setMessage(BusinessProperties.log.message message) {
            this.message = message;
        }
    }

    /**
     * traceLog 异步记录日志,默认使用rabbitmq
     */
    public static class traceLog {
        public traceLog(){}

        /**
         * topic
         */
        private String topic;
        /**
         * tag
         */
        private String tag;

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }

        public String getTag() {
            return tag;
        }

        public void setTag(String tag) {
            this.tag = tag;
        }
    }

    public static class udp {
        public udp() {
        }

        /**
         * udp server连接超时时间，单位ms，默认1s
         */
        private int connTimeOut = 1000;
        /**
         * udp接收及发送报文长度,默认10240byte
         */
        private int messageLength = 10240;

        public int getConnTimeOut() {
            return connTimeOut;
        }

        public void setConnTimeOut(int connTimeOut) {
            this.connTimeOut = connTimeOut;
        }

        public int getMessageLength() {
            return messageLength;
        }

        public void setMessageLength(int messageLength) {
            this.messageLength = messageLength;
        }
    }

    /**
     * HttpClient配置
     */
    public static class http {
        public http() {
        }

        /**
         * http请求连接超时时间，单位ms，默认2s
         */
        private int connTimeOut = 2000;
        /**
         * http请求超时时间，单位ms，默认30s
         */
        private int reqTimeOut = 30000;

        public int getConnTimeOut() {
            return connTimeOut;
        }

        public void setConnTimeOut(int connTimeOut) {
            this.connTimeOut = connTimeOut;
        }

        public int getReqTimeOut() {
            return reqTimeOut;
        }

        public void setReqTimeOut(int reqTimeOut) {
            this.reqTimeOut = reqTimeOut;
        }
    }

    /**
     * 文服工具配置
     */
    public static class ftp {
        public ftp() {
        }

        /**
         * 文服FTP用户名
         */
        private String username;
        /**
         * 文服FTP密码
         */
        private String password;
        /**
         * 文服FTP密钥
         */
        private String privateKey;
        /**
         * 文服FTP密钥密码
         */
        private String passphrase;
        /**
         * 文服FTP host
         */
        private String host;
        /**
         * 文服FTP port
         */
        private int port = 22;
        /**
         * 文服FTP连接超时时间,单位ms
         */
        private int loginTimeout = 3000;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getPrivateKey() {
            return privateKey;
        }

        public void setPrivateKey(String privateKey) {
            this.privateKey = privateKey;
        }

        public String getPassphrase() {
            return passphrase;
        }

        public void setPassphrase(String passphrase) {
            this.passphrase = passphrase;
        }

        public String getHost() {
            return host;
        }

        public void setHost(String host) {
            this.host = host;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getLoginTimeout() {
            return loginTimeout;
        }

        public void setLoginTimeout(int loginTimeout) {
            this.loginTimeout = loginTimeout;
        }
    }

    /**
     * 数据库配置
     */
    public static class database {
        public database() {
        }

        /**
         * Mybatis Xml配置文件，默认名：mybatis.xml
         */
        private String mybatisXmlPath = "mybatis.xml";
        /**
         * Batch模式，单次最多批量提交数
         */
        private int batchInsertCount = 3000;

        public String getMybatisXmlPath() {
            return mybatisXmlPath;
        }

        public void setMybatisXmlPath(String mybatisXmlPath) {
            this.mybatisXmlPath = mybatisXmlPath;
        }

        public int getBatchInsertCount() {
            return batchInsertCount;
        }

        public void setBatchInsertCount(int batchInsertCount) {
            this.batchInsertCount = batchInsertCount;
        }
    }

    /**
     * 消息中心配置
     */
    public static class mq {
        private rabbitmq rabbitmq;

        public mq() {
            this.rabbitmq = new rabbitmq();
        }

        public static class rabbitmq {
            public rabbitmq() {
            }

            /**
             * RabbitMQ用户名
             */
            private String user = "root";
            /**
             * RabbitMQ密码
             */
            private String password = "root";
            /**
             * RabbitMQ host
             */
            private String host = "localhost";
            /**
             * RabbitMQ port
             */
            private int port = 5672;
            /**
             * 持久化rabbitmq消息
             */
            private int deliveryMode = 2;
            /**
             * 消息优先级，默认0
             */
            private int priority = 0;
            /**
             * 消息确认
             */
            private boolean autoAck = true;
            /**
             * 自动重连
             */
            private boolean autoRecovery = true;
            /**
             * 连接重试次数，默认3
             */
            private int connRetryCount = 3;
            /**
             * 重试消费次数，默认5
             */
            private int consumerRetryCount = 5;
            /**
             * 重试消费时间间隔，单位s，默认5分钟
             */
            private int consumerRetryTime = 300;
            /**
             * rabbitmq最多消费数量，默认不限制
             */
            private int basicQos = 0;

            public String getUser() {
                return user;
            }

            public void setUser(String user) {
                this.user = user;
            }

            public String getPassword() {
                return password;
            }

            public void setPassword(String password) {
                this.password = password;
            }

            public String getHost() {
                return host;
            }

            public void setHost(String host) {
                this.host = host;
            }

            public int getPort() {
                return port;
            }

            public void setPort(int port) {
                this.port = port;
            }

            public int getDeliveryMode() {
                return deliveryMode;
            }

            public void setDeliveryMode(int deliveryMode) {
                this.deliveryMode = deliveryMode;
            }

            public int getPriority() {
                return priority;
            }

            public void setPriority(int priority) {
                this.priority = priority;
            }

            public boolean isAutoAck() {
                return autoAck;
            }

            public void setAutoAck(boolean autoAck) {
                this.autoAck = autoAck;
            }

            public boolean isAutoRecovery() {
                return autoRecovery;
            }

            public void setAutoRecovery(boolean autoRecovery) {
                this.autoRecovery = autoRecovery;
            }

            public int getConnRetryCount() {
                return connRetryCount;
            }

            public void setConnRetryCount(int connRetryCount) {
                this.connRetryCount = connRetryCount;
            }

            public int getConsumerRetryCount() {
                return consumerRetryCount;
            }

            public void setConsumerRetryCount(int consumerRetryCount) {
                this.consumerRetryCount = consumerRetryCount;
            }

            public int getConsumerRetryTime() {
                return consumerRetryTime;
            }

            public void setConsumerRetryTime(int consumerRetryTime) {
                this.consumerRetryTime = consumerRetryTime;
            }

            public int getBasicQos() {
                return basicQos;
            }

            public void setBasicQos(int basicQos) {
                this.basicQos = basicQos;
            }
        }

        public BusinessProperties.mq.rabbitmq getRabbitmq() {
            return rabbitmq;
        }

        public void setRabbitmq(BusinessProperties.mq.rabbitmq rabbitmq) {
            this.rabbitmq = rabbitmq;
        }
    }

    public BusinessProperties.mq getMq() {
        return mq;
    }

    public void setMq(BusinessProperties.mq mq) {
        this.mq = mq;
    }

    public BusinessProperties.database getDatabase() {
        return database;
    }

    public void setDatabase(BusinessProperties.database database) {
        this.database = database;
    }

    public BusinessProperties.ftp getFtp() {
        return ftp;
    }

    public void setFtp(BusinessProperties.ftp ftp) {
        this.ftp = ftp;
    }

    public BusinessProperties.http getHttp() {
        return http;
    }

    public void setHttp(BusinessProperties.http http) {
        this.http = http;
    }

    public BusinessProperties.udp getUdp() {
        return udp;
    }

    public void setUdp(BusinessProperties.udp udp) {
        this.udp = udp;
    }

    public BusinessProperties.traceLog getTraceLog() {
        return traceLog;
    }

    public void setTraceLog(BusinessProperties.traceLog traceLog) {
        this.traceLog = traceLog;
    }

    public BusinessProperties.log getLog() {
        return log;
    }

    public void setLog(BusinessProperties.log log) {
        this.log = log;
    }

    public BusinessProperties.trace getTrace() {
        return trace;
    }

    public void setTrace(BusinessProperties.trace trace) {
        this.trace = trace;
    }
}
