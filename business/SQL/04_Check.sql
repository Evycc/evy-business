check table public_deploy_info;
check table public_error_map;
check table public_log_flow;
check table service_limit_info;
check table td_batch;
check table td_batch_msg;
check table td_router;
check table trace_app_mermory_info;
check table trace_app_thread_info;
check table trace_http_flow;
check table trace_mq_flow;
check table trace_redis_health;
check table trace_services_info;
check table trace_slow_sql;

SELECT count(pem_id) AS '记录数为5即成功'
FROM public_error_map
WHERE pem_error_code IN ('ERR001', 'login_err_1', 'login_err_2', 'NOT_FOUND_ERROR', 'SERVICE_TIME_OUT');

SELECT count(tr_id) AS '记录数为1即成功'
FROM td_router
WHERE tr_router_id = 'route_evy_deploy_centre';

SELECT count(tsi_id) AS '记录数为20即成功'
FROM trace_services_info
WHERE tsi_service_bean_name IN (
                                'evy.deploy.auto.app',
                                'evy.deploy.buildPjo.app',
                                'evy.deploy.create.app',
                                'evy.deploy.getBranch.app',
                                'evy.deploy.nextSeq.app',
                                'evy.deploy.qryDeployInfo.app',
                                'evy.deploy.review.app',
                                'evy.login.app',
                                'evy.trace.memory.app',
                                'evy.trace.httpQry.app',
                                'evy.trace.mqQry.app',
                                'evy.trace.redisQry.app',
                                'evy.trace.srvQry.app',
                                'evy.trace.slowSqlQry.app',
                                'evy.trace.threadQry.app',
                                'evy.srv.create',
                                'evy.srv.modify',
                                'evy.trace.trackingQry.app',
                                'evy.trace.dump.app',
                                'evy.deploy.checkStart.app'
    );