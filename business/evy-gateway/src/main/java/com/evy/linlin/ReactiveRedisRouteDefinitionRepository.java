package com.evy.linlin;

import com.evy.common.infrastructure.common.command.utils.JsonUtils;
import com.evy.common.infrastructure.common.log.CommandLog;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.cloud.gateway.support.NotFoundException;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 自定义RouteDefinitionRepository，基于redis
 * @Author: EvyLiuu
 * @Date: 2020/1/1 20:46
 */
public class ReactiveRedisRouteDefinitionRepository implements RouteDefinitionRepository {
    public final ReactiveRedisTemplate<String, String> template;
    private static final String ROUTE_NAME_KEYS = "gateway:route";

    public ReactiveRedisRouteDefinitionRepository(ReactiveRedisTemplate<String, String> template) {
        this.template = template;
    }

    @Override
    public Flux<RouteDefinition> getRouteDefinitions() {
        return template.<String, String>opsForHash()
                .values(ROUTE_NAME_KEYS)
                .flatMap(value -> Flux.just(JsonUtils.convertToObject(String.valueOf(value), RouteDefinition.class)));
    }

    @Override
    public Mono<Void> save(Mono<RouteDefinition> route) {
        route.subscribe(r -> {
            CommandLog.info("保存路由{}", r.getId());
            template.opsForHash()
                    .put(ROUTE_NAME_KEYS, r.getId(), JsonUtils.convertToJson(r, RouteDefinition.class))
                    .subscribe();
        });

        return Mono.empty();
    }

    @Override
    public Mono<Void> delete(Mono<String> routeId) {
        routeId.subscribe(id -> {
            CommandLog.info("删除路由 ID:{}", id);
            template.opsForHash()
                    .remove(ROUTE_NAME_KEYS, id)
                    .subscribe(count -> {
                        if (count == 0) {
                            throw new NotFoundException("RouteDefinition not found: " + id
                                    + "From RedisRouteDefinitionRepository");
                        }
                    }, throwable -> Mono.defer(() -> Mono.error(throwable)));
        });

        return Mono.empty();
    }
}
