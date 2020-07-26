package com.evy.linlin.testdemo;

import com.evy.common.log.CommandLog;
import com.evy.common.mq.common.app.basic.MqConsumer;
import com.evy.common.trace.TraceUtils;
import com.evy.common.utils.AppContextUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableDiscoveryClient
//@EnableFeignClients
public class TestDemoApplication implements CommandLineRunner {
    public static void main(String[] args) {
        try {
            SpringApplication.run(TestDemoApplication.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        CommandLog.info("TestDemoApplication CommandLineRunner()");
        TraceUtils.init();
    }
}
