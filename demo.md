### 基本功能：
1. 创建应用：

    应用创建分私人项目和组织项目，私人项目不需要审批，组织项目创建时应用创建需要应用管理员审批，组织创建需要超级管理员审批。

    - 应用的操作有：

        1. 概述：

            基础信息：基础信息，应用owner，appsOps，测试人员，代码仓库地址，日常、预发、线上域名配置
        2. 变更：

            创建变更：变更开发人员，测试人员，代码审核，关闭变更，操作记录
        3. 发布：

            日常，预发，线上，灰度，项目···，可以配置

            线上发布步骤，代码审核-构建-提交发布单-审批-部署线上发布分支（${count}-master，count递增）-写基线（master分支marge p-master，部署成功后才将代码写入master是为了保证master分支的代码一定正确，因为前面部署时可能失败，失败的代码在${count}-master分支不影响主干代码）

            
        4. 主干配置项：

            固定项目根目录下的application.properties的配置，优先已这里的配置为准。
        5. 环境：

            - 查看日常，预发，线上等环境的资源管理

            - 查看日常，预发，线上等环境的部署历史

            - 查看日常，预发，线上等环境的部署策略（分批发布）
2. 创建需求，缺陷等工作项

    - 需求
    - 缺陷

        
### 实现猜想：
1. 怎么在docker container中部署git分之的代码,并缺看到部署全过程日志：

    ADDP实现一个远程ssh客户端，直接ssh连上container执行ssh命令，并且将ssh回传的记录保存到数据库中（用数据库追加的功能，一个应用统一环境只会存在一条记录，后面的部署直接覆盖前面的数据）并且通过websocket方式事实发送到ADDP前端界面显示（websocket通过应用+环境区分连接）。

2. ssh连接的container怎么能写入ADDP配置的application.properties内容？

    1. 自定义docker基础镜像，在clone代码后执行一个特定的脚本拉取ADDP配置内容并写入源码中。然后在编译部署
    2. 在ADDP上实现代码clone后编译成jar、war包放到OSS（自己写一个简单的文件存储服务器），然后ssh container wget这个jar、war包后部署
3. 怎么实现多变更（分支）同时部署在一个环境中？

    1. 第一次单分支部署时先创建一个副本分支（bak-${name}-master），副本分支是从master创建，然后副本分支marge部署分支（如果冲突的ADDP抛出构建异常，冲突解决的的信息暂时不提供）。单分支后面部署时都会是副本分支marge当前分支后编译部署。

    2. 多分支部署时会创建一个随机副本分支，副本一样从master创建，然后marge部署的多个分支代码。随机副本分支的清楚策略（30天后判断副本分支是否是部署状态，不是的话清楚分支）
4. 怎么实现应用资源管理时每个资源都有自己的ip，应用分环境都有自己的域名？

    关于域名，在本机测试时把所有域名绑定到localhsot

    怎么实现域名到ip：网关（nginx）
    
    怎么实现docker多个ip：

        本机访问应用时输入域名，dns解析到ip是127.0.0.1，访问到127.0.0.1时网关通过域名代理到127.0.0.1的不同端口。比如应用a在docker的端口绑定是-p 10000:22 10001:80，这是网关会将访问请求代理到127.0.0.1:10001。

5. 在本机开发时怎么实现多台服务器来部署应用：

    1. docker多container实现，然后将container的ssh端口，80端口映射到本级10000端口向上递增，提供mapping表。container的区分（${project-name}-${ip}），部署时直接ssh本机不同的端口连接不同容器实现多服务器部署。

6. container怎么实现自动启动应用并且ssh能得到启动日志，还能够知道tomcat，springboot启动完成？

    自动启动应用需要自己提供一个docker基础镜像，里面集成了应用启动脚本，应用启动时后台执行的，在后台时将输出日志写到/var/tmp/enevt.log文件里，然后执行tail -100f /var/tmp/enevt.log实时得到部署日志，当得到tomcat，springboot启动完成的信号后ctrl+c关闭tail命令，然后执行自检脚本（自检先wget 127.0.0.1，如果成功就成功，失败后自检wget 127.0.0.1/checkhtml，ADDP ssh启动自检脚本后检查输出内容，如果fail就弹出部署失败。）