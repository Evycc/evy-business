'use strict';
app.controller('DeployMainController', ['$scope', 'DeployMainService', '$compile', function ($scope, DeployMainService, $compile) {
    let self = this;


    /********定义用户模型********/
    self.tips = {
        id : 'tips-div',
        pre : '提示信息:',
        post : '',
        show : false
    }
    /**
     * 定义登陆表单模型
     */
    self.loginUser = {
        username: '',
        password: ''
    }
    /**
     * 定义QryDeployInfoDTO模型<br/>
     * 查询用户名下所有部署应用信息,用于后续执行部署
     * @type {{deploySeq: string, userSeq: string}}
     */
    self.qryDeployInfo = {
        /**
         * 唯一用户标识
         */
        userSeq : '',
        /**
         * 标识当前应用，用于关联当前部署记录
         */
        deploySeq : ''
    }
    /**
     * 线程ip select 列表
     * @type {*[]}
     */
    self.curSelectIp = [];
    self.cur = {
        /**
         * 登陆后用户名
         */
        user: '尚未登陆',
        /**
         * 当前分支
         */
        branch: 'none',
        appName: 'none',
        deploySeq : '',
        userSeq : ''
    }
    self.curMqTraceInfo = [];
    self.curSlowSqlModel = [];
    self.curServiceInfo = [];
    self.curRedisView = [];
    self.curHttpInfoView = [];
    self.curTraceListResult = [];
    /**
     * 定义展示页面ID
     */
    self.viewId = {
        mainView : 'deploy-main-view',
        createView : 'deploy-stepEl',
        selectBranchView : 'deploy-select-branch',
        queryMemoryView : 'deploy-memory-view',
        queryThreadView : 'deploy-thread-view',
        queryMqView : 'deploy-mq-view',
        queryMqQueryResultView : 'deploy-mq-result-view',
        querySlowSqlView : 'deploy-slowSql-view',
        queryServiceView : 'deploy-service-view',
        queryServiceAddView : 'deploy-service-add-view',
        queryServiceModifyView : 'deploy-service-modify-view',
        queryRedisView : 'deploy-redis-view',
        queryHttpView : 'deploy-http-view',
        queryHttpResultView : 'deploy-http-result-view',
        queryTrackingView : 'deploy-trace-view',
        queryTrackingResultView : 'deploy-trace-result-view',
        showAbsoluteDiv: 'show-absolute-div'
    }
    /**
     * 定义展示页面ID
     */
    self.viewShow = {
        mainViewShow : false,
        createViewShow : false,
        selectBranchViewShow : false,
        queryMemoryView : true,
        queryThreadView : false,
        queryMqView : false,
        queryMqQueryResultView : false,
        querySlowSqlView : false,
        queryServiceView : false,
        queryServiceAddView : false,
        queryServiceModifyView : false,
        queryRedisView : false,
        queryHttpView : false,
        queryHttpResultView: false,
        queryTrackingView: false,
        queryTrackingResultView: false,
        showAbsoluteDiv: false
    }

    self.showAbsoluteDiv = {
        showDivTitle : '',
        showDivContent : '',
        showTextDiv : true,
        showThreadDiv : !this.showTextDiv,
        showThreadResultDiv : !this.showTextDiv,
        showThreadResult : '',
        threadResultDivId : 'thread-result-div-id'
    }

    /**
     * 是否登陆
     * @type {boolean}
     */
    self.isLogin = false;
    /**
     * 用户标识
     * @type {string}
     */
    self.loginToken = '';
    /**
     * 部署信息主页模型
     */
    self.deployInfo = {
        title: '部署应用配置'
    }
    /**
     * 部署应用配置 表单
     */
    self.deployForm = {
        userSeq: '',
        appName: '',
        gitPath: '',
        brchanName: '',
        targetHost: '',
        switchJunit: 1,
        switchBatchDeploy: 1,
        jvmParam: ''
    }
    /**
     * 当前自动化部署表单
     */
    self.lastDeployForm = {
        deployTime : '',
        appName : '',
        switchJunit : false,
        switchBatchDeploy: false,
        gitPath: '',
        targetHost: '',
        jvmParam: '',
        remarks: ''
    }
    self.mqForm = {
        topic: '',
        msgId: '',
        limit: '',
        userSeq: ''
    }
    self.httpForm = {
        path : '',
        limit : ''
    }
    self.traceFrom = {
        qryTraceId : ''
    }
    self.srvForm = {
        srvCode: '',
        providerName: '',
        consumerName: ''
    }
    self.srvModifyForm = {
        srvCode: '',
        serviceName: '',
        providerName: '',
        consumerName: '',
        limitQps: '',
        limitFallback: '',
    }
    self.constSrvInfo = {
        srvCode: '',
        serviceName: '',
        providerName: ''
    }
    /**
     * 线程信息数组
     * @type {*[]}
     */
    self.curThreadInfo = [];
    /**
     * key : ip  value : 对应Ip线程信息集合
     */
    self.threadInfoAll = [
        {
            'ip':'',
            'list': []
        }
    ];
    /**
     * INextBuildSeq 模型
     * 用于新部署任务
     */
    self.nextDeployBuild = {
        userSeq: '',
        appName: '',
        gitPath: '',
        branchName: '',
        remarks: '',
        targetHost: '',
        switchJunit: 1,
        switchBatchDeploy: 1,
        jvmParam: ''
    }
    /**
     * 获取分支列表结果
     */
    self.deployViewReq = {
        show : false,
        title : '选择分支'
    }
    /**
     * 选择分支模块
     */
    self.selectBranch = [
        // {
        //     appName : '',
        //     branchName : '',
        //     gitPath : '',
        //     serverIp : '',
        //     deploySeq : ''
        // }
    ]
    /**
     * 分支列表结果list模型
     *
     */
    self.deployViewReqList = [
        // {
        //     branchName : 'master'
        // }
    ]
    /**
     * 回滚部署记录列表模型
     */
    self.rollbackDeployList = [
        {
            //     appName : '',
            //     branch : '',
            //     /**
            //      * 配置唯一序列
            //      */
            //     deploySeq : '',
            // gmtModify : '',
            //     serverIp : ''
        }
    ]
    /**
     * 应用内存使用率模型
     */
    self.appMemoryInfoList = [
        {
            // appIp: '',
            // cpuCount: '',
            // /**
            //  * CPU使用率
            //  */
            // cpuLoadPercentage: '',
            // /**
            //  * 系统内存,单位kb
            //  */
            // sysMermoryKb: '',
            // /**
            //  * 系统可用内存,单位kb
            //  */
            // sysAvailMermoryKb: '',
            // /**
            //  * 系统已用内存,单位kb
            //  */
            // sysUseMermoryKb: '',
            // /**
            //  * 应用最大堆内存,单位kb
            //  */
            // appHeapMaxMermoryKb: '',
            // /**
            //  * 应用最小堆内存,单位kb
            //  */
            // appHeapMinMermoryKb: '',
            // /**
            //  * 应用占用堆内存,单位kb
            //  */
            // appHeapUseMermoryKb: '',
            // /**
            //  * 应用最大非堆内存,单位kb
            //  */
            // appNoHeapMaxMermoryKb: '',
            // /**
            //  * 应用最小非堆内存,单位kb
            //  */
            // appNoHeapMinMermoryKb: '',
            // /**
            //  * 应用占用非堆内存,单位kb
            //  */
            // appNoHeapUseMermoryKb: '',
            // /**
            //  * 采集时间
            //  */
            // gmtModify: ''
        }
    ]

    /********监听用户事件函数********/

    /**
     * 监听登陆表单输入框变化,存在值则变更表单样式
     */
    $scope.$watch('main.loginUser.username', function (newValue) {
        let inputDivId = 'form-cell-1';
        let inputId = 'form-cell-input-1';
        if (newValue == null || angular.equals('', newValue)) {
            hideInputSelectStyle(inputDivId, inputId);
        } else {
            showInputSelectStyle(inputDivId, inputId)
        }
    })

    /**
     * 监听登陆表单输入框变化,存在值则变更表单样式
     */
    $scope.$watch('main.loginUser.password', function (newValue) {
        let inputDivId = 'form-cell-2';
        let inputId = 'form-cell-input-2';
        if (newValue == null || angular.equals('', newValue)) {
            hideInputSelectStyle(inputDivId, inputId);
        } else {
            showInputSelectStyle(inputDivId, inputId)
        }
    })

    /**
     * 监听登陆表单输入框变化,存在值则变更表单样式
     */
    $scope.$watch('main.curThreadIp', function (newValue) {
        if (self.isLogin && self.viewShow.queryThreadView) {
            let bool = true;
            for (let i =0; i < self.threadInfoAll.length; i++) {
                if (self.threadInfoAll[i].ip === self.curThreadIp) {
                    //已经存在则不进行请求
                    bool = false;
                    break;
                }
            }
            if (bool) {
                self.ThreadCount = 0
                self.qryThreadInfoListSubmit();
            } else {
                self.ThreadCount = 0
                self.updateCurThreadInfo();
            }
        }
    })

    /**
     * 设置<h3 class="info-title">{{main.deployInfo.title}}</h3> 标题
     * @param title
     */
    self.setDeployInfoTitle = function (title) {
        self.deployInfo.title = title;
    }
    /**
     * 获取分支列表属性
     * @param $event 传入的a标签对象
     */
    self.setBranchName = function ($event) {
        self.deployForm.brchanName = $event.target.innerText;
    }
    self.showClose = function () {
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryServiceModifyView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryHttpResultView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
        self.viewShow.showAbsoluteDiv = false;
    }
    /**
     * 展示新增部署应用页面
     * @param title
     */
    self.showDeployCreateView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = true;
    }
    self.showQueryMemoryView = function (title){
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryMemoryView = true;
        self.qryAllMemoryInfoList();
    }
    /**
     * 展示部署配置信息页面
     * @param title
     */
    self.showDeployMainView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.mainViewShow = true;
    }
    /**
     * 展示切换分支页面
     * @param title
     */
    self.showSelectBranchView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.selectBranchViewShow = true;
    }

    self.showQueryThreadView = function(title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryThreadView = true;
        self.rmCurThreadAll();
        self.qryThreadInfoListSubmit();
    }

    self.qryAllMemoryInfoList = function () {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        DeployMainService.qryMemory(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询内存信息失败 ' + response.errorMsg);
                } else {
                    if (response.outMap != null) {
                        self.initMemoryViewJs(response.outMap);
                    }
                }
                console.log(response)
            }, function (err){
                console.log('查询内存信息失败 ' + err)
            });
        //TODO
    }

    self.rmCurThreadAll = function () {
        for (let i=0; i < self.threadInfoAll.length; i++) {
            if (self.threadInfoAll[i].ip === self.curThreadIp) {
                self.threadInfoAll[i].list[0].splice(0, self.threadInfoAll[i].list[0].length);
                break;
            }
        }
    }

    self.qryThreadInfoListSubmit = function () {
        self.qryThreadInfoList('', 0, 20);
    }

    self.ThreadCount = 0;
    self.curThreadIp = self.curSelectIp[0];
    /**
     * 获取应用线程信息
     */
    self.qryThreadInfoList = function (threadName, beginIndex, endIndex) {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        body.beginIndex = beginIndex;
        body.endIndex = endIndex;
        body.serviceIp = self.curThreadIp;
        if (threadName !== null && threadName !== '') {
            body.threadName = threadName;
        }

        self.sendQryThreadInfo(body);
    }

    self.sendQryThreadInfo = function (body) {
        DeployMainService.qryThreadInfo(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询线程信息失败 ' + response.errorMsg);
                } else {
                    if (response.list != null) {
                        self.saveThreadInfo(response.list);
                    }
                    //分段获取
                    if (self.curThreadIp === body.serviceIp && body.beginIndex < (response.total - body.endIndex)) {
                        self.qryThreadInfoList('', body.beginIndex + body.endIndex, body.endIndex);
                    }
                }
            }, function (err){
                console.log('查询线程信息失败 ' + err)
            });
    }

    self.saveThreadInfo = function(respList) {
        if (self.threadInfoAll[0].ip === '') {
            self.threadInfoAll[0].ip = respList[0].appIp;
            self.threadInfoAll[0].list.push(respList);
        } else {
            for (let i=0; i < self.threadInfoAll.length; i++) {
                if (self.threadInfoAll[i].ip === respList[0].appIp) {
                    if (self.threadInfoAll[i].list.length > 0) {
                        for (let j=0; j < respList.length; j++) {
                            self.threadInfoAll[i].list[0].push(respList[j]);
                        }
                    } else {
                        self.threadInfoAll[i].list = respList;
                    }

                    break;
                } else {
                    if (i === (self.threadInfoAll.length-1)) {
                        self.threadInfoAll[i+1] = {};
                        self.threadInfoAll[i+1].ip = respList[0].appIp;
                        self.threadInfoAll[i+1].list = [];
                        self.threadInfoAll[i+1].list.push(respList);
                        break;
                    }
                }
            }
        }

        self.updateCurThreadInfo();
    }

    self.updateCurThreadInfo = function () {
        if (self.threadInfoAll.length > 0) {
            for (let i=0; i < self.threadInfoAll.length; i++) {
                if (self.threadInfoAll[i].ip === self.curThreadIp) {
                    self.curThreadInfo = self.threadInfoAll[i].list;

                    for (let j=0; j < self.curThreadInfo[0].length; j++) {
                        if (self.ThreadCount < self.curThreadInfo[0][j].threadMaxCount) {
                            self.ThreadCount = self.curThreadInfo[0][j].threadMaxCount;
                        }
                    }
                }
            }
        }
    }

    self.showThreadStack = function (text) {
        self.viewShow.showAbsoluteDiv = true;
        self.showAbsoluteDiv.showDivTitle = '堆栈信息';
        self.showAbsoluteDiv.showDivContent = text;
        self.showAbsoluteDiv.showTextDiv = true;
        self.showAbsoluteDiv.showThreadResultDiv = false;
        self.showAbsoluteDiv.showThreadDiv = false;
    }

    self.showQryRealTimeThreadId = function () {
        self.viewShow.showAbsoluteDiv = true;
        self.showAbsoluteDiv.showTextDiv = false;
        self.showAbsoluteDiv.showThreadResultDiv = true;
        self.showAbsoluteDiv.showThreadDiv = true;
    }

    self.qryRealTimeThreadId = function () {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        body.targetIp = self.curThreadIp;
        body.threadId = self.showAbsoluteDiv.threadId;

        DeployMainService.threadInfo(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('实时查询线程失败 ' + response.errorMsg);
                } else {
                    if (response.threadInfo != null) {
                        self.showAbsoluteDiv.showThreadResult = '';
                        if (response.threadInfo.dumpResult === 1) {
                            self.showAbsoluteDiv.showThreadResult += response.threadInfo.dumpResultErrorText;
                        } else {
                            self.showAbsoluteDiv.showThreadResult += '线程名:' + response.threadInfo.threadName + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '线程状态:' + response.threadInfo.threadState + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '占用内存byte:' + response.threadInfo.threadAvailByte + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '线程锁:' + response.threadInfo.threadBlockedName + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '获取锁次数:' + response.threadInfo.threadBlockedCount + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '获取锁时长:' + response.threadInfo.threadBlockedTimeMs + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '等待锁ID:' + response.threadInfo.threadBlockedId + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '监控线程ID:' + response.threadInfo.waitFromThreadIds + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '监控锁名:' + response.threadInfo.lockedMonitors + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '监控等待时长:' + response.threadInfo.threadWaitedTimeMs + '<br/>';
                            self.showAbsoluteDiv.showThreadResult += '堆栈信息:' + response.threadInfo.threadStack;
                        }
                        angular.element('#' + self.showAbsoluteDiv.threadResultDivId).html(self.showAbsoluteDiv.showThreadResult);
                    }
                }
            }, function (err){
                console.log('实时查询线程失败 ' + err)
            });
    }

    self.showQueryMqView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryMqView = true;
    }
    self.showQuerySlowSqlView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.querySlowSqlView = true;
        self.qrySlowSqlView();
    }
    self.qrySlowSqlView = function () {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        DeployMainService.qrySlowSql(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询慢sql失败 ' + response.errorMsg);
                } else {
                    if (response.qrySlowSqlInfoList != null) {
                        self.savecurSlowSqlModel(response.qrySlowSqlInfoList);
                    }
                }
            }, function (err){
                console.log('查询慢sql失败 ' + err)
            });
    }
    self.savecurSlowSqlModel = function (list) {
        self.curSlowSqlModel = list;
    }
    self.SubmitServiceAdd = function () {
        self.viewShow.queryServiceAddView = true;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceModifyView = false;
    }
    self.closeServiceAddView = function () {
        self.viewShow.queryServiceModifyView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryServiceView = true;
    }
    self.SubmitServiceModify = function (srvInfo) {
        self.viewShow.queryServiceModifyView = true;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryServiceView = false;

        self.constSrvInfo.srvCode = srvInfo.serviceBeanName;
        self.constSrvInfo.serviceName = srvInfo.serviceName;
        self.constSrvInfo.providerName = srvInfo.providerName;
        self.srvModifyForm.srvCode = srvInfo.serviceBeanName;
        self.srvModifyForm.serviceName = srvInfo.serviceName;
        self.srvModifyForm.providerName = srvInfo.providerName;
        let tempConsumerName = '';
        for (let i =0; i < srvInfo.consumerName.length; i++) {
            if (tempConsumerName !== '') {
                tempConsumerName += '|';
            }
            tempConsumerName += srvInfo.consumerName[i];
        }
        self.srvModifyForm.consumerName = tempConsumerName;
        self.srvModifyForm.limitQps = srvInfo.limitQps;
        self.srvModifyForm.limitFallback = srvInfo.limitFallback;
        console.log(self.srvModifyForm);
    }
    self.closeServiceModifyView = function () {
        self.viewShow.queryServiceModifyView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryServiceView = true;
    }
    self.SubmitSrvAddInfo = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        self.srvForm.consumerName += '|evy-gateway';
        if (self.srvForm.consumerName.lastIndexOf('|') +1 === self.srvForm.length) {
            self.srvForm.consumerName += '|';
        }
        if (self.srvForm.consumerName.indexOf('evy-gateway') === -1) {
            self.srvForm.consumerName += 'evy-gateway';
        }
        DeployMainService.createSrvInfo(self.srvForm)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('新增服务码失败 ' + response.errorMsg);
                    self.srvForm = {};
                } else {
                    self.showTips('新增服务码成功,服务加载时进行发布者更新');
                    self.srvForm = {};
                }
                console.log(response)
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                console.log('新增服务码失败 ' + err);
                self.srvForm = {};
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }
    self.SubmitSrvModifyInfo = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        DeployMainService.modifySrvInfo(self.srvModifyForm)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('提交修改服务信息失败 ' + response.errorMsg);
                    self.srvModifyForm = {};
                } else {
                    self.showTips('提交修改服务信息成功');
                    self.srvModifyForm = {};
                }
                console.log(response)
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                console.log('提交修改服务信息失败 ' + err);
                self.srvModifyForm = {};
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }
    self.showQueryServiceView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryServiceView = true;
        self.qryServiceInfoView();
    }
    self.qryServiceInfoView = function () {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        DeployMainService.qrySrvInfo(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询服务信息失败 ' + response.errorMsg);
                } else {
                    if (response.qryServiceInfos != null) {
                        self.saveCurServiceInfo(response.qryServiceInfos);
                    }
                }
            }, function (err){
                console.log('查询服务信息失败 ' + err)
            });
    }
    self.saveCurServiceInfo = function (list) {
        self.curServiceInfo = list;
        console.log(list)
    }

    self.showQueryRedisView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryRedisView = true;
        self.qryRedisInfo();
    }

    self.qryRedisInfo = function () {
        let body = {};
        body.buildSeq = self.cur.deploySeq;
        body.userSeq = self.cur.userSeq;
        DeployMainService.qryRedisInfo(body)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询Redis健康信息失败 ' + response.errorMsg);
                } else {
                    self.saveCurRedisList(response.list);
                }
            }, function (err){
                console.log('查询Redis健康信息失败 ' + err)
            });
    }
    self.saveCurRedisList = function (list) {
        self.curRedisView = list;
    }

    self.SubmitQryHttpInfo = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        self.httpForm.buildSeq = self.cur.deploySeq;
        self.httpForm.userSeq = self.cur.userSeq;
        if (self.httpForm.limit === '') {
            self.httpForm.limit = 1;
        }
        DeployMainService.qryHttpInfo(self.httpForm)
            .then(function (response) {
                if (response.errorCode !== '0') {
                    self.showTips('查询Http请求信息失败 ' + response.errorMsg);
                } else {
                    self.viewShow.queryHttpResultView = true;
                    self.saveCurHttpInfoView(response.list);
                }
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                console.log('查询Http请求信息失败 ' + err);
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }
    self.saveCurHttpInfoView = function (list) {
        self.curHttpInfoView = list;
    }

    self.showQueryHttpView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryHttpView = true;
    }
    self.showTraceView = function (title) {
        self.showClose();
        self.setDeployInfoTitle(title);
        self.viewShow.queryTrackingView = true;
    }

    /********定义修改样式函数********/
    function showInputSelectStyle(inputDivId, inputId) {
        let style = 'background :#e8f0fe;';
        let div1 = 'form-cell-1';

        if (angular.equals(div1, inputDivId)) {
            angular.element('#' + inputDivId).attr('style', style + 'border-bottom-left-radius: 0;border-bottom-right-radius: 0;');
        } else {
            angular.element('#' + inputDivId).attr('style', style + 'border-top-left-radius: 0;border-top-right-radius: 0;');
        }

        angular.element('#' + inputId).attr('style', 'background :#e8f0fe;border:none;outline:medium;');
    }

    function hideInputSelectStyle(inputDivId, inputId) {
        let style = 'background :#fff;';
        let div1 = 'form-cell-1';

        if (angular.equals(div1, inputDivId)) {
            angular.element('#' + inputDivId).attr('style', style + 'border-bottom-left-radius: 0;border-bottom-right-radius: 0;');
        } else {
            angular.element('#' + inputDivId).attr('style', style + 'border-top-left-radius: 0;border-top-right-radius: 0;');
        }

        angular.element('#' + inputId).attr('style', 'background :#fff;border:none;outline:medium;');
    }

    /**
     * 鼠标移入时,高亮input输入框
     * @param inputId   input输入框id
     */
    self.InputMouseEnterEvent = function (inputId) {
        //('style', '-webkit-box-shadow: 0 0 5px rgba(0,113,241,1);')
        let cssName = 'InputMouseEnterEvent';
        angular.element('#' + inputId).addClass(cssName);
    }

    /**
     * 鼠标移出时,取消input输入框高亮
     * @param inputId   input输入框id
     */
    self.InputMouseLevelEvent = function (inputId) {
        let cssName = 'InputMouseEnterEvent';
        angular.element('#' + inputId).removeClass(cssName);
    }

    /**
     * 用户不存在配置信息时,隐藏deploy-list列表
     */
    self.HideDeployList = function () {
        angular.element('#deploy-list-select').attr('style', 'display: none;');
        for (let i = 1; i <= 7; i++) {
            angular.element('#deploy-list-' + i).attr('style', 'display: none;');
        }
    }

    /**
     * 用户存在配置信息时,展示deploy-list列表
     */
    self.ShowDeployList = function () {
        angular.element('#deploy-list-select').attr('style', '');
        for (let i = 1; i <= 7; i++) {
            angular.element('#deploy-list-' + i).attr('style', '');
        }
    }

    /**
     * 启动或禁用button
     * @param btnId button ID
     * @param bool true 禁用按钮 false 启用按钮
     */
    self.btnDisplay = function (btnId, bool) {
        angular.element('#' + btnId).attr('disabled', bool);
    }

    self.GetGitBranchListBtnText = {
        isSubmit: false,
        noSubmitText: '获取分支列表 ',
        SubmitText: '稍等... ',
        lodingSpan: false
    }
    /**
     * GetGitBranchList 提交时按钮样式
     * @param isSubmit true 禁用按钮 false 启用按钮
     */
    self.GetGitBranchListStyle = function (btnId, isSubmit) {
        self.btnDisplay(btnId, isSubmit);
        self.GetGitBranchListBtnText.isSubmit = isSubmit;
        self.GetGitBranchListBtnText.lodingSpan = isSubmit;
    }

    self.SubmitNewDeployInfoText = {
        isSubmit: false,
        noSubmitText: '提交 ',
        SubmitText: '提交中... ',
        lodingSpan: false
    }
    /**
     * SubmitNewDeployInfo 提交时按钮样式
     * @param isSubmit true 禁用按钮 false 启用按钮
     */
    self.SubmitNewDeployInfoStyle = function (btnId, isSubmit) {
        self.btnDisplay(btnId, isSubmit);
        self.SubmitNewDeployInfoText.isSubmit = isSubmit;
    }

    self.BtnLodingStyle = function (objId) {
        let lodingClass = 'icon-spinner icon-spin';
        self.SetNewClass(objId, lodingClass);
    }

    self.SetNewClass = function (objId, className) {
        angular.element('#' + objId).attr('class','');
        angular.element('#' + objId).addClass(className);
    }

    self.showTips = function (title) {
        self.tips.post = title;
        angular.element('#' + self.tips.id).attr('style', 'width:100%;z-index: 1001');

        setTimeout(()=>{
            self.tips.post = '';
            angular.element('#' + self.tips.id).attr('style', 'display: none;z-index: 1001');
        }, 2000);
    }

    self.deployInfoStyle = {
        successStyle : 'span-success',
        faildStyle : 'span-faild',
        checkingStyle : 'span-checking',
        buildInfo : {
            id : 'build-status-text',
            title : '编译中',
            show : false,
            buildSuccessStatus : false,
            buildFalidStatus : false,
            buildCheckStatus : false,
            buildSuccessTitle : '编译成功',
            buildFalidTitle : '编译失败',
            buildCheckTitle : '编译中'
        },
        deployInfo : {
            id : 'deploy-status-text',
            title : '部署中',
            show : false,
            deploySuccessStatus : false,
            deployFalidStatus : false,
            deployCheckStatus : false,
            deploySuccessTitle : '部署成功',
            deployFalidTitle : '部署失败',
            deployCheckTitle : '部署中'
        },
        checkInfo : {
            id : 'check-service-status-text',
            title : '服务检查中',
            show : false,
            checkSuccessStatus : false,
            checkFalidStatus : false,
            checkCheckStatus : false,
            checkSuccessTitle : '服务正常',
            checkFalidTitle : '服务异常',
            checkCheckTitle : '服务检查中'
        },
    }

    /********定义请求后端函数********/

    /**
     * 提交登陆请求
     */
    self.SubmitLogin = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        if (self.loginUser.username === '' || self.loginUser.password === '') {
            self.showTips('登录项不能填空');
            self.btnDisplay(btnId, false);
            self.SetNewClass(spanId, spanClass);
        } else {
            self.loginUser.password = DeployMainService.encCode(self.loginUser.password);
            DeployMainService.login(self.loginUser)
                .then(function (response) {
                    if (response.errorCode !== '0') {
                        self.showTips('登录失败 ' + response.errorMsg);
                    } else {
                        //登录成功处理
                        self.loginSuccess(self.loginUser.username, response.userSeq);
                        self.qryDeployInfoReq(response.userSeq);
                    }
                    self.loginUser.password = '';
                    self.btnDisplay(btnId, false);
                    self.SetNewClass(spanId, spanClass);
                }, function (err){
                    self.showTips('登录失败 ' + err);
                    self.loginUser.username = '';
                    self.loginUser.password = '';
                    self.btnDisplay(btnId, false);
                    self.SetNewClass(spanId, spanClass);
                });
        }
    }

    self.SubmitMqInfo = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        self.mqForm.userSeq = self.cur.userSeq;
        self.mqForm.buildSeq = self.cur.deploySeq;
        DeployMainService.qryMqInfo(self.mqForm)
            .then(function (response){
                if (response.errorCode !== '0') {
                    self.showTips('获取MQ信息异常 ' + response.errorMsg);
                    self.curMqTraceInfo = [];
                } else {
                    //返回0到N条记录,参考com.evy.linlin.trace.dto.QryMqTraceInfoOutDTO
                    console.log(response.list)
                    if (response.list != null) {
                        self.curMqTraceInfo = response.list;
                        self.viewShow.queryMqQueryResultView = true;
                    }
                }
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                self.showTips('获取MQ信息异常 ' + err);
                self.btnDisplay(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }

    self.showMqContent = function (text) {
        self.viewShow.showAbsoluteDiv = true;
        self.showAbsoluteDiv.showDivTitle = '消息正文';
        self.showAbsoluteDiv.showDivContent = text;
    }

    self.closeAbsoluteDiv = function () {
        self.viewShow.showAbsoluteDiv = false;
    }

    /**
     * 登录成功处理,获取用户名下应用集合<br/>
     * 存在记录,则初始化self.selectBranch分支模块
     * @param userSeq
     */
    self.qryDeployInfoReq = function (userSeq) {
        //检查用户名下最新部署记录,用于展示"自动化部署"界面,不存在记录则展示"新增部署应用"
        self.qryDeployInfo.userSeq = userSeq;
        DeployMainService.qryDeployInfo(self.qryDeployInfo)
            .then(function (response){
                if (response.errorCode !== '0') {
                    self.showTips('获取用户部署信息异常 ' + response.errorMsg);
                } else {
                    //返回0到N条记录,参考com.evy.linlin.deploy.dto.QryDeployInfoOutDTO
                    if (response.dtoList !== null && response.dtoList.length > 0) {
                        //展示最新一条记录
                        let lastArray = response.dtoList[response.dtoList.length -1];
                        if (self.cur.appName === 'none') {
                            self.cur.appName = lastArray.appName;
                        }
                        if (self.cur.branch === 'none') {
                            self.cur.branch = lastArray.gitBrchan;
                        }
                        self.initBranch(response);
                        self.initLastDeployForm(lastArray);
                    }
                }
            }, function (err){
                self.showTips('获取用户部署信息异常 ' + err);
            });
    }

    self.buildInfoSuccess =function () {
        self.deployInfoStyle.buildInfo.show = true;
        self.deployInfoStyle.buildInfo.buildSuccessStatus = true;
        self.deployInfoStyle.buildInfo.title = self.deployInfoStyle.buildInfo.buildSuccessTitle;
        self.SetNewClass(self.deployInfoStyle.buildInfo.id, 'title-span left-span span-success');
    }

    self.buildInfoChecked =function () {
        self.deployInfoStyle.buildInfo.show = true;
        self.deployInfoStyle.buildInfo.buildCheckStatus = true;
        self.deployInfoStyle.buildInfo.title = self.deployInfoStyle.buildInfo.buildCheckTitle;
        self.SetNewClass(self.deployInfoStyle.buildInfo.id, 'title-span left-span span-checking');
    }

    self.buildInfoFaild =function () {
        self.deployInfoStyle.buildInfo.show = true;
        self.deployInfoStyle.buildInfo.buildFalidStatus = true;
        self.deployInfoStyle.buildInfo.title = self.deployInfoStyle.buildInfo.buildFalidTitle;
        self.SetNewClass(self.deployInfoStyle.buildInfo.id, 'title-span left-span span-faild');
    }

    self.deployInfoSuccess = function () {
        self.buildInfoSuccess();
        self.deployInfoStyle.deployInfo.show = true;
        self.deployInfoStyle.deployInfo.deploySuccessStatus = true;
        self.deployInfoStyle.deployInfo.title = self.deployInfoStyle.deployInfo.deploySuccessTitle;
        self.SetNewClass(self.deployInfoStyle.deployInfo.id, 'title-span left-span span-success');
    }

    self.deployInfoChecked = function () {
        self.buildInfoSuccess();
        self.deployInfoStyle.deployInfo.show = true;
        self.deployInfoStyle.deployInfo.deployCheckStatus = true;
        self.deployInfoStyle.deployInfo.title = self.deployInfoStyle.deployInfo.deployCheckTitle;
        self.SetNewClass(self.deployInfoStyle.deployInfo.id, 'title-span left-span span-checking');
    }

    self.deployInfoFaild = function () {
        self.buildInfoSuccess();
        self.deployInfoStyle.deployInfo.show = true;
        self.deployInfoStyle.deployInfo.deployFalidStatus = true;
        self.deployInfoStyle.deployInfo.title = self.deployInfoStyle.deployInfo.deployFalidTitle;
        self.SetNewClass(self.deployInfoStyle.deployInfo.id, 'title-span left-span span-faild');
    }

    self.checkInfoSuccess = function () {
        self.buildInfoSuccess();
        self.deployInfoSuccess();
        self.deployInfoStyle.checkInfo.show = true;
        self.deployInfoStyle.checkInfo.checkSuccessStatus = true;
        self.deployInfoStyle.checkInfo.title = self.deployInfoStyle.checkInfo.checkSuccessTitle;
        self.SetNewClass(self.deployInfoStyle.checkInfo.id, 'title-span left-span span-success');
    }

    self.checkInfoChecked = function () {
        self.buildInfoSuccess();
        self.deployInfoSuccess();
        self.deployInfoStyle.checkInfo.show = true;
        self.deployInfoStyle.checkInfo.checkCheckStatus = true;
        self.deployInfoStyle.checkInfo.title = self.deployInfoStyle.checkInfo.checkCheckTitle;
        self.SetNewClass(self.deployInfoStyle.checkInfo.id, 'title-span left-span span-checking');
    }

    self.checkInfoFaild = function () {
        self.buildInfoSuccess();
        self.deployInfoSuccess();
        self.deployInfoStyle.checkInfo.show = true;
        self.deployInfoStyle.checkInfo.checkFalidStatus = true;
        self.deployInfoStyle.checkInfo.title = self.deployInfoStyle.checkInfo.checkFalidTitle;
        self.SetNewClass(self.deployInfoStyle.checkInfo.id, 'title-span left-span span-faild');
    }

    /**
     * 初始化self.lastDeployForm自动化部署信息
     * @param lastArray
     */
    self.initLastDeployForm = function (lastArray) {
        self.lastDeployForm.deployTime = lastArray.createDateTime;
        self.lastDeployForm.appName = lastArray.appName;
        self.lastDeployForm.switchJunit = (lastArray.switchJunit === 0);
        self.lastDeployForm.switswitchBatchDeploy = (lastArray.switchBatchDeploy === 0);
        self.lastDeployForm.gitPath = lastArray.gitPath;
        self.lastDeployForm.targetHost = lastArray.targetHost;
        self.lastDeployForm.jvmParam = lastArray.jvmParam;
        self.checkStageFlag(lastArray.stageFlag);

        self.cur.deploySeq = lastArray.deploySeq;
        if (lastArray.targetHost !== null && lastArray.targetHost !== '') {
            self.curSelectIp = lastArray.targetHost.split(',', -1);
            self.curThreadIp = self.curSelectIp[0];
        }
    }

    /**
     * 检查并设置部署状态
     * @param stageFlag
     * @return boolean 返回true表示状态明确
     */
    self.checkStageFlag = function (stageFlag) {
        console.log(stageFlag)
        let flag = false;
        //jar部署阶段 0a:编译成功 0b:编译中 0c:编译失败 1a:部署成功 1b:部署中 1c:部署失败
        //顺序: 编译->部署->检查服务
        switch (stageFlag) {
            case '0a' :
                self.buildInfoSuccess();
                flag = true;
                break;
            case '0b' :
                self.buildInfoChecked();
                break;
            case '0c' :
                self.buildInfoFaild();
                flag = true;
                break;
            case '1a' :
                self.deployInfoSuccess();
                flag = true;
                break;
            case '1b' :
                self.deployInfoChecked();
                break;
            case '1c' :
                self.deployInfoFaild();
                flag = true;
                break;
        }
        return flag;
    }

    /**
     * 初始化self.selectBranch选择分支模块
     * @param response
     */
    self.initBranch = function (response) {
        for (let i =0; i < response.length; i++) {
            self.selectBranch[i].appName = response.dtoList[i].appName;
            self.selectBranch[i].branchName = response.dtoList[i].gitBrchan;
            self.selectBranch[i].gitPath = response.dtoList[i].gitPath;
            self.selectBranch[i].serverIp = response.dtoList[i].targetHost;
            self.selectBranch[i].deploySeq = response.dtoList[i].deploySeq;
        }
    }

    /**
     * 登录成功处理
     * @param userName 登录用户名
     * @param userSeq  用户标识userSeq
     */
    self.loginSuccess = function (userName, userSeq) {
        self.isLogin = true;
        self.loginToken = userSeq;
        self.cur.user = userName;
        self.cur.userSeq = userSeq;
    }

    /**
     * 重新部署
     * @param event
     */
    self.redeployment = function (event) {

    }

    /**
     * 部署记录
     * @param event
     */
    self.deployRecord = function (event) {

    }

    /**
     * 一键部署
     * @param event
     */
    self.autoDeploySubmit = function (event) {
        DeployMainService.nextDeployBuild(self.lastDeployForm)
            .then(function (response){
                if (response.errorCode !== '0') {
                    self.showTips('部署异常 ' + response.errorMsg);
                } else {
                    //返回buildSeq,参考com.evy.linlin.deploy.dto.NextDeployBuildSeqOutDTO
                    if (response.buildSeq === '') {
                        self.showTips('部署异常 buildSeq为空');
                    } else {
                        let buildSeq = response.buildSeq;
                        DeployMainService.autoDeploy(buildSeq)
                            .then(function (response){
                                if (response.errorCode !== '0') {
                                    self.showTips('部署异常 ' + response.errorMsg);
                                } else {
                                    //新建定时回查任务
                                    self.newReviewDeployInfoTask(buildSeq);
                                }
                            }, function (err){
                                self.showTips('一键部署异常 ', err);
                            });
                    }
                }
            }, function (err){
                self.showTips('部署异常 ' + err);
            });
    }

    /**
     * 定时回查,状态为:失败，则停止回查
     */
    self.timerTask;
    self.newReviewDeployInfoTask = function (buildSeq) {
        //回查前清除存量定时任务
        self.rmReviewDeployInfoTask();
        self.timerTask = setTimeout(function (){
            DeployMainService.deployReview(buildSeq)
                .then(function (response){
                    if (response.errorCode !== '0') {
                    } else {
                        //返回stageFlag,参考com.evy.linlin.deploy.dto.ReviewStatusOutDTO
                        let result = self.checkStageFlag(response.stageFlag);
                        if (result) {
                            //状态明确，停止回查
                            self.rmReviewDeployInfoTask();
                        }
                    }
                }, function (err){});
        }, 3000);
    }

    self.rmReviewDeployInfoTask = function () {
        if (self.timerTask !== undefined){
            clearTimeout(self.timerTask);
        }
    }

    //TODO 按钮loding状态，参考SubmitNewDeployInfo
    self.showBtnLodingStyle = function (event, isSubmit) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.children.item(0).attributes.item(0).nodeValue;
        let spanClass = event.target.children.item(0).attributes.item(1).nodeValue;
        self.btnDisplay(btnId, isSubmit);
    }

    /**
     * 提交新建应用部署配置项
     */
    self.SubmitNewDeployInfo = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.children.item(0).attributes.item(0).nodeValue;
        let spanClass = event.target.children.item(0).attributes.item(1).nodeValue;
        self.SubmitNewDeployInfoStyle(btnId, true);
        self.BtnLodingStyle(spanId);

        self.deployForm.userSeq = self.cur.userSeq;
        //获取Junit选项
        if (angular.element('#JunitCheckBox').prop('checked')) {
            self.deployForm.switchJunit = 0;
        }
        //获取分批部署选项
        if (angular.element('#BatchDeployCheckBox').prop('checked')) {
            self.deployForm.switchBatchDeploy = 0;
        }

        //提交请求
        DeployMainService.createDeployInfo(self.deployForm)
            .then(function (response){
                if (response.errorCode !== '0') {
                    self.showTips('新建部署信息异常 ' + response.errorMsg);
                } else {
                    //返回deploySeq,参考com.evy.linlin.deploy.dto.CreateDeployInfoOutDTO
                    console.log(response);
                    self.qryDeployInfo.deploySeq = response.deploySeq;

                    //重新获取部署信息,新用户获取了也没用呀？
                    self.qryDeployInfoReq(self.cur.userSeq);
                }
                self.SubmitNewDeployInfoStyle(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                self.showTips('新建部署信息异常 ' + err);
                self.SubmitNewDeployInfoStyle(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }

    /**
     * 获取git分支列表
     */
    self.GetGitBranchList = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.children.item(0).attributes.item(0).nodeValue;
        let spanClass = event.target.children.item(0).attributes.item(1).nodeValue;

        self.GetGitBranchListStyle(btnId, true);
        self.BtnLodingStyle(spanId);
        //TODO 获取分支请求
        self.GetGitBranchListStyle(btnId, false);
        self.SetNewClass(spanId, spanClass);
    }

    /**
     * 切换分支
     * @param deploySeq 部署序列号,根据序列号获取相应分支配置
     */
    self.selectBranchSubmit = function (deploySeq) {
        self.selectBranch.forEach(value => {
            console.log(value)
            if (value.deploySeq === deploySeq) {
                self.showTips(value.branchName +'分支切换成功');
            }
        })
    }

    /**
     * 提交查询traceId
     */
    self.SubmitQryTraceId = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.attributes.item(0).nodeValue;
        let spanClass = event.target.attributes.item(1).nodeValue;
        self.btnDisplay(btnId, true);
        self.BtnLodingStyle(spanId);

        //提交请求
        DeployMainService.qryTraceInfo(self.traceFrom)
            .then(function (response){
                if (response.errorCode !== '0') {
                    self.showTips('查询traceId失败 ' + response.errorMsg);
                } else {
                    //返回参考com.evy.linlin.trace.dto.QryTrackingInfoOutDTO
                    console.log(response);
                    if (response.traceList != null) {
                        self.buildCurTraceListResult(response.traceList);
                        self.viewShow.queryTrackingResultView = true;
                    }
                }
                self.SubmitNewDeployInfoStyle(btnId, false);
                self.SetNewClass(spanId, spanClass);
            }, function (err){
                self.showTips('查询traceId失败 ' + err);
                self.SubmitNewDeployInfoStyle(btnId, false);
                self.SetNewClass(spanId, spanClass);
            });
    }

    self.buildCurTraceListResult = function (list) {
        for (let i = 0; i < list.length; i++) {
            let date = new Date(list[i].order);
            list[i].order = date.getFullYear()+'-'+(date.getMonth()+1)+'-'+date.getDate()+' '
                +('0'+date.getHours()).slice(-2)+':'+('0'+date.getMinutes()).slice(-2)+':'+('0'+date.getSeconds()).slice(-2)+'.'+date.getMilliseconds();
        }

        self.curTraceListResult = list;
        self.buildTraceDivSpan();
        self.buildTraceDivMsSpan();
    }

    /**
     * 构建链路占比进度条
     */
    self.buildTraceDivSpan = function () {
        let srvInt = 0;
        let dbInt = 0;
        let httpInt = 0;
        let mqInt = 0;
        let leftRadiusClass = 'left-div-radius';
        let rightRadiusClass = 'right-div-radius';
        let startFlag = true;
        let endFlag = true;

        for (let i=0; i < self.curTraceListResult.length; i++) {
            switch (self.curTraceListResult[i].reqType) {
                case '0' : ++srvInt; break;
                case '1' : ++dbInt; break;
                case '2' : ++httpInt; break;
                case '3' : ++mqInt; break;
            }
        }

        //赋值style属性
        srvInt = srvInt <= 0? "0%" : Math.round((srvInt / self.curTraceListResult.length) * 10000) / 100.0 + "%";
        dbInt = dbInt <= 0? "0%" : Math.round((dbInt / self.curTraceListResult.length) * 10000) / 100.0 + "%";
        httpInt = httpInt <= 0? "0%" : Math.round((httpInt / self.curTraceListResult.length) * 10000) / 100.0 + "%";
        mqInt = mqInt <= 0? "0%" : Math.round((mqInt / self.curTraceListResult.length) * 10000) / 100.0 + "%";
        angular.element('#srv-span').attr('style', 'width:' + srvInt);
        angular.element('#db-span').attr('style', 'width:' + dbInt);
        angular.element('#http-span').attr('style', 'width:' + httpInt);
        angular.element('#mq-span').attr('style', 'width:' + mqInt);

        if (srvInt !== "0%" && startFlag) {
            self.SetNewClass('srv-span', leftRadiusClass);
            startFlag = false;
        } else if (dbInt !== "0%" && startFlag) {
            self.SetNewClass('db-span', leftRadiusClass);
            startFlag = false;
        } else if (httpInt !== "0%" && startFlag) {
            self.SetNewClass('http-span', leftRadiusClass);
            startFlag = false;
        } else {
            self.SetNewClass('mq-span', leftRadiusClass);
            startFlag = false;
        }

        if (mqInt !== "0%" && endFlag) {
            self.SetNewClass('mq-span', rightRadiusClass);
            endFlag = false;
        } else if (httpInt !== "0%" && endFlag) {
            self.SetNewClass('http-span', rightRadiusClass);
            endFlag = false;
        } else if (dbInt !== "0%" && endFlag) {
            self.SetNewClass('db-span', rightRadiusClass);
            endFlag = false;
        } else {
            self.SetNewClass('srv-span', rightRadiusClass);
            endFlag = false;
        }
    }

    /**
     * 构建链路耗时占比进度条
     */
    self.buildTraceDivMsSpan = function () {
        let srvInt = 0;
        let dbInt = 0;
        let httpInt = 0;
        let mqInt = 0;
        let totalInt = 0;
        let leftRadiusClass = 'left-div-radius';
        let rightRadiusClass = 'right-div-radius';
        let startFlag = true;
        let endFlag = true;

        for (let i=0; i < self.curTraceListResult.length; i++) {
            switch (self.curTraceListResult[i].reqType) {
                case '0' : srvInt += parseInt(self.curTraceListResult[i].takeTimeMs); break;
                case '1' : dbInt += parseInt(self.curTraceListResult[i].takeTimeMs); break;
                case '2' : httpInt += parseInt(self.curTraceListResult[i].takeTimeMs); break;
                case '3' : mqInt += parseInt(self.curTraceListResult[i].takeTimeMs); break;
            }
            totalInt += parseInt(self.curTraceListResult[i].takeTimeMs);
        }

        //赋值style属性
        srvInt = srvInt <= 0? "0%" : Math.round((srvInt / totalInt) * 10000) / 100.0 + "%";
        dbInt = dbInt <= 0? "0%" : Math.round((dbInt / totalInt) * 10000) / 100.0 + "%";
        httpInt = httpInt <= 0? "0%" : Math.round((httpInt / totalInt) * 10000) / 100.0 + "%";
        mqInt = mqInt <= 0? "0%" : Math.round((mqInt / totalInt) * 10000) / 100.0 + "%";
        angular.element('#srv-ms-span').attr('style', 'width:' + srvInt);
        angular.element('#db-ms-span').attr('style', 'width:' + dbInt);
        angular.element('#http-ms-span').attr('style', 'width:' + httpInt);
        angular.element('#mq-ms-span').attr('style', 'width:' + mqInt);

        if (srvInt !== "0%" && startFlag) {
            self.SetNewClass('srv-ms-span', leftRadiusClass);
            startFlag = false;
        } else if (dbInt !== "0%" && startFlag) {
            self.SetNewClass('db-ms-span', leftRadiusClass);
            startFlag = false;
        } else if (httpInt !== "0%" && startFlag) {
            self.SetNewClass('http-ms-span', leftRadiusClass);
            startFlag = false;
        } else {
            self.SetNewClass('mq-ms-span', leftRadiusClass);
            startFlag = false;
        }

        if (mqInt !== "0%" && endFlag) {
            self.SetNewClass('mq-ms-span', rightRadiusClass);
            endFlag = false;
        } else if (httpInt !== "0%" && endFlag) {
            self.SetNewClass('http-ms-span', rightRadiusClass);
            endFlag = false;
        } else if (dbInt !== "0%" && endFlag) {
            self.SetNewClass('db-ms-span', rightRadiusClass);
            endFlag = false;
        } else {
            self.SetNewClass('srv-ms-span', rightRadiusClass);
            endFlag = false;
        }
    }

    /**
     * TODO TEST
     */
    self.init = function (){
        self.deployViewReqList = [
            {
                'branchName' : 'master'
            },
            {
                'branchName' : 'master1'
            },
            {
                'branchName' : 'master2'
            },
            {
                'branchName' : 'master3'
            },
            {
                'branchName' : 'YD_2020_11_23'
            },
            {
                'branchName' : 'YD_2020_11_23'
            },
            {
                'branchName' : 'YD_2020_11_23_222222222'
            }
        ]

        self.selectBranch = [
            {
                appName : 'evy-deploy-center',
                branchName : 'YD_2020_11_27',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
            {
                appName : 'evy-deploy-centeradfasdfadf',
                branchName : 'YD_2020_11_27',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
            {
                appName : 'evy-deploy-center',
                branchName : 'YD_2020_11_27asdfasdfasdf',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
            {
                appName : 'evy-deploy-centerasdfasdfasdf',
                branchName : 'YD_2020_11_27asdfasdfasdf',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
            {
                appName : 'evy-deploy-center',
                branchName : 'YD_2020_11_27asdf',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
            {
                appName : 'evy-deploy-centerasdf',
                branchName : 'YD_2020_11_27',
                gitPath : 'https://github.com/Evycc/evy-business',
                serverIp : '127.0.0.1',
                deploySeq : '123546978'
            },
        ]
    }

    /**
     * 展示服务器cpu使用率
     */
    self.initMemoryViewJs = function (outMap) {
        let chart1 = {
            title: {
                text: '服务器cpu使用率'
            },
            yAxis: {
                title: {
                    text: 'cpu使用率'
                },
                max: 100,
                labels: {
                    formatter: function () {
                        return this.value + '%';
                    }
                }
            },
            xAxis: {
                type: 'datetime',
                labels: {
                    format: '{value:%Y-%m-%d}',
                    rotation: 45,
                    align: 'left'
                }
            },
            tooltip: {
                pointFormat: '{series.name} <b>{point.y}</b>%'
            },
            legend: {
                layout: 'vertical',
                align: 'right',
                verticalAlign: 'middle'
            },
            plotOptions: {
                series: {
                    label: {
                        connectorAllowed: false,
                        format: '{value:%Y-%m-%d}'
                    },
                    // pointStart: Date.UTC(2020, 11, 28, 20, 45, 27),
                    pointInterval: 60 * 1000
                }
            },
            series: [
                {
                    name: '127.0.0.2',
                    data: [1.21, 0.34, 0.01, 0.04, 0.02, 0.01, 0.12]
                }
            ],
            responsive: {
                rules: [{
                    condition: {
                        maxWidth: 500
                    },
                    chartOptions: {
                        legend: {
                            layout: 'horizontal',
                            align: 'center',
                            verticalAlign: 'bottom'
                        }
                    }
                }]
            }
        };

        let index=0;
        let maxIpLength = 0;
        for (let ip in outMap) {
            let sysMemoryTotal = 0;
            let sysAvailMemoryList = [];
            let appUseMemoryList = [];
            let appHeapMaxTotal = [];
            let appHeapMinTotal = [];
            let appHeapUseList = [];
            let appNoHeapMaxTotal = [];
            let appNoHeapMinTotal = [];
            let appNoHeapUseList = [];
            /*cpu图形处理*/
            if (chart1.series[index] === undefined) {
                chart1.series[index] = {};
            }
            chart1.series[index].name = ip;
            if (chart1.series[index].data === undefined) {
                chart1.series[index].data = [];
            }
            /*cpu图像处理 end*/
            for (let i =0; i < outMap[ip].length; i++) {
                chart1.series[index].data.push(parseFloat(outMap[ip][i].cpuLoadPercentage) *100);

                //服务器最大内存
                sysMemoryTotal = sysMemoryTotal < parseInt(outMap[ip][i].sysMermoryKb) ? parseInt(outMap[ip][i].sysMermoryKb) : sysMemoryTotal;
                //服务器系统可用内存集合
                sysAvailMemoryList.push(parseInt(outMap[ip][i].sysAvailMermoryKb) /1024/1024);
                //应用占用内存集合
                appUseMemoryList.push(parseInt(outMap[ip][i].sysUseMermoryKb) /1024/1024);
                //应用堆最大内存
                appHeapMaxTotal.push(parseInt(outMap[ip][i].appHeapMaxMermoryKb) /1024/1024);
                //应用堆最小内存
                appHeapMinTotal.push(parseInt(outMap[ip][i].appHeapMinMermoryKb) /1024/1024);
                //应用堆内存占用集合
                appHeapUseList.push(parseInt(outMap[ip][i].appHeapUseMermoryKb) /1024/1024);
                //应用非堆最大内存
                appNoHeapMaxTotal.push(parseInt(outMap[ip][i].appNoHeapMaxMermoryKb) /1024/1024);
                //应用非堆最小内存
                appNoHeapMinTotal.push(parseInt(outMap[ip][i].appNoHeapMinMermoryKb) /1024/1024);
                //应用非堆内存占用集合
                appNoHeapUseList.push(parseInt(outMap[ip][i].appNoHeapUseMermoryKb) /1024/1024);
            }

            /*内存图像处理*/
            let chart2 = {
                chart: {
                    type: 'area'
                },
                title: {
                    text: '服务器内存使用率'
                },
                subtitle: {
                    text: ip
                },
                xAxis: {
                    type: 'datetime',
                    labels: {
                        format: '{value:%Y-%m-%d}',
                        rotation: 45,
                        align: 'left'
                    }
                },
                yAxis: {
                    title: {
                        text: '系统内存,单位MB'
                    },
                    max: sysMemoryTotal/1024/1024,
                    endOnTick: false,
                    labels: {
                        formatter: function () {
                            return this.value + 'MB';
                        }
                    }
                },
                tooltip: {
                    pointFormat: '{series.name} <b>{point.y:,.0f}</b>MB'
                },
                plotOptions: {
                    series: {
                        label: {
                            connectorAllowed: false,
                            format: '{value:%Y-%m-%d}'
                        },
                        // pointStart: Date.UTC(2020, 11, 28, 20, 45, 27),
                        pointInterval: 60 * 1000
                    }
                },

                series: [{
                    name: '系统可用内存',
                    data: sysAvailMemoryList
                }, {
                    name: '应用占用内存',
                    data: appUseMemoryList
                }, {
                    name: '应用堆内存占用',
                    data: appHeapUseList
                }, {
                    name: '应用非堆内存占用',
                    data: appNoHeapUseList
                }, {
                    name: '应用最大堆内存',
                    data: appHeapMaxTotal
                }, {
                    name: '应用最小堆内存',
                    data: appHeapMinTotal
                }, {
                    name: '应用最大非堆内存',
                    data: appNoHeapMaxTotal
                }, {
                    name: '应用最小非堆内存',
                    data: appNoHeapMinTotal
                }]
            };
            let viewId = 'appMemoryView'+index;
            let div = document.createElement("div");
            div.id = viewId;
            angular.element('#' + self.viewId.queryMemoryView).append(div);
            self.SetNewClass(viewId, 'chart-style');
            console.log(chart2)
            /*内存图像处理 end*/

            maxIpLength = maxIpLength === 0 ? outMap[ip].length : maxIpLength < outMap[ip].length ? outMap[ip].length : maxIpLength;
            index++;
            self.chartPlotOptions(chart2, maxIpLength);
            Highcharts.chart(viewId,chart2);
        }

        self.chartPlotOptions(chart1, maxIpLength);

        Highcharts.chart('appCpuView', chart1);
    }

    self.chartPlotOptions = function (chart, len) {
        let date = new Date();
        if (chart.plotOptions === undefined) {
            chart.plotOptions = {};
        }
        if (chart.plotOptions.series === undefined) {
            chart.plotOptions.series = {};
        }
        if (chart.plotOptions.series.label === undefined) {
            chart.plotOptions.series.label = {};
        }
        chart.plotOptions.series.label.connectorAllowed = false;
        chart.plotOptions.series.label.format = '{value:%Y-%m-%d}';
        chart.plotOptions.series.pointStart = Date.UTC(date.getFullYear(), date.getMonth(), date.getDate(), 0, 0, 0);
        // chart.plotOptions.series.pointInterval = (24 * 60 * 60 * 1000) / len;
    }

    /**
     * TODO TEST
     */
    self.init();
    // self.showDeployMainView('自动化部署');
    // self.initMemoryViewJs();
}]);


