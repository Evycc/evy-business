#!/bin/bash
set -o nounset    #遇到不存在的变量，终止
#set -o errexit    #遇到错误，终止
#修饰变量    readonly:只读    local:函数内变量
#使用$()代替`反单引号，不需要转义
#使用[[]]代替[]，避免转义
#-n对语法进行检查    -v | -x根据脚本每个命令执行

#######################buildJar.sh说明#######################
#需先执行gitBuild.sh
#通过maven编译指定目录为jar包,默认工程为SpringBoot工程
#使用方式 sh -x buildJar.sh ${项目名称} ${应用名称} ${是否执行Junit 0:执行 1:不执行} ${tag 建议传入时间戳}
#返回: 返回编译后的jar路径,如 {"errorCode":"0","msg":"/cdadmin/gitProject/history/test-demo/2020-08-25"}
#例: sh -vx buildJar.sh evy-business test-demo 1 2020-08-07

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

#获取项目父模块目录,用于mvn编译
#$1 查找的根目录
#$2 ${是否执行Junit 0:执行 1:不执行}
function buildParetnModule(){
  tempLen=0
  tempFileName=""
  if [[ -n "${1}" ]]
    then
      #遍历目录
      for file in $(find "${1}" -type f -name pom.xml)
        do
          if [[ $tempLen -eq 0 || $tempLen -gt ${#file} ]]; then tempLen=${#file}; tempFileName=${file}
          fi
      done
  fi
  if [[ -n $tempFileName ]];
  then
    #截取"/pom.xml",只保留目录绝对路径
    endIndex=${#tempFileName}-8
    tempFileName=${tempFileName:0:$endIndex}

    if [[ -n "$2" && "$2" -eq "1" ]]; then mvn="mvn clean install -U -Dmaven.test.skip=true"
      else mvn="mvn clean install -U"
    fi
    cd "$tempFileName" && $mvn
  fi
  exit 0
}

#######################全局变量#######################
#git clone根目录
readonly projectRootDir='/cdadmin/gitProject/'
#编译历史目录
readonly projectHistoryDir='/cdadmin/gitProject/history/'

#######################获取目标项目路径#######################
#项目名称
if [[ -n "${1}" ]]; then readonly paramProjectName=${1}; fi
#应用名称
if [[ -n "${2}" ]]; then readonly paramAppName=${2}; fi
paramIfJunit='1'
#是否执行Junit
if [[ -n ${3} && ${3} -eq '0' ]]; then paramIfJunit='0'; fi
#应用名称
if [[ -n "${4}" ]]; then readonly paramTag=${4}; fi
#目标项目路径 /cdadmin/gitProject/{项目名称}/{应用名称}
# $projectRootDir$paramProjectName'/**/'$paramAppName
readonly dirPath=$(find "$projectRootDir$paramProjectName"  -type d -name "$paramAppName")
readonly sucess='0'
readonly failed='1'
readonly mvnBuildError='BUILD FAILURE'
readonly noFoundDirError='err'

cd $dirPath || echoReturnMsg $failed '目标路径不存在{'"$dirPath"'}'

#######################编译打包工程#######################
#编译父模块
buildParetnModule "$projectRootDir$paramProjectName" "$paramIfJunit"

mvnBuildCmd='mvn clean install -U'
if [[ $paramIfJunit -eq '1' ]]; then mvnBuildCmd='mvn clean install -U -Dmaven.test.skip=true'; fi

#创建指定工程下以参数tag命名的目录
targetDir=$projectHistoryDir$paramAppName'/'$paramTag
mkdir -p "$targetDir" && rm -rf "$targetDir'/*'"

#将mvn日志放置到指定工程下以参数tag命名的目录
cd $dirPath && $mvnBuildCmd > "$targetDir"'/mvnLog'

if [[ -n "$(cd $dirPath && grep "$mvnBuildError" "$targetDir"'/mvnLog')" ]]
then
  echoReturnMsg $failed '编译失败'
fi
readonly jarPath="$dirPath/target/*.jar"

#######################将编译后的jar放置到指定工程下以参数tag命名的目录,以便后续进行回滚#######################
cp $jarPath $targetDir
if [[ ! "$(ls -l $targetDir)" =~ 'jar' ]]
then
    echoReturnMsg $failed '不存在编译jar包'
else
    echoReturnMsg $sucess "$targetDir"
fi