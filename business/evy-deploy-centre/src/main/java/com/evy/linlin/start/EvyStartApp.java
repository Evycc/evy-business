package com.evy.linlin.start;

import com.evy.common.command.infrastructure.constant.BusinessConstant;
import com.evy.common.log.CommandLog;
import com.evy.common.trace.TraceUtils;
import com.evy.common.trace.service.TraceService;
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
        boolean result =false;
        try {
            SpringApplication.run(EvyStartApp.class, args);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        CommandLog.error("应用启动{} IP:{} 端口:{} 应用名:{}",
                result ? "成功" : "失败",
                BusinessConstant.VM_HOST,
                AppContextUtils.getForEnv("server.port"),
                AppContextUtils.getForEnv("spring.application.name"));
    }

    @Override
    public void run(String... args) throws Exception {
        //开启应用信息健康
        TraceUtils.init();
        //初始化服务,否则间隔1分钟后才能查到服务
        TraceService.executeService();
    }
}
