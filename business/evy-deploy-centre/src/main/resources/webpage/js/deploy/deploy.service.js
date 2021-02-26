'use strict';
app.factory('DeployMainService', ['$http', '$q', function ($http, $q) {
    const GATEWAY_PATH = '/EvyGateway';
    let incr = 1546400;

    const REQ_SRVCODE_MAP = {
        login : 'evy.login.app',
        autoDeploy : 'evy.deploy.auto.app',
        buildProject : 'evy.deploy.buildPjo.app',
        createDeployInfo : 'evy.deploy.create.app',
        getBranch : 'evy.deploy.getBranch.app',
        nextSeq : 'evy.deploy.nextSeq.app',
        qryDeployInfo : 'evy.deploy.qryDeployInfo.app',
        deployReview : 'evy.deploy.review.app',
        qryMemory : 'evy.trace.memory.app',
        qryHttpInfo : 'evy.trace.httpQry.app',
        qryMqInfo : 'evy.trace.mqQry.app',
        qryRedisInfo : 'evy.trace.redisQry.app',
        qrySrvInfo : 'evy.trace.srvQry.app',
        qrySlowSql : 'evy.trace.slowSqlQry.app',
        qryThreadInfo : 'evy.trace.threadQry.app'
    }

    return {
        encCode : encCode,
        buildPublicBody : buildPublicBody,
        login: login,
        autoDeploy : autoDeploy,
        buildProject : buildProject,
        createDeployInfo : createDeployInfo,
        getBranch : getBranch,
        nextSeq : nextSeq,
        qryDeployInfo : qryDeployInfo,
        deployReview : deployReview,
        qryMemory : qryMemory,
        qryHttpInfo : qryHttpInfo,
        qryMqInfo : qryMqInfo,
        qryRedisInfo : qryRedisInfo,
        qrySrvInfo : qrySrvInfo,
        qrySlowSql : qrySlowSql,
        qryThreadInfo : qryThreadInfo
    };

    /**
     * 登录或创建用户
     * @param body 参数
     * @return 返回userSeq
     */
    function login(body) {
        return sendPostReq(REQ_SRVCODE_MAP.login, body);
    }

    /**
     * 发起自动化部署
     * @param body 参数
     * @return 无实体返回,请求成功即可
     */
    function autoDeploy(body) {
        return sendPostReq(REQ_SRVCODE_MAP.autoDeploy, body);
    }

    /**
     * 编译指定应用
     * @param body 参数
     * @return 编译应用唯一序列,用于关联数据库中部署应用信息
     */
    function buildProject(body) {
        return sendPostReq(REQ_SRVCODE_MAP.buildProject, body);
    }

    /**
     * 创建用户名下新部署配置
     * @param body 参数
     * @return 部署配置标识
     */
    function createDeployInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.createDeployInfo, body);
    }

    /**
     * 获取git对应分支集合
     * @param body 参数
     * @return     gitPath及branchs列表
     */
    function getBranch(body) {
        return sendPostReq(REQ_SRVCODE_MAP.getBranch, body);
    }

    /**
     * 获取一个新的buildSeq,用于回查部署任务
     * @param body 参数
     * @return 返回编译唯一序列，用于关联一个编译记录
     */
    function nextSeq(body) {
        return sendPostReq(REQ_SRVCODE_MAP.nextSeq, body);
    }

    /**
     * 查询用户名下所有部署应用信息,用于后续执行部署
     * @param body 参数
     * @return 返回部署应用信息
     */
    function qryDeployInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryDeployInfo, body);
    }

    /**
     * 回查部署状态
     * @param body 参数
     * @return 返回0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败
     */
    function deployReview(body) {
        return sendPostReq(REQ_SRVCODE_MAP.deployReview, body);
    }

    /**
     * 查询应用内存使用率
     * @param body 参数
     * @return 返回多个应用内存使用率
     */
    function qryMemory(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryMemory, body);
    }

    /**
     * 查询Http请求耗时及响应信息
     * @param body 参数
     * @return 返回Http请求耗时及响应信息
     */
    function qryHttpInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryHttpInfo, body);
    }

    /**
     * 查询MQ消息消费链路、耗时情况
     * @param body 参数
     * @return 返回MQ消息消费链路、耗时情况
     */
    function qryMqInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryMqInfo, body);
    }

    /**
     * 查询Redis服务器健康信息收集
     * @param body 参数
     * @return 返回Redis服务器健康信息收集
     */
    function qryRedisInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryRedisInfo, body);
    }

    /**
     * 查询Eureka服务发布及消费情况
     * @param body 参数
     * @return 返回Eureka服务发布及消费情况
     */
    function qrySrvInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qrySrvInfo, body);
    }

    /**
     * 查询慢SQL、及优化建议
     * @param body 参数
     * @return 返回慢SQL、及优化建议
     */
    function qrySlowSql(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qrySlowSql, body);
    }

    /**
     * 查询线程信息,清晰展示线程阻塞、死锁情况
     * @param body 参数
     * @return 返回查询线程信息,清晰展示线程阻塞、死锁情况
     */
    function qryThreadInfo(body) {
        return sendPostReq(REQ_SRVCODE_MAP.qryThreadInfo, body);
    }

    function sendPostReq(serviceCode, reqBody) {
        reqBody.serviceCode = serviceCode;
        let deferred = $q.defer();	//生成异步对象
        $http.post(GATEWAY_PATH, reqBody)
            .then(function (response) {
                    deferred.resolve(response.data);
                },
                function (errResponse) {
                    console.error(errResponse);
                    deferred.reject(errResponse.data);
                });
        return deferred.promise;
    }

    /**
     * 自定义加密<br/>
     * 加密 : 将字符串以字符的形式分解后,左移16位,形成加密串(若字符是英文字母形式,则转成ascii码后再进行左移)<br/>
     * @param pass 明文
     * @return pass 密文
     */
    function encCode(pass) {
        let chars = pass.split('');
        let encPass = '';

        for(let i=0; i < chars.length; i++) {
            if (!(chars[i].match(/^\d+$/))) {
                //非数字
                encPass += (chars[i].charCodeAt(0) << 16);
            } else {
                encPass += (chars[i] << 16);
            }
        }
        return encPass;
    }

    /**
     * 构造公共报文
     * @param map 请求参数
     */
    function buildPublicBody(map) {
        map.srcSendNo = incrSeq();
        map.requestTime = curTimeStr();
        map.clientIp = '127.0.0.1';

        return map;
    }

    function incrSeq() {
        return ++incr;
    }

    function curTimeStr() {
        let time = new Date();
        let year = time.getFullYear();
        let month = time.getMonth() + '';
        let date = time.getDate() + '';
        let hours = time.getHours() + '';
        let minutes = time.getMinutes() + '';
        let seconds = time.getSeconds() + '';
        return year + month.padStart(2,'0') + date.padStart(2,'0') + hours.padStart(2,'0') +
            minutes.padStart(2,'0') + seconds.padStart(2,'0');
    }
}]);