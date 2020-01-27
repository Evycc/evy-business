package com.evy.linlin.testdemo;

import com.evy.common.domain.repository.mq.MqConsumer;
import com.evy.common.infrastructure.common.context.AppContextUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableDiscoveryClient
//@EnableFeignClients
public class TestDemoApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TestDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        AppContextUtils.getBean(MqConsumer.class);
    }
}
