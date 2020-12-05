'use strict';
app.controller('DeployMainController', ['$scope', 'DeployMainService', '$compile', function ($scope, DeployMainService, $compile) {
    let self = this;


    /********定义用户模型********/
    /**
     * 定义登陆表单模型
     */
    self.loginUser = {
        username: '',
        password: ''
    }
    self.cur = {
        /**
         * 登陆后用户名
         */
        user: '尚未登陆',
        /**
         * 当前分支
         */
        branch: 'master',
        deploySeq : ''
    }
    /**
     * 定义展示页面ID
     */
    self.viewId = {
        mainView : 'deploy-main-view',
        createView : 'deploy-stepEl',
        selectBranchView : 'deploy-select-branch'
    }
    /**
     * 定义展示页面ID
     */
    self.viewShow = {
        mainViewShow : true,
        createViewShow : false,
        selectBranchViewShow : false
    }
    /**
     * 是否登陆
     * @type {boolean}
     */
    self.isLogin = true;
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
        branchName: '',
        targetHost: '',
        switchJunit: 1,
        switchBatchDeploy: 1,
        jvmParam: ''
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
     * 分支列表结果list
     *
     */
    self.deployViewReqList = [
        // {
        //     branchName : 'master'
        // }
    ]
    /**
     * 可切换分支集合
     */
    self.checkBranchList = [
        // {
        //     appName : '',
        //     gitPath : '',
        //     branch : '',
        //     /**
        //      * 配置唯一序列
        //      */
        //     deploySeq : '',
        //     /**
        //      * 缩略展示ip
        //      */
        //     serverIp : ''
        // }
    ]
    /**
     * 回滚部署记录列表
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

    }
    self.showDeployMainView = function () {

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

    self.GetGitBeanchListBtnText = {
        isSubmit: false,
        noSubmitText: '获取分支列表 ',
        SubmitText: '稍等... '
    }
    /**
     * GetGitBeanchList 提交时按钮样式
     * @param isSubmit true 禁用按钮 false 启用按钮
     */
    self.GetGitBeanchListStyle = function (btnId, isSubmit) {
        self.btnDisplay(btnId, isSubmit);
        self.GetGitBeanchListBtnText.isSubmit = isSubmit;
    }

    self.SubmitNewDeployInfoText = {
        isSubmit: false,
        noSubmitText: '提交 ',
        SubmitText: '稍等... '
    }
    /**
     * SubmitNewDeployInfo 提交时按钮样式
     * @param isSubmit true 禁用按钮 false 启用按钮
     */
    self.SubmitNewDeployInfoStyle = function (btnId, isSubmit) {
        if (isSubmit) {
            self.btnDisplay(btnId, true);
            self.SubmitNewDeployInfoText.isSubmit = true;
        } else {
            self.btnDisplay(btnId, false);
            self.SubmitNewDeployInfoText.isSubmit = false;
        }
    }

    self.BtnLodingStyle = function (objId) {
        let lodingClass = 'icon-spinner icon-spin';
        angular.element('#' + objId).attr('class','');
        angular.element('#' + objId).addClass(lodingClass);
    }

    self.SetNewClass = function (objId, className) {
        angular.element('#' + objId).attr('class','');
        angular.element('#' + objId).addClass(className);
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
        //TODO 登陆service
        self.btnDisplay(btnId, false);
        self.SetNewClass(spanId, spanClass);
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
        //获取Junit选项
        if (angular.element('#JunitCheckBox').prop('checked')) {
            self.deployForm.switchJunit = 0;
        }
        //获取分批部署选项
        if (angular.element('#BatchDeployCheckBox').prop('checked')) {
            self.deployForm.switchBatchDeploy = 0;
        }

        self.SubmitNewDeployInfoStyle(btnId, false);
        self.SetNewClass(spanId, spanClass);
    }

    /**
     * 获取git分支列表
     */
    self.GetGitBeanchList = function (event) {
        let btnId = event.currentTarget.attributes.item(0).nodeValue;
        let spanId = event.target.children.item(0).attributes.item(0).nodeValue;
        let spanClass = event.target.children.item(0).attributes.item(1).nodeValue;

        self.GetGitBeanchListStyle(btnId, true);
        self.BtnLodingStyle(spanId);
        //TODO
        self.GetGitBeanchListStyle(btnId, false);
        self.SetNewClass(spanId, spanClass);
    }

    /**
     * 切换分支
     * @param deploySeq 部署序列号,根据序列号获取相应分支配置
     */
    self.selectBranchSubmit = function (deploySeq) {
        //TODO
    }

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

    self.init();
}]);