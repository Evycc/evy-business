package com.evy.linlin.start;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableDiscoveryClient
public class EvyStartApp
{
    public static void main( String[] args )
    {
        try {
            SpringApplication.run(EvyStartApp.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
