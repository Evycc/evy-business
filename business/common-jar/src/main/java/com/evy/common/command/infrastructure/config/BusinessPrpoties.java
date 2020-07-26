package com.evy.common.command.infrastructure.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 提供通用参数，使用与需要提供配置的对象也是Bean的情况
 * @Author: EvyLiuu
 * @Date: 2019/11/30 14:20
 */
@Component
@ConfigurationProperties(
        prefix = "evy.command"
)
public class BusinessPrpoties {
    /**
     * 执行com.evy.common.infrastructure.common.command.CommandUtils#execute方法超时时间，默认30s
     */
    @Getter
    @Setter
    private int executeTimeout = 30000;
    @Getter
    @Setter
    private mq mq;
    @Getter
    @Setter
    private db db;
    @Getter
    @Setter
    private ftp ftp;

    public BusinessPrpoties() {
        this.mq = new BusinessPrpoties.mq();
        this.db = new BusinessPrpoties.db();
        this.ftp = new BusinessPrpoties.ftp();
    }

    public static class ftp {
        public ftp() {
        }

        @Getter
        @Setter
        private String username;
        @Getter
        @Setter
        private String password;
        @Getter
        @Setter
        private String privateKey;
        @Getter
        @Setter
        private String passphrase;
        @Getter
        @Setter
        private String host;
        @Getter
        @Setter
        private int port = 22;
        @Getter
        @Setter
        private int loginTimeout = 3000;
    }

    public static class db {
        /**
         * Mybatis Xml配置文件，默认名：mybatis.xml
         */
        @Getter
        @Setter
        private String mybatisXmlPath = "mybatis.xml";
        /**
         * Batch模式，单次最多批量提交数
         */
        @Getter
        @Setter
        private int batchInsertCount = 3000;
        public db() {
        }
    }

    public static class mq {
        @Getter
        @Setter
        private rabbitmq rabbitmq;

        public mq() {
            this.rabbitmq = new rabbitmq();
        }

        public static class rabbitmq {
            public rabbitmq() {
            }

            @Getter
            @Setter
            private String user = "root";
            @Getter
            @Setter
            private String password = "root";
            @Getter
            @Setter
            private String host = "localhost";
            @Getter
            @Setter
            private int port = 5672;
            /**
             * 持久化rabbitmq消息
             */
            @Getter
            @Setter
            private int deliveryMode = 2;
            /**
             * 消息优先级，默认0
             */
            @Getter
            @Setter
            private int priority = 0;
            /**
             * 消息确认
             */
            @Getter
            @Setter
            private boolean autoAck = false;
            /**
             * 自动重连
             */
            @Getter
            @Setter
            private boolean autoRecovery = true;
            /**
             * 连接重试次数，默认3
             */
            @Getter
            @Setter
            private int connRetryCount = 3;
            /**
             * 重试消费次数，默认5
             */
            @Getter
            @Setter
            private int consumerRetryCount = 5;
            /**
             * 重试消费时间间隔，单位s，默认5分钟
             */
            @Getter
            @Setter
            private int consumerRetryTime = 300;
            /**
             * rabbitmq最多消费数量，默认不限制
             */
            @Getter
            @Setter
            private int basicQos = 0;
        }
    }
}
