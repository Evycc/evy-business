package com.evy.linlin.start;

import com.evy.common.trace.TraceUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * SpringBoot启动类
 * @Author: Evyliuu
 */
@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableDiscoveryClient
public class EvyStartApp implements CommandLineRunner
{
    public static void main( String[] args )
    {
        try {
            SpringApplication.run(EvyStartApp.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        TraceUtils.init();
    }
}
