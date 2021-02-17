package com.evy.linlin.start;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceUtils;
import com.evy.common.utils.AppContextUtils;
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
            CommandLog.error("应用启动异常 IP:{} 端口:{} 应用名:{}", BusinessConstant.VM_HOST, AppContextUtils.getForEnv("server.port"), AppContextUtils.getForEnv("spring.application.name"));
            e.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        //Trace链路信息收集
        TraceUtils.init();
    }
}
