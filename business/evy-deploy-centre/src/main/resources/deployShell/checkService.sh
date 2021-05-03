#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################checkService.sh说明#######################
#需要先在目标服务器配置ssh环境变量: 取home目录/root/.bashrc 添加行 . /etc/profile
#通过ssh到目标服务器,检测对应PID进程是否存在,存在则通过PID获取监听端口,curl host:port,检查应用是否启动成功
#使用方式 sh -x checkService.sh ${目标服务器ip} ${目标pid}
#例:
#$1 192.168.152.135
#$2 1870
#sh -x checkService.sh 192.168.152.135 1870
#返回: {"errorCode":"0","msg":"0"}

#######################入参#######################
#目标服务器ip
if [[ -n "${1}" ]]; then readonly targetHost=${1}; fi
#目标pid
if [[ -n "${2}" ]]; then readonly targetPid=${2}; fi
#错误码
readonly SUCCESS='0'
readonly FAILED='1'
readonly NO_PID_ERROR_MSG='不存在的PID'
readonly NO_PORT_MSG='找不到PID对应进程端口'
readonly NO_SERVICE_MSG='服务启动失败'
#目标服务器ssh用户
readonly serverUser='cdadmin'

#######################函数定义#######################

#检查目标服务器是否存在对应PID进程
#返回值 0存在PID 1不存在PID
function sshCheckPid() {
    pid=$(ssh $serverUser@"$targetHost" "pgrep java|grep $targetPid");
    if [[ -n "$pid" ]]; then
        echo 0;
        return 0;
    fi
    echo 1;
    return 1;
}

#根据PID搜索目标端口
#返回值 不存在端口返回空
function sshSearchPort() {
    port=$(ssh $serverUser@"$targetHost" "netstat -nap | grep $targetPid|grep LISTEN|awk -F' ' '{print \$4}'|awk -F':' '{print \$4}'");
    if [[ -n "$port" ]]; then
      echo "$port";
      return 0;
    fi
    echo "";
    return 1;
}

#curl目标服务器及端口,返回存在status表示服务器是启动成功的
#入参 {1:端口}
#返回值 0:服务器启动成功 1:服务器启动失败
function sshCheckService() {
    result=$(ssh $serverUser@"$targetHost" "curl $targetHost':'$1");
    if [[ $result =~ 'status' ]]; then
        echo 0;
        return 0;
    fi
    echo 1;
    return 0;
}

#$1 errorCode 0成功 1失败
#$2 返回信息,用于应用获取
function echoReturnMsg(){
  if [[ -n "${1}" && -n "${2}" ]]
    then
      echo '{"errorCode":'\""${1}"\"',"msg":'\""${2}"\"'}'
      exit "${1}"
  fi
}

function main() {
    checkPid=$(sshCheckPid);
    if [[ "$checkPid" -eq "$SUCCESS" ]]; then
        port=$(sshSearchPort);

        if [[ -n "$port" ]]; then
            result=$(sshCheckService "$port");

            if [[ "$result" -eq "$SUCCESS" ]]; then
                echoReturnMsg $SUCCESS "$result";
                else echoReturnMsg $FAILED "$NO_SERVICE_MSG";
            fi
            else echoReturnMsg $FAILED "$NO_PORT_MSG";
        fi
        else echoReturnMsg $FAILED "$NO_PID_ERROR_MSG";
    fi
    return 0;
}

#######################shell main#######################
main