package com.evy.linlin.testdemo;

import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableFeignClients(basePackages = "com.evy.*")
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
