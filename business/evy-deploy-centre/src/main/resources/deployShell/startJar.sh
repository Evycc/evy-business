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
#例: sh -x startJar.sh 192.168.152.128 /cdadmin/gitProject/history/test-demo/2020-08-30 -Xms512m
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
i=1
#参数3 jvm参数
paramJvm=''
for arg in "$@"; do
    if [[ i -eq 1 ]]; then
      #参数1 目标服务器IP
      paramTargetIp=${arg}
        elif [[ i -eq 2 ]]; then
          ##参数2 jar包路径
          paramJarPath=${arg}'/'
            else
              paramJvm=$paramJvm${arg}' '
    fi
    ((i=++i))
done
paramJvm=$paramJvm' -DAppLocalhost='$paramTargetIp' '

readonly serverUser='cdadmin'
readonly targetPath='/cdadmin/jar/'
readonly shPath='/cdadmin/'
readonly shFileName='targetStartJar.sh'
readonly startLog='startLog'
readonly pidLog='pidLog'
readonly jarPath=$(ls $paramJarPath*jar*)
readonly jarFileName=$(echo $jarPath|awk -F'/' '{print $NF}')
readonly targetClassPath1='/cdadmin/jar/localClass/classes/'
readonly targetClassPath2='/cdadmin/jar/localClass/lib/'
classpathParam='/cdadmin/jar/localClass/classes'
readonly DEFAULT_JVM_PARAM=' -XX:TieredStopAtLevel=1 -noverify '
#jvm参数 agent jar配置
readonly AGENT_JVM_PARAM='-javaagent:/cdadmin/jar/common-agent-jar-1.0-SNAPSHOT.jar'
readonly AGENT_JVM_PARAM_DEBUG='-javaagent:/cdadmin/jar/common-agent-jar-1.0-SNAPSHOT.jar=DEBUGSLOW_SQL=2000'
readonly AGENT_LOCAL_PATH='/cdadmin/common-agent-jar-1.0-SNAPSHOT.jar'
#jvm参数 dump文件保存地址,必须事先创建
readonly DUMP_LOG_DIR='/applog/current/dump/'
readonly DUMP_JVM_PARAM='-XX:HeapDumpPath='$DUMP_LOG_DIR

#######################将jar包解压到本地,拼接classpath#######################
cd $paramJarPath && jar xf $jarPath

for fileName in $(find $paramJarPath'BOOT-INF/lib/' -maxdepth 1 -mindepth 1 -type f -printf '%f\n')
  do
    classpathParam=$classpathParam':'$targetClassPath2$fileName
done

classpathParam=' -classpath '$classpathParam

#######################停止java进程#######################
#ssh $serverUser@"$paramTargetIp" "ps -ef|grep java|awk -F' ' '{print \$2}'|while read -r p; do kill -9 \$p; done || true"

#######################从远程服务器创建目录#######################
ssh $serverUser@"$paramTargetIp" "mkdir -p $DUMP_LOG_DIR"
ssh $serverUser@"$paramTargetIp" "mkdir -p $targetPath && rm -rf $targetPath/*"

#######################上传classpath文件到目标服务器############
ssh $serverUser@"$paramTargetIp" "mkdir -p $targetClassPath1 && rm -rf $targetClassPath1"
ssh $serverUser@"$paramTargetIp" "mkdir -p $targetClassPath2 && rm -rf $targetClassPath2"

scp -r $paramJarPath'BOOT-INF/classes' $serverUser@"$paramTargetIp":$targetClassPath1
scp -r $paramJarPath'BOOT-INF/lib' $serverUser@"$paramTargetIp":$targetClassPath2

#######################上传targetStartJar.sh到目标服务器#######################
scp $shPath$shFileName $serverUser@"$paramTargetIp":$targetPath

#######################上传jar到目标服务器#######################
scp "$jarPath" $serverUser@"$paramTargetIp":$targetPath

#######################上传agent jar到目标服务器#######################
if [[ -f $AGENT_LOCAL_PATH ]]; then
  scp "$AGENT_LOCAL_PATH" $serverUser@"$paramTargetIp":$targetPath
  paramJvm=$paramJvm' '$AGENT_JVM_PARAM_DEBUG
fi

#######################从目标服务器启动jar#######################
ssh $serverUser@"$paramTargetIp" "chmod 775 $targetPath$shFileName; chmod 775 $targetPath$jarFileName; sh -vx $targetPath$shFileName $targetPath $jarFileName $DUMP_JVM_PARAM$DEFAULT_JVM_PARAM$paramJvm $classpathParam"

#######################从目标服务器传回jar启动日志及pid#######################
sleep 3s
scp $serverUser@"$paramTargetIp":$targetPath$startLog $paramJarPath
scp $serverUser@"$paramTargetIp":$targetPath$pidLog $paramJarPath

#######################返回pid#######################
_exec1=$(cat "$paramJarPath$pidLog")
echoReturnMsg 0 "$_exec1"