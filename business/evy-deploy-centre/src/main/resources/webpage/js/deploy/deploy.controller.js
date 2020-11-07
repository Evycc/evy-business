'use strict';
app.controller('DeployMainController', ['$scope', 'DeployMainService', '$compile', function ($scope, DeployMainService, $compile) {
    let self = this;


    /********定义用户模型********/
    /**
     * 定义登陆表单模型
     */
    self.loginUser = {
        username : '',
        password : ''
    }
    self.cur = {
        /**
         * 登陆后用户名
         */
        user : '尚未登陆',
        /**
         * 当前分支
         */
        branch : 'master'
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
        title : '部署应用配置'
    }

    /********监听用户事件函数********/

    /**
     * 监听登陆表单输入框变化,存在值则变更表单样式
     */
    $scope.$watch('main.loginUser.username', function (newValue) {
        let inputDivId = 'form-cell-1';
        let inputId = 'form-cell-input-1';
        if (newValue == null || angular.equals('', newValue)){
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
        if (newValue == null || angular.equals('', newValue)){
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

    /********定义修改样式函数********/
    function showInputSelectStyle(inputDivId, inputId) {
        let style = 'background :#e8f0fe;';
        let div1 = 'form-cell-1';
        let div2 = 'form-cell-2';

        if (angular.equals(div1, inputDivId)) {
            angular.element('#' + inputDivId).attr('style',style + 'border-bottom-left-radius: 0;border-bottom-right-radius: 0;');
        } else {
            angular.element('#' + inputDivId).attr('style',style + 'border-top-left-radius: 0;border-top-right-radius: 0;');
        }

        angular.element('#' + inputId).attr('style','background :#e8f0fe;border:none;outline:medium;');
    }
    function hideInputSelectStyle(inputDivId, inputId) {
        let style = 'background :#fff;';
        let div1 = 'form-cell-1';
        let div2 = 'form-cell-2';

        if (angular.equals(div1, inputDivId)) {
            angular.element('#' + inputDivId).attr('style',style + 'border-bottom-left-radius: 0;border-bottom-right-radius: 0;');
        } else {
            angular.element('#' + inputDivId).attr('style',style + 'border-top-left-radius: 0;border-top-right-radius: 0;');
        }

        angular.element('#' + inputId).attr('style','background :#fff;border:none;outline:medium;');
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
}]);