#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################gitBuild.sh说明#######################
#从git复制项目到本地或对已用项目进行更新
#ssh方式拉取代码 : 需要对方在git配置ssh密钥,密钥路径:/用户名/.ssh/id_rsa.pub
#使用方式 sh -x gitBuild.sh ${git项目名称} ${git地址} ${git分支}
#返回: errorCode返回0,表示更新项目成功
#例: sh -x gitBuild.sh evy-business https://github.com/Evycc/evy-business.git
#返回: {"errorCode":"0","msg":"已经是最新的。"}

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

#######################clone git 项目并切换分支到本地,已存在则进行pull操作#######################
#git clone根目录
readonly projectRootDir='/cdadmin/gitProject/'
#分支名
paramGitBranch=''
#项目名
if [[ -n "${1}" ]]; then readonly paramProjectName=${1}; fi
#git地址
if [[ -n "${2}" ]]; then readonly paramGitPath=${2}; fi
if [[ "$#" -eq 3 && -n "${3}" ]]; then paramGitBranch=${3}; fi
#错误码
readonly execErr='fatal'
readonly warnErr='warning'
readonly buildErrorMsg='构建应用异常'
readonly sucess='0'
readonly failed='1'

#根目录不存在,则创建
if [[ ! -d "$projectRootDir" ]]
  then
    (mkdir -p $projectRootDir)
fi

#项目不存在根目录中,则创建并clone
readonly dirPath=$projectRootDir$paramProjectName
if [[ ! -d "$dirPath" ]]
then
  #不存在git目录,创建目录并进行clone操作
  _exec1="$(mkdir -p "$dirPath")"
  _exec2="$(cd "$projectRootDir" && git init && git clone "$paramGitPath" && cd "$paramProjectName" && git pull origin)"

  #存在异常,则输出错误码 GIT_BUILD_ERROR
  if [[ -n $_exec1 ]]; then (echoReturnMsg "$failed" "$_exec1"); fi
  if [[ $_exec2 =~ $execErr || $_exec2 =~ $warnErr ]]; then (echoReturnMsg "$failed" "$_exec2"); fi
fi

#未输入分支则默认为master分支,无需切换
if [[ -n $paramGitBranch ]];
  then _exec3="$(cd "$dirPath" && git checkout "$paramGitBranch")"
    #存在异常,则输出错误码 GIT_BUILD_ERROR
  if [[ $_exec3 =~ $execErr || $_exec3 =~ $warnErr ]]; then (echoReturnMsg "$failed" "$_exec3"); fi
fi

#已存在git目录,进行pull操作
_exec4="$(cd "$dirPath" && git pull)"
if [[ $_exec4 =~ $execErr || $_exec4 =~ $warnErr ]]
  then (echoReturnMsg "$failed" "$_exec4")
  else
    if [[ -z  "$_exec4" ]]
      then
        rm -rf "$dirPath"
        echoReturnMsg "$failed" "$_exec4"
      else
        (echoReturnMsg "$sucess" "$_exec4")
    fi
fi