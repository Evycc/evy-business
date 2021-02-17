package com.evy.linlin.app;

import com.evy.common.command.infrastructure.tunnel.dto.InputDTO;
import com.evy.common.utils.AppContextUtils;
import com.evy.common.utils.CommandUtils;
import com.evy.linlin.gateway.filter.ServiceFilter;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * 网关启动类
 * @author Evyliuu
 */
@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableFeignClients(basePackages = "com.evy.*")
@EnableDiscoveryClient
@RestController
public class EvyGatewayApplication implements CommandLineRunner {
    private final ServiceFilter serviceFilter;

    public EvyGatewayApplication(ServiceFilter serviceFilter) {
        this.serviceFilter = serviceFilter;
    }

    public static void main(String[] args) {
        try {
            SpringApplication.run(EvyGatewayApplication.class, args);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void run(String... args) throws Exception {
        serviceFilter.initServiceInfo();
        serviceFilter.initServiceLimitInfo();
    }

    /**
     * 服务化路由 path("lb://{服务名}")<br/>
     * 服务需要在这里进行注册路由,由ServiceFilter进行统一路由到对应服务<br/>
     * 也可通过路由表td_router进行动态路由配置
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("service", predicateSpec ->
                        predicateSpec
                                .readBody(Map.class, map -> !map.isEmpty())
                                .and()
                                .path("/EvyGateway")
                                .filters(f -> f.filter(serviceFilter)
                                )
                                .uri("forward:" + ServiceFilter.SERVICE_NO_FOUND)
                )
                .build();
    }

    @RequestMapping(value = "/rpc", method = RequestMethod.POST)
    public Flux<Object> serviceInvoke(@RequestBody Map<String, String> helloDto) throws Exception {
        System.out.println("rpc");

        //动态进行Feign调用
        FeignClientBuilder.Builder<?> builder = new FeignClientBuilder(AppContextUtils.getApplicationContext())
                .forType(Class.forName("com.evy.linlin.HelloServiceRpc"), "TEST-DEMO");
        Object cobj = builder.build();

        Object dto = Class.forName("com.evy.linlin.HelloDto").getDeclaredConstructor();

        Method cm = cobj.getClass().getMethod("hello", InputDTO.class);
        Object cr = cm.invoke(cobj, CommandUtils.conveterFromMap("com.evy.linlin.HelloDto", helloDto));
        System.out.println(cr);

        return Flux.just(cr);
    }
}
