# 悟空无代码平台


体验地址：[https://www.72crm.com](http://www.72crm.com)

## 悟空无代码平台介绍


悟空无代码平台正式开源，通过悟空无代码平台开发工具，企业可自主地快速开发出适合企业需要的信息化系统，开发过程只需要业务人员参与，开发效率极高，维护性很强。

开发人员可以通过无码的形式，在可视化设计器中，用鼠标拖拽和点选的开发方式，快速搭建企业应用软件。开发人员利用开发平台，可以跳过基础架构，直接进入与业务需求相关的工作。

悟空无代码平台与传统的通过代码的开发方式相比，这种面向业务的开发方式，开发迅速，调整极快，能够快速适应企业需求，也能够快速应对企业变化。


官网地址：[http://www.5kcrm.com](http://www.5kcrm.com/)


QQ群交流群群：[259359511](https://qm.qq.com/cgi-bin/qm/qr?k=G9T2audQqbZBR_HG0aTP9E-8fE7oMeb8&jump_from=webapi)


扫码添加小悟官方客服微信，邀您加入千人微信交流群：

<img src="https://images.gitee.com/uploads/images/2019/1231/115927_f9c580c8_345098.png" width="120">

关注悟空CRM公众号，了解更多悟空资讯

<img src="https://images.gitee.com/uploads/images/2019/1202/135713_d3566c6a_345098.jpeg" width="120">


 :boom:  :boom:  :boom: 注：悟空无代码平台采用全新的前后端分离模式，本仓库代码中已集成前端vue打包后文件，  **可免去打包操作，无需运行前端** 



# 悟空CRM目录结构

``` lua
wk_modules
├── module        -- 无代码模块
├── common        -- 基础模块(暂时无用)
```

# 核心功能模块

 **字段组件丰富：** 支持多行文本、网址、数字、下拉列表、百分比、时间、多选等丰富控件<br/>
 **布局样式多样：** 可调整表单内容大小与形式、平铺或下拉等格式，满足表单元素多样化。<br/>
 **跨应用关联：** 不同应用之间数据关联以实现跨应用数据调取，同时支持对不同应用数据间的筛选与聚合。<br/>
 **多维权限设置：** 对于表单内容，可对其权限设为必填、设为唯一或选择隐藏。<br/>
 **节点类型多样：** 审批节点、填写节点、抄送节点等，根据节点任务选择对应类型。<br/>
 **分支规则设定：** 数据筛选标准的数据才能进入对应的分支，实现对数据的筛选与分流。<br/>
 **节点权限控制：** 不同应用之间数据关联以实现跨应用数据调取。<br/>
 **操作简单灵活：** 通过拖拽方式，将各类组件进行添加<br/>
 **支持多元组件：** 丰富多样的组件库，按需整合信息。筛选/文本/报表等多种组件<br/><br/>
 **多类型图标样式：** 数据表、汇总表、指标卡等数十种图标类型，涵盖广泛的应用场景。<br/>


# 悟空无代码平台使用的主要技术栈

|名称                 | 版本                     | 说明   |
|---------------------|---------------------------|----  |
| spring-cloud-alibaba| 2021.0.4                  |  核心框架  |
| spring-boot         | 2.6.11                    |  spring版本  |
| mybatis-plus        | 3.5.2                     |  ORM框架  |
| nacos               | 2.1.0                     |  注册中心以及配置管理  |
| seata               | 1.2.0                     |  分布式事务 |
| elasticsearch       | 7.15.2                    |  搜索引擎中间件  |
| jetcache            | 2.7.1                     |  分布式缓存框架  |
| feign               | 3.1.4                     |  服务调用        |
| rocketmq            | 4.9.4                     |  消息队列        |


# 使用说明

### 一、前置环境
- Jdk1.8
- Maven3.5^   
- Mysql5.7^
- Redis(版本不限)
- Elasticsearch 7.15.2
- Nacos（1.4^)
- RocketMq（4.9.4)

# 安装

安装说明：[安装说明](https://gitee.com/wukongcrm_admin/wukong-nocode/wikis/%E5%AE%89%E8%A3%85%E8%AF%B4%E6%98%8E)

### 安装说明

#### 一、依赖环境安装

###### 1. 安装jdk
```
yum -y install java-1.8.0-openjdk-devel;
```

###### 2. 安装redis
```
yum -y install epel-release;
yum -y install redis;
chkconfig redis on;
#-- 修改redis密码为123456
yum -y install vim;
vim /etc/redis.conf;
#-- 在文件最下面追加一行
requirepass 123456
#-- 或者输入 / 搜索 # requirepass foobared
#-- 将前面的#删除，将foobared改为123456
#-- 修改完成之后 :wq 保存并退出,重启redis
service redis restart;
```

###### 3.安装nacos [官方文档](https://nacos.io/zh-cn/docs/v2/quickstart/quick-start.html)
```
# 保存到/opt目录下
wget https://github.com/alibaba/nacos/releases/download/2.0.3/nacos-server-2.0.3.zip -P /opt
yum install unzip
unzip /opt/nacos-server-2.0.3.zip -d /opt/nacos
cd /opt/nacos/bin
sh startup.sh -m standalone
```

###### 4.安装elasticsearch
```
-- 推荐使用docker安装
yum install docker
docker run -d --name es --restart=always -p 9200:9200 -p 9300:9300 -e "discovery.type=single-node" docker.elastic.co/elasticsearch/elasticsearch:7.15.2
docker exec -it elastic /bin/bash 
bin/elasticsearch-plugin install analysis-icu
exit
-- 重启es
docker restart es
```

###### 5.安装rocketmq
```
cd /opt
wget https://archive.apache.org/dist/rocketmq/4.9.2/rocketmq-all-4.9.2-bin-release.zip
unzip rocketmq-all-4.9.2-bin-release.zip
cd rocketmq-4.9.2/
nohup sh bin/mqnamesrv >/dev/null 2>&1 &
nohup sh bin/mqbroker -n localhost:9876 >/dev/null 2>&1 &
```

###### 6.安装mysql
```
wget https://repo.mysql.com//mysql80-community-release-el7-3.noarch.rpm
yum -y install mysql80-community-release-el7-3.noarch.rpm
yum -y install mysql-community-server --nogpgcheck
sudo systemctl start mysqld.service;
sudo systemctl enable mysqld.service;

--查看安装的mysql默认密码
grep "password" /var/log/mysqld.log
--进入mysql 例：mysql -u root -p"GXOO%eiI/7o>"
mysql - u root -p"此处为上一步的默认密码" 
 
--修改mysql密码，如下图所示
set global validate_password_policy=LOW;
ALTER USER 'root'@'localhost' IDENTIFIED BY 'password';

--退出mysql
exit
    
--修改mysql配置
vim /etc/my.cnf;
--输入 i 进入编辑模式，修改sql_mode设置，将下面sql_mode配置复制，到 [mysqld]下使用 shift+insert 粘贴
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION 
--修改完毕，按esc按键，然后 :wq 保存并退出，重启mysql
service mysqld restart;
```

#### 一、项目配置与启动

###### 1. 导入DB目录下数据库

###### 2.在项目根目录执行`mvn install`

###### 3.在module模块下resource目录配置数据库帐号信息以及redis帐号信息，MQ配置地址，elasticsearch配置地址`

###### 4. 访问[悟空ID](https://id.72crm.com/)获取账号
###### 注册之后点击默认企业
![默认企业](https://foruda.gitee.com/images/1673774011290861301/5bdc4983_8065912.png "img1.png")
###### 点击无代码管理
![无代码管理](https://foruda.gitee.com/images/1673774098067066785/3a905bfb_8065912.png "img2.png")
##### 将App ID，accessKey，secretKey复制到 module\src\main\resources\application-dev.yml，分别对应appId，clientId，clientSecret 如下图所示
![代码配置](https://foruda.gitee.com/images/1673774280708048007/6d5b6fc7_8065912.png "img4.png")
将appId复制到module\src\main\webapp\public\APPLICATION_ID.txt内，替换里面内容
###### 5. 项目打包部署
```
--项目打包
mkdir /opt/package
mvn clean -Dmaven.test.skip=true package
cp module/target/module.zip /opt/package
cd /opt/package
unzip module.zip -d module
cd module
sh 72crm.sh start
```


### 三、其他说明

#### 1.接口文档<br/>

```
接口文档地址：http://localhost:46923/doc.html
```
#### 2.docker镜像<br/>
```
敬请期待
```
### 四、悟空无代码平台功能模块预览
![输入图片说明](img/001.png)
![输入图片说明](img/002.png)
![输入图片说明](img/006.png)
![输入图片说明](img/007.png)
![输入图片说明](img/005.png)
![输入图片说明](img/004.png)