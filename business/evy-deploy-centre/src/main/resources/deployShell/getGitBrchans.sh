#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################getGitBrchans.sh说明#######################
#clone git路径到临时目录，并获取远程分支列表
#使用方式 sh -x buildJar.sh ${git路径} ${项目名称}
#返回: 返回编译后的jar路径,如 {"errorCode":"0","msg":"/cdadmin/gitProject/history/test-demo/2020-08-25"}
#例: sh -vx getGitBrchans.sh git@github.com:Evycc/springcloud-demo.git springcloud-demo

#######################函数声明#######################
#$1 errorCode 0成功 1失败
#$2 返回信息,用于应用获取
function echoReturnMsg(){
  if [[ -n "${1}" && -n "${2}" ]]
    then
      echo '{"errorCode":'\""${1}"\"',"msg":'\""${2}"\"'}'
      exit "${1}"
  fi
}

#######################全局变量#######################
#git clone根目录
readonly projectTmpDir='/cdadmin/tmp/'
readonly gitErrorCode='fatal'

#######################获取sh参数#######################
#git路径
if [[ -n "${1}" ]]; then readonly paramGitPath=${1}; fi
#项目名称
if [[ -n "${2}" ]]; then readonly paramProjectName=${2}; fi

#######################clone对应项目到本地tmp目录#######################
_exce1=$(git clone $paramGitPath $projectTmpDir$paramProjectName)

#git clone 存在fatal提示,则返回失败
if [[ $_exce1 =~ $gitErrorCode ]]
  then echoReturnMsg 1 "$_exce1"
fi

if cd "$projectTmpDir$paramProjectName";
  then
    echoReturnMsg 0 "$(cd "$projectTmpDir$paramProjectName" && git branch -r)";
    rm -rf "$projectTmpDir$paramProjectName"
  else echoReturnMsg 1 "clone目录不存在";
fi