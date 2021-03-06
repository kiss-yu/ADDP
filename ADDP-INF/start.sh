#!/bin/sh
# 应用名
APP=$1
# 环境
ENV=$2
# 应用端口
port=$3
# 应用端口设置端口
inPort=$4
logPath=$5
echo "logPath=${logPath}"
# 启动应用镜像
#container="$(docker ps -a |grep ${APP}-${ENV})"
#if [ -z ${container} ]
#    then
#        echo "build container"
#
#     else
#        docker start ${APP}-${ENV}
#fi

docker stop ${APP}-${ENV}
docker rm ${APP}-${ENV}
if [ "$ENV" = "bak" ]
    then
        docker run  --name ${APP}-${ENV}  -p ${port}:${inPort} -v /tmp:/tmp -v ${logPath}:${logPath}  -dit ${APP}:pro
else
    docker run  --name ${APP}-${ENV}  -p ${port}:${inPort} -v /tmp:/tmp -v ${logPath}:${logPath}  -dit ${APP}:${ENV}
fi


