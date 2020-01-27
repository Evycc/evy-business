package com.evy.linlin;

import com.evy.common.infrastructure.common.command.JsonUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.StringRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

/**
 * 自定义RouteDefinitionRepository，基于redis
 * @Author: EvyLiuu
 * @Date: 2020/1/4 20:02
 */
public class RedisRouteDefinitionRepository implements RouteDefinitionRepository {
    @Autowired
    private final StringRedisTemplate template;
    private static final String ROUTE_NAME_KEYS = "gateway:route";

    public RedisRouteDefinitionRepository(StringRedisTemplate template) {
        this.template = template;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return Flux.fromIterable(
                template.opsForHash()
                        .values(ROUTE_NAME_KEYS)
                        .stream()
                        .map(value -> JsonUtils.convertToObject(String.valueOf(value), RouteDefinition.class))
                        .collect(Collectors.toUnmodifiableList())
        );
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        route.subscribe(r -> {
            CommandLog.info("保存路由{}", r.getId());
            template
                    .opsForHash()
                    .put(ROUTE_NAME_KEYS, r.getId(), JsonUtils.convertToJson(r, RouteDefinition.class));
        });


        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        routeId.subscribe(id -> {
            CommandLog.info("删除路由 ID:{}", id);
            if (template.opsForHash().delete(ROUTE_NAME_KEYS, id) == 0) {
                Mono.defer(() -> Mono.error(new NotFoundException("RouteDefinition not found: " + routeId
                        + "From RedisRouteDefinitionRepository")))
                        .subscribe();
            }
        });

        return Mono.empty();
    }
}
