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
        prefix = "evy.command"
)
public class BusinessProperties {
    /**
     * 执行com.evy.common.infrastructure.common.command.CommandUtils#execute方法超时时间，默认30s
     */
    private int executeTimeout = 30000;

    /**
     * MQ消息中心
     */
    private BusinessProperties.mq mq;
    /**
     * 数据库配置
     */
    private database database;
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
     * 构造参数
     */
    public BusinessProperties() {
        this.mq = new BusinessProperties.mq();
        this.database = new database();
        this.ftp = new BusinessProperties.ftp();
        this.http = new BusinessProperties.http();
    }

    public static class udp {
        public udp(){}
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

    public int getExecuteTimeout() {
        return executeTimeout;
    }

    public void setExecuteTimeout(int executeTimeout) {
        this.executeTimeout = executeTimeout;
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
}
