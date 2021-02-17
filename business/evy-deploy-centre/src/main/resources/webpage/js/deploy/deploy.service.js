'use strict';
app.factory('DeployMainService', ['$http', '$q', function ($http, $q) {
    const GATEWAY_PATH = 'http://localhost:15464/EvyGateway';
    const REQ_SRVCODE_MAP = {
        login : ''
    }

    let factory = {
        login : login
    };
    return factory;

    /**
     * 登录或创建用户
     * @param username 用户名
     * @param password 密码
     * @return 返回userSeq
     */
    function login(username, password) {
        let deferred = $q.defer();	//生成异步对象
        $http.post(REQ_SRVCODE_MAP.login)
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
}]);