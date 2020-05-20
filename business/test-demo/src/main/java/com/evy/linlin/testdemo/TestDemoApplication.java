package com.evy.linlin.testdemo;

import com.evy.common.domain.repository.mq.MqConsumer;
import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableDiscoveryClient
//@EnableFeignClients
public class TestDemoApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication.run(TestDemoApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        CommandLog.info("TestDemoApplication CommandLineRunner()");
        AppContextUtils.getBean(MqConsumer.class);
    }
}
