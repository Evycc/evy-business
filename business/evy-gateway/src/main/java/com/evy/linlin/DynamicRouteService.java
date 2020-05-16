package com.evy.linlin;

import com.evy.common.domain.repository.db.DBUtils;
import com.evy.common.infrastructure.common.command.utils.JsonUtils;
import com.evy.common.infrastructure.common.constant.BusinessConstant;
import com.evy.common.infrastructure.common.log.CommandLog;
import com.google.gson.JsonSyntaxException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

/**
 * 从数据表获取动态路由数据
 *
 * @Author: EvyLiuu
 * @Date: 2019/12/28 21:08
 */
@Component
public class DynamicRouteService implements CommandLineRunner {
    private final ReactiveRedisRouteDefinitionRepository definitionRepository;
    private final ReactiveRedisTemplate<String, String> template;
    private static final String ROUTE_MAP_KEYS = "gateway:routemap";
    private static final ScheduledThreadPoolExecutor executorService = new ScheduledThreadPoolExecutor(1,
            Executors.defaultThreadFactory());
    private static final String QUERY_ROUTERS_SQL = "SELECT tr_router_id,tr_router_uri,tr_predicate_name,tr_filters_name,tr_predicate_args,tr_filters_args,tr_order,gmt_modify FROM td_router";
    private static final String TR_ROUTER_ID = "tr_router_id";
    private static final String TR_ROUTER_URI = "tr_router_uri";
    private static final String TR_PREDICATE_NAME = "tr_predicate_name";
    private static final String TR_FILTERS_NAME = "tr_filters_name";
    private static final String TR_PREDICATE_ARGS = "tr_predicate_args";
    private static final String TR_FILTERS_ARGS = "tr_filters_args";
    private static final String TR_ORDER = "tr_order";
    private static final String GMT_MODIFY = "gmt_modify";

    public DynamicRouteService(@Qualifier("reactiveRedisRouteDefinitionRepository") ReactiveRedisRouteDefinitionRepository definitionRepository, @Qualifier("reactiveRedisTemplate") ReactiveRedisTemplate<String, String> template) {
        this.definitionRepository = definitionRepository;
        this.template = template;
        Runtime.getRuntime().addShutdownHook(new Thread(executorService::shutdown));
    }

    @Override
    public void run(String... args) {
        submit();
        initRouteForRedis();
    }

    /**
     * 执行定时调度，检测路由表
     */
    private void submit() {
        executorService.setMaximumPoolSize(1);
        executorService.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executorService.setKeepAliveTime(0L, TimeUnit.MILLISECONDS);

        executorService.scheduleWithFixedDelay(() -> {
//            executeForRedis();
            executeForReactiveRedis();
        }, 1, 10, TimeUnit.SECONDS);
    }

    /**
     * 使用org.springframework.data.redis.core.StringRedisTemplate进行redis操作
     */
    private void executeForRedis() {
        DBUtils.executeQuerySql(resultSet -> {
            try {
                CommandLog.info("异步线程检测动态路由表...");
                while (resultSet.next()) {
                    String routerId = resultSet.getString(TR_ROUTER_ID);
                    String gmtModify = resultSet.getString(GMT_MODIFY);

                    Object modifyTime = template.opsForHash().get(ROUTE_MAP_KEYS, routerId);
                    if (StringUtils.isEmpty(modifyTime) || (gmtModify.compareTo(String.valueOf(modifyTime)) > 0)) {
                        String routerUri = resultSet.getString(TR_ROUTER_URI);
                        String predicateName = resultSet.getString(TR_PREDICATE_NAME);
                        String flitersName = resultSet.getString(TR_FILTERS_NAME);
                        String pargs = resultSet.getString(TR_PREDICATE_ARGS);
                        String fargs = resultSet.getString(TR_FILTERS_ARGS);
                        String routerOrder = resultSet.getString(TR_ORDER);

                        RouteDefinition routeDefinition = new RouteDefinition();
                        //路由断言器List
                        List<PredicateDefinition> plist = splitAndReturnPd(predicateName, pargs);
                        //过滤器List
                        List<FilterDefinition> flist = splitAndReturnFilter(flitersName, fargs);
                        routeDefinition.setId(routerId);
                        routeDefinition.setOrder(Integer.parseInt(routerOrder));
                        routeDefinition.setUri(URI.create(routerUri));

                        if (plist != null && plist.size() > 0) {
                            routeDefinition.setPredicates(plist);
                        }
                        if (flist != null && flist.size() > 0) {
                            routeDefinition.setFilters(flist);
                        }

                        updateRouter(routeDefinition);
                        template.opsForHash().put(ROUTE_MAP_KEYS, routerId, gmtModify);
                    }
                }
            } catch (SQLException e) {
                CommandLog.errorThrow("添加动态路由异常", e);
            }
        }, QUERY_ROUTERS_SQL, null);
    }

