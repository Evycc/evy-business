package com.evy.linlin;

import com.evy.common.infrastructure.common.command.utils.CommandUtils;
import com.evy.common.infrastructure.common.command.utils.AppContextUtils;
import com.evy.common.infrastructure.tunnel.InputDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.lang.reflect.Method;
import java.util.Map;

@SpringBootApplication(scanBasePackages = "com.evy.*")
@EnableFeignClients
@EnableDiscoveryClient
@RestController
public class EvyGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(EvyGatewayApplication.class, args);
    }

    /**
     * 服务化路由 path("lb://{服务名}")
     *
     * @param builder
     * @return
     */
    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("test", predicateSpec ->
                                predicateSpec
                                        .path("/evygateway")
                                        //服务转发时，会带上请求的path，如http://localhost:8080/evygateway，转发时为http://localhost:8081/TEST-DEMO/evygateway
                                        //通过stripPrefix方法去掉path指定位置的参数
                                        .filters(f -> f.stripPrefix(1))
                                        .uri("lb://TEST-DEMO")
                )
                .build();
    }

    @Bean
    public ReactiveRedisTemplate reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {
        return new ReactiveRedisTemplate(factory, RedisSerializationContext.fromSerializer(new Jackson2JsonRedisSerializer<>(Object.class)));
    }

    @Bean
    public RedisRouteDefinitionRepository redisRouteDefinitionRepository(@Qualifier("stringRedisTemplate") StringRedisTemplate redisTemplate){
        return new RedisRouteDefinitionRepository(redisTemplate);
    }

    @Bean
    public ReactiveRedisRouteDefinitionRepository reactiveRedisRouteDefinitionRepository(@Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate factory){
        return new ReactiveRedisRouteDefinitionRepository(factory);
    }

    @RequestMapping(value = "/rpc", method = RequestMethod.POST)
    public Flux<Object> serviceInvoke(@RequestBody Map<String, String> helloDto) throws Exception {
        System.out.println("rpc");

        //动态进行Feign调用
        FeignClientBuilder.Builder<?> builder = new FeignClientBuilder(AppContextUtils.getApplicationContext()).forType(Class.forName("com.evy.linlin.HelloServiceRpc"), "TEST-DEMO");
        Object cobj = builder.build();

        Object dto = Class.forName("com.evy.linlin.HelloDto").getDeclaredConstructor();

        Method cm = cobj.getClass().getMethod("hello", InputDTO.class);
        Object cr = cm.invoke(cobj, CommandUtils.conveterFromMap("com.evy.linlin.HelloDto", helloDto));
        System.out.println(cr);

        return Flux.just(cr);
    }
}
