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
i=1
#参数3 jvm参数
paramJvm=''
for arg in "$@"; do
    if [[ i -eq 1 ]]; then
      #参数1 jar路径
      paramJarPath=${arg}
        elif [[ i -eq 2 ]]; then
          ##参数2 jar包名
          paramJarFileName=${arg}'/'
            else
              paramJvm=$paramJvm${arg}' '
    fi
    ((i=++i))
done

readonly START_APP='com.evy.linlin.start.EvyStartApp'
readonly startLog='startLog'
readonly pidLog='pidLog'

#######################停止java进程#######################
#第一次先执行kill等待程序停止
(ps -ef|grep java|awk -F' ' '{print $2}'|while read -r p; do kill $p; done || true)
sleep 6s
#若还是存在进程,则直接强行停止
(ps -ef|grep java|awk -F' ' '{print $2}'|while read -r p; do kill -9 $p; done || true)

#######################启动jar#######################
#后台启动 jar包方式启动
#(nohup java $paramJvm -jar $paramJarPath$paramJarFileName > $paramJarPath$startLog 2>&1 &)
#后台启动 类名启动
(nohup java $paramJvm $START_APP > $paramJarPath$startLog 2>&1 &)

#将启动jar进程号写入pidLog文件
(ps -ef|grep java|awk -F' ' '{print $2}'|while read -r p; do echo $p > $paramJarPath$pidLog; break; done)