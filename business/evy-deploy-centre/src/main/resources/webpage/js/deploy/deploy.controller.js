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
        queryServiceLimitView : 'deploy-service-limit-view',
        queryServiceAuthView : 'deploy-service-auth-view',
        queryRedisView : 'deploy-redis-view',
        queryHttpView : 'deploy-http-view',
        queryHttpResultView : 'deploy-http-result-view',
        queryTrackingView : 'deploy-trace-view',
        queryTrackingResultView : 'deploy-trace-result-view',
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
        queryServiceLimitView : false,
        queryServiceAuthView : false,
        queryServiceAddView : false,
        queryRedisView : false,
        queryHttpView : false,
        queryHttpResultView: false,
        queryTrackingView: false,
        queryTrackingResultView: false
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
        message: '',
        limit: ''
    }
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
        console.log(newValue)
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
    /**
     * 展示新增部署应用页面
     * @param title
     */
    self.showDeployCreateView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = true;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showQueryMemoryView = function (title){
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = true;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    /**
     * 展示部署配置信息页面
     * @param title
     */
    self.showDeployMainView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = true;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    /**
     * 展示切换分支页面
     * @param title
     */
    self.showSelectBranchView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = true;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }

    self.showQueryThreadView = function(title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = true;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
        self.qryThreadInfoList('', 0, 100);
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
                        for (let i=0; i < response.list.length; i++) {
                            self.addThreadInfoUl(response.list[i], i);
                        }
                    }
                    console.log(self.curThreadIp)
                    console.log(body)
                    //分段获取
                    if (self.curThreadIp === body.serviceIp && body.beginIndex < (response.total - body.endIndex)) {
                        self.qryThreadInfoList('', body.beginIndex + body.endIndex, body.endIndex);
                    }
                }
                console.log(response)
            }, function (err){
                self.showTips('查询线程信息失败 ' + err);
            });
    }

    /**
     * 添加间隔li对象
     * @param ul 目标ul
     */
    self.addtempLi = function (ul) {
        let publicLi = document.createElement('li');
        publicLi.className  = 'main-font-color';
        publicLi.innerHTML = '/';
        ul.appendChild(publicLi);
    }

    self.addThreadInfoUl = function (list, index) {
        console.log(list)
        let addUl = document.createElement('ul');
        let tempIdName = 'tempThreadId';
        let btnText = '堆栈查询';
        if (/^\d+$/.test(list.threadMaxCount) && self.ThreadCount < list.threadMaxCount) {
            self.ThreadCount = list.threadMaxCount;
        }

        addUl.id = tempIdName + index;

        let publicLi = document.createElement('li');
        publicLi.className  = 'main-font-color';
        publicLi.innerHTML = '/';

        let threadIdLi = document.createElement('li');
        threadIdLi.className = 'twelve-ul-style';
        threadIdLi.innerHTML = list.threadId;
        addUl.appendChild(threadIdLi);
        self.addtempLi(addUl);

        let threadNameLi = document.createElement('li');
        threadNameLi.className = 'twelve-ul-style';
        threadNameLi.innerHTML = list.threadName;
        addUl.appendChild(threadNameLi);
        self.addtempLi(addUl);

        let threadStatusLi = document.createElement('li');
        threadStatusLi.className = 'twelve-ul-style';
        threadStatusLi.innerHTML = list.threadStatus;
        addUl.appendChild(threadStatusLi);
        self.addtempLi(addUl);

        let threadStartTimeMsLi = document.createElement('li');
        threadStartTimeMsLi.className = 'twelve-ul-style';
        threadStartTimeMsLi.innerHTML = list.threadStartTimeMs;
        addUl.appendChild(threadStartTimeMsLi);
        self.addtempLi(addUl);

        let threadBlockedCountLi = document.createElement('li');
        threadBlockedCountLi.className = 'twelve-ul-style';
        threadBlockedCountLi.innerHTML = list.threadBlockedCount;
        addUl.appendChild(threadBlockedCountLi);
        self.addtempLi(addUl);

        let threadBlockedTimeMsLi = document.createElement('li');
        threadBlockedTimeMsLi.className = 'twelve-ul-style';
        threadBlockedTimeMsLi.innerHTML = list.threadBlockedTimeMs;
        addUl.appendChild(threadBlockedTimeMsLi);
        self.addtempLi(addUl);

        let threadBlockedNameLi = document.createElement('li');
        threadBlockedNameLi.className = 'twelve-ul-style';
        threadBlockedNameLi.innerHTML = list.threadBlockedName;
        addUl.appendChild(threadBlockedNameLi);
        self.addtempLi(addUl);

        let threadBlockedIdLi = document.createElement('li');
        threadBlockedIdLi.className = 'twelve-ul-style';
        threadBlockedIdLi.innerHTML = list.threadBlockedId;
        addUl.appendChild(threadBlockedIdLi);
        self.addtempLi(addUl);

        let threadWaitedCountLi = document.createElement('li');
        threadWaitedCountLi.className = 'twelve-ul-style';
        threadWaitedCountLi.innerHTML = list.threadWaitedCount;
        addUl.appendChild(threadWaitedCountLi);
        self.addtempLi(addUl);

        let threadWaitedTimeMsLi = document.createElement('li');
        threadWaitedTimeMsLi.className = 'twelve-ul-style';
        threadWaitedTimeMsLi.innerHTML = list.threadWaitedTimeMs;
        addUl.appendChild(threadWaitedTimeMsLi);
        self.addtempLi(addUl);

        let gmtModifyLi = document.createElement('li');
        gmtModifyLi.className = 'twelve-ul-style';
        gmtModifyLi.innerHTML = list.gmtModify;
        addUl.appendChild(gmtModifyLi);
        self.addtempLi(addUl);

        let threadStackLi = document.createElement('li');
        threadStackLi.className = 'twelve-ul-style';
        self.addViewBtn(threadStackLi, btnText);
        addUl.appendChild(threadStackLi);

        angular.element('#' + self.viewId.queryThreadView).append(addUl);
        self.SetNewClass(addUl.id, 'ul-div-style bottom-border-solid xs-font-size');
    }

    self.addViewBtn = function (superDom, btnText) {
        let btn = document.createElement('button');
        btn.className = 'login-btn deploy-info-btn popover-options submit-btn btn ng-binding small-service-btn';
        btn.type = 'submit';
        btn.innerHTML = btnText;
        superDom.appendChild(btn);
    }

    self.showQueryMqView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = true;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showQuerySlowSqlView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = true;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showQueryServiceView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = true;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showQueryRedisView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = true;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showQueryHttpView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = true;
        self.viewShow.queryTrackingView = false;
        self.viewShow.queryTrackingResultView = false;
    }
    self.showTraceView = function (title) {
        self.setDeployInfoTitle(title);
        self.viewShow.createViewShow = false;
        self.viewShow.mainViewShow = false;
        self.viewShow.selectBranchViewShow = false;
        self.viewShow.queryMemoryView = false;
        self.viewShow.queryThreadView = false;
        self.viewShow.queryMqView = false;
        self.viewShow.queryMqQueryResultView = false;
        self.viewShow.querySlowSqlView = false;
        self.viewShow.queryServiceView = false;
        self.viewShow.queryServiceLimitView = false;
        self.viewShow.queryServiceAuthView = false;
        self.viewShow.queryServiceAddView = false;
        self.viewShow.queryRedisView = false;
        self.viewShow.queryHttpView = false;
        self.viewShow.queryTrackingView = true;
        self.viewShow.queryTrackingResultView = false;
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
        self.SubmitNewDeployInfoText.lodingSpan = isSubmit;
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
        angular.element('#' + self.tips.id).attr('style', '');

        setTimeout(()=>{
            self.tips.post = '';
            angular.element('#' + self.tips.id).attr('style', 'display:none');
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
                }, function (err){
                    self.showTips('登录失败 ' + err);
                    self.loginUser.username = '';
                    self.loginUser.password = '';
                });
        }

        self.btnDisplay(btnId, false);
        self.SetNewClass(spanId, spanClass);
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
            }, function (err){
                self.showTips('新建部署信息异常 ' + err);
            });

        self.SubmitNewDeployInfoStyle(btnId, false);
        self.SetNewClass(spanId, spanClass);
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

        self.appMemoryInfoList = [
            {
                appIp: '127.0.0.1',
                cpuCount: '8',
                /**
                 * CPU使用率
                 */
                cpuLoadPercentage: '',
                /**
                 * 系统内存,单位kb
                 */
                sysMermoryKb: '',
                /**
                 * 系统可用内存,单位kb
                 */
                sysAvailMermoryKb: '',
                /**
                 * 系统已用内存,单位kb
                 */
                sysUseMermoryKb: '',
                /**
                 * 应用最大堆内存,单位kb
                 */
                appHeapMaxMermoryKb: '',
                /**
                 * 应用最小堆内存,单位kb
                 */
                appHeapMinMermoryKb: '',
                /**
                 * 应用占用堆内存,单位kb
                 */
                appHeapUseMermoryKb: '',
                /**
                 * 应用最大非堆内存,单位kb
                 */
                appNoHeapMaxMermoryKb: '',
                /**
                 * 应用最小非堆内存,单位kb
                 */
                appNoHeapMinMermoryKb: '',
                /**
                 * 应用占用非堆内存,单位kb
                 */
                appNoHeapUseMermoryKb: '',
                /**
                 * 采集时间
                 */
                gmtModify: ''
            }
        ]
    }

    /**
     * 展示服务器cpu使用率
     */
    self.initMemoryViewJs = function () {
        let ip1 = '127.0.0.1';
        let ip2 = '192.168.152.1';
        let ip3 = '127.0.0.1';
        let chart1 = Highcharts.chart('appCpuView', {
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
                    pointStart: Date.UTC(2020, 11, 28, 20, 45, 27),
                    pointInterval: 60 * 1000
                }
            },
            series: [{
                name: '127.0.0.1',
                data: [0.01, 0.01, 0.01, 0.01, 0.01, 0.01, 0.01]
            }, {
                name: '192.168.152.1',
                data: [0.34, 0.01, 0.01, 0.02, 0.02, 0.01, 0.02]
            }, {
                name: '127.0.0.2',
                data: [1.21, 0.34, 0.01, 0.04, 0.02, 0.01, 0.12]
            }],
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
        });

        let chart2 = Highcharts.chart('appMemoryView',{
            chart: {
                type: 'area'
            },
            title: {
                text: '服务器内存使用率'
            },
            subtitle: {
                text: '127.0.0.1'
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
                max: 17009004544/1024/1024,
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
                    pointStart: Date.UTC(2020, 11, 28, 20, 45, 27),
                    pointInterval: 60 * 1000
                }
            },

            series: [{
                name: '系统可用内存',
                data: [11178192896/1024/1024, 11137314816/1024/1024, 10267836416/1024/1024,
                    6142365696/1024/1024, 7768403968/1024/1024, 7729283072/1024/1024]
            }, {
                name: '应用占用内存',
                data: [136314880/1024/1024, 136314880/1024/1024, 136314880/1024/1024,
                    136314880/1024/1024, 136314880/1024/1024, 136314880/1024/1024,]
            }, {
                name: '应用占用内存',
                data: [136314880/1024/1024, 136314880/1024/1024, 136314880/1024/1024,
                    136314880/1024/1024, 136314880/1024/1024, 136314880/1024/1024,]
            }, {
                name: '最大堆内存',
                data: [4253024256/1024/1024, 4253024256/1024/1024, 4253024256/1024/1024,
                    4253024256/1024/1024, 4253024256/1024/1024, 4253024256/1024/1024,]
            }, {
                name: '堆使用内存',
                data: [65060656/1024/1024, 95232176/1024/1024, 104692528/1024/1024,
                    108886832/1024/1024, 114129712/1024/1024, 55257104/1024/1024,]
            }]
        });
    }

    /**
     * TODO TEST
     */
    self.init();
    // self.showDeployMainView('自动化部署');
    self.initMemoryViewJs();
}]);


