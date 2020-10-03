#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################targetStartJar.sh说明#######################
#需要先在目标服务器配置ssh环境变量: 取home目录/root/.bashrc 添加行 . /etc/profile
#通过startJar.sh,ssh到远程服务器执行该脚本
#启动Jar,输出日志到startLog文件,输出pid到pidLog文件
#使用方式 sh -x targetStartJar.sh ${jar路径} ${jar包名} ${jvm参数}
#例:
#$1 /cdadmin/jar/
#$2 test.jar
#$3 -javaagent:common-agent-jar-1.0-SNAPSHOT.jar=DEBUG

#######################入参#######################
#jar路径
if [[ -n "${1}" ]]; then readonly paramJarPath=${1}; fi
#jar包名
if [[ -n "${2}" ]]; then readonly paramJarFileName=${2}; fi
#jvm参数
if [[ -n "${3}" ]]; then readonly paramJvm=${3}; fi
readonly startLog='startLog'
readonly pidLog='pidLog'

#######################停止java进程#######################
(ps -ef|grep java|awk -F' ' '{print $2}'|while read -r p; do kill -9 $p; done)

#######################启动jar#######################
#后台启动
(nohup java -jar $paramJarPath$paramJarFileName $paramJvm > $paramJarPath$startLog 2>&1 &)
#将启动jar进程号写入pidLog文件
(ps -ef|grep java|awk -F' ' '{print $2}'|while read -r p; do echo $p > $paramJarPath$pidLog; break; done)