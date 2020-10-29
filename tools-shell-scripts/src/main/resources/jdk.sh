#!/bin/bash
#offline jdk install-->请先离线安装jdk, 并将该脚本上传至jdk.tar.gz的目录下, 修改执行权限
ipath="/opt/zy/jdk"
installpath=$(cd `dirname $0`; pwd)
j=`whereis java`
java=$(echo ${j} | grep "jdk")
if [[ "$java" != "" ]]
then
    echo "java was installed!"
else
    echo "java not installed!"
    echo;
    echo;
    echo "解压 jdk-*-linux-x64.tar.gz"
    tar -zxvf jdk-*-linux-x64.tar.gz >/dev/null 2>&1
    echo;
    echo;
    cd jdk* && jdkname=`pwd | awk -F '/' '{print $NF}'`
    echo "获取jdk版本: ${jdkname}"
    echo;
    echo;
    cd ${installpath}
    echo "获取当前目录:${installpath}"
    echo;
    echo;
    mv ${jdkname} ${ipath}
    echo "转移${jdkname}文件到${ipath}安装目录"
    echo "jdk安装目录:${ipath}/${jdkname}"
    echo;
    echo;
    echo "#java jdk" >> /etc/profile
    echo "export JAVA_HOME=${ipath}/${jdkname}" >> /etc/profile
    echo 'export JRE_HOME=${JAVA_HOME}/jre' >> /etc/profile
    echo 'export CLASSPATH=.:${JAVA_HOME}/lib:${JRE_HOME}/lib' >> /etc/profile
    echo 'export PATH=${JAVA_HOME}/bin:$PATH' >> /etc/profile
    source /etc/profile > /dev/null 2>&1
    echo "jdk 安装完毕!"
    echo;
    echo;
    echo "请执行以下命令以使jdk环境生效"
    echo;
    echo;
    echo "source /etc/profile"
    echo;
    echo;
fi