    /**
     * 使用org.springframework.data.redis.core.ReactiveRedisTemplate进行redis操作
     */
    private void executeForReactiveRedis() {
        DBUtils.executeQuerySql(resultSet -> {
            try {
                CommandLog.info("异步线程检测动态路由表...");

                while (resultSet.next()) {
                    String routerId = resultSet.getString(TR_ROUTER_ID);
                    String gmtModify = resultSet.getString(GMT_MODIFY);

                    String routerUri = resultSet.getString(TR_ROUTER_URI);
                    String predicateName = resultSet.getString(TR_PREDICATE_NAME);
                    String flitersName = resultSet.getString(TR_FILTERS_NAME);
                    String pargs = resultSet.getString(TR_PREDICATE_ARGS);
                    String fargs = resultSet.getString(TR_FILTERS_ARGS);
                    String routerOrder = resultSet.getString(TR_ORDER);

                    template.<String, String>opsForHash()
                            .get(ROUTE_MAP_KEYS, routerId)
                            .switchIfEmpty(Mono.just(BusinessConstant.EMPTY_STR))
                            .subscribe(modifyTime -> {
                                if (StringUtils.isEmpty(modifyTime) || (gmtModify.compareTo(modifyTime) > 0)) {
                                    RouteDefinition routeDefinition = new RouteDefinition();
                                    //路由断言器List
                                    List<PredicateDefinition> plist = splitAndReturnPd(predicateName, pargs);
                                    //过滤器List
                                    List<FilterDefinition> flist = splitAndReturnFilter(flitersName, fargs);
                                    routeDefinition.setId(routerId);
                                    routeDefinition.setOrder(Integer.parseInt(routerOrder));
                                    routeDefinition.setUri(URI.create(routerUri));

                                    if (plist != null && plist.size() > 0) {
                                        routeDefinition.setPredicates(plist);
                                    }
                                    if (flist != null && flist.size() > 0) {
                                        routeDefinition.setFilters(flist);
                                    }

                                    updateRouter(routeDefinition);
                                    //K:路由id V:动态路由更新时间
                                    template.opsForHash().put(ROUTE_MAP_KEYS, routerId, gmtModify).subscribe();
                                }
                            });
                }
            } catch (SQLException e) {
                CommandLog.errorThrow("添加动态路由异常", e);
            }
        }, QUERY_ROUTERS_SQL, null);
    }

    /**
     * 动态添加gateway路由
     *
     * @param routeDefinition 路由实例
     */
    private void updateRouter(RouteDefinition routeDefinition) {
        CommandLog.info("动态添加路由{}", routeDefinition);
        //已存在则删除
        definitionRepository.getRouteDefinitions()
                .filter(route -> route.getId().equals(routeDefinition.getId()))
                .switchIfEmpty(subscriber -> {
                    subscriber.onNext(routeDefinition);
                })
                .subscribe(dRoute -> {
                            definitionRepository.delete(Mono.just(dRoute.getId()));
                            definitionRepository.save(Mono.just(routeDefinition)).subscribe();
                        }
                );
    }

    /**
     * 根据JSON参数，构造PredicateDefinition
     *
     * @param predicateName PredicateDefinition ID
     * @param pargs         JSON参数
     * @return PredicateDefinition集合
     */
    private List<PredicateDefinition> splitAndReturnPd(String predicateName, String pargs) {
        List<PredicateDefinition> plist = null;
        //路由断言器设置
        String[] pds = StringUtils.tokenizeToStringArray(predicateName, "|");
        if (pds.length > 0) {
            plist = new ArrayList<>();
        }
        for (String pd : pds) {
            PredicateDefinition predicateDefinition = new PredicateDefinition();
            predicateDefinition.setName(pd);
            String[] args = StringUtils.tokenizeToStringArray(pargs, "|");
            try {
                for (String arg : args) {
                    Map map = JsonUtils.convertToObject(arg, Map.class);
                    predicateDefinition.setArgs(map);
                }
                plist.add(predicateDefinition);
            } catch (JsonSyntaxException e) {
                //非json格式
                CommandLog.errorThrow("tr_predicate_args非json格式", e);
            }
        }

        return plist;
    }

    /**
     * 根据JSON参数，构造FilterDefinition
     *
     * @param flitersName FilterDefinition ID
     * @param fargs       JSON参数
     * @return FilterDefinition集合
     */
    private List<FilterDefinition> splitAndReturnFilter(String flitersName, String fargs) {
        List<FilterDefinition> flist = null;
        //路由过滤器设置
        String[] fs = StringUtils.tokenizeToStringArray(flitersName, "|");
        if (fs.length > 0) {
            flist = new ArrayList<>();
        }
        for (String f : fs) {
            FilterDefinition filterDefinition = new FilterDefinition();
            filterDefinition.setName(f);
            String[] args = StringUtils.tokenizeToStringArray(fargs, "|");
            try {
                for (String arg : args) {
                    Map map = JsonUtils.convertToObject(arg, Map.class);
                    filterDefinition.setArgs(map);
                }
                flist.add(filterDefinition);
            } catch (JsonSyntaxException e) {
                //非json格式
                CommandLog.errorThrow("tr_predicate_args非json格式", e);
            }
        }

        return flist;
    }

    /**
     * 从Redis初始化已存在的路由
     */
    private void initRouteForRedis() {
        definitionRepository.getRouteDefinitions()
                .subscribe(route -> {
                    CommandLog.info("初始化已存在路由{}", route);
                    definitionRepository.save(Mono.just(route));
                });
    }
}
