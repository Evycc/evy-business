#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################startJar.sh说明#######################
#需先执行buildJar.sh
#通过sftp上传编译好的jar包到指定服务器目录->在指定服务器启动jar包,获取PID,获取执行日志
#使用方式 sh -x startJar.sh ${目标服务器ip} ${jar包路径} ${jvm参数}
#返回远程服务器启动pid 如: {"errorCode":"0","msg":"5464"}
#例: sh -x startJar.sh 192.168.152.128 /cdadmin/gitProject/history/test-demo/2020-08-30 -Xms=512m
#$1 127.0.0.1
#$2 /cdadmin/gitProject/history/test-demo/2020-08-25
#$3 -javaagent:common-agent-jar-1.0-SNAPSHOT.jar=DEBUG

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
#目标服务器IP
if [[ -n "${1}" ]]; then readonly paramTargetIp=${1}; fi
#jar包路径
if [[ -n "${2}" ]]; then readonly paramJarPath=${2}'/'; fi
#jvm参数
if [[ -n "${3}" ]]; then readonly paramJvm=${3}; fi
readonly targetPath='/cdadmin/jar/'
readonly shPath='/cdadmin/'
readonly shFileName='targetStartJar.sh'
readonly startLog='startLog'
readonly pidLog='pidLog'
readonly jarPath=$(ls $paramJarPath*jar*)
readonly jarFileName=$(echo $jarPath|awk -F'/' '{print $NF}')

#######################从远程服务器创建目录#######################
#ssh cdadmin@"$paramTargetIp" "mkdir -r $targetPath"
ssh root@"$paramTargetIp" "mkdir -p $targetPath"

#######################上传targetStartJar.sh到目标服务器#######################
#scp $shPath$shFileName cdadmin@"$paramTargetIp":$targetPath
scp $shPath$shFileName root@"$paramTargetIp":$targetPath

#######################上传jar到目标服务器#######################
#scp "$jarPath" cdadmin@"$paramTargetIp":$targetPath
scp "$jarPath" root@"$paramTargetIp":$targetPath

#######################从目标服务器启动jar#######################
#ssh cdadmin@"$paramTargetIp" "chmod 775 $targetPath$shFileName; sh $targetPath$shFileName $targetPath $jarFileName $paramJvm"
ssh root@"$paramTargetIp" "chmod 775 $targetPath$shFileName; chmod 775 $targetPath$jarFileName; sh -vx $targetPath$shFileName $targetPath $jarFileName $paramJvm"

#######################从目标服务器传回jar启动日志及pid#######################
#scp cdadmin@"$paramTargetIp":$targetPath$startLog $paramJarPath
#scp cdadmin@"$paramTargetIp":$targetPath$pidLog $paramJarPath
scp root@"$paramTargetIp":$targetPath$startLog $paramJarPath
scp root@"$paramTargetIp":$targetPath$pidLog $paramJarPath

#######################返回pid#######################
_exec1=$(cat "$paramJarPath$pidLog")
echoReturnMsg 0 "$_exec1"