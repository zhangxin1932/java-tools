#优秀项目集锦
1.解决 ScheduledThreadPoolExecutor 可能内存溢出的问题
余志坚, & 姜春志. (2016). 采用ScheduledThreadPoolExecutor执行定时重试任务时内存溢出的分析及解决. 科技资讯, 014(007), 15-17.
https://www.docin.com/p-1705571719.html

2.955项目
https://github.com/formulahendry/955.WLB

#java-tools目录
##tools-ssl
```
主要是对SSL/TLS工具类,apache httpClient工具类的封装等
包括:
--algorithm => java加密算法相关知识, AES, RSA, BASE64, SHA512等, https流程
--httpclient => apache http client
--ssl工具类
```
##tools-undefined
```
未分类的工具类
包括:
--concurrent => java.util.concurrent包下并发类,线程池使用方法
--dateTimeUtils => 时间日期格式化工具类(如jdk版本, apache版本, 自定义版本)
--encrypt => 加密工具类
--flowcontrol => google流控处理方案
--regex => 正则表达式工具类
```
#apache工具类
```
BeanUtils 
Commons-BeanUtils 提供对 Java 反射和自省API的包装

Betwixt 
Betwixt提供将 JavaBean 映射至 XML 文档，以及相反映射的服务.

Chain 
Chain 提供实现组织复杂的处理流程的“责任链模式”.

CLI 
CLI 提供针对命令行参数，选项，选项组，强制选项等的简单API.

Codec 
Codec 包含一些通用的编码解码算法。包括一些语音编码器， Hex, Base64, 以及URL encoder.

Collections 
Commons-Collections 提供一个类包来扩展和增加标准的 Java Collection框架

Configuration 
Commons-Configuration 工具对各种各式的配置和参考文件提供读取帮助.

Daemon 
一种 unix-daemon-like java 代码的替代机制

DBCP 
Commons-DBCP 提供数据库连接池服务

DbUtils 
DbUtils 是一个 JDBC helper 类库，完成数据库任务的简单的资源清除代码.

Digester 
Commons-Digester 是一个 XML-Java对象的映射工具，用于解析 XML配置文件.

Discovery 
Commons-Discovery 提供工具来定位资源 (包括类) ，通过使用各种模式来映射服务/引用名称和资源名称。.

EL 
Commons-EL 提供在JSP2.0规范中定义的EL表达式的解释器.

FileUpload 
FileUpload 使得在你可以在应用和Servlet中容易的加入强大和高性能的文件上传能力

HttpClient 
Commons-HttpClient 提供了可以工作于HTTP协议客户端的一个框架.

IO 
IO 是一个 I/O 工具集

Jelly 
Jelly是一个基于 XML 的脚本和处理引擎。 Jelly 借鉴了 JSP 定指标签，Velocity, Cocoon和Xdoclet中的脚本引擎的许多优点。Jelly 可以用在命令行， Ant 或者 Servlet之中。

Jexl 
Jexl是一个表达式语言，通过借鉴来自于Velocity的经验扩展了JSTL定义的表达式语言。.

JXPath 
Commons-JXPath 提供了使用Xpath语法操纵符合Java类命名规范的 JavaBeans的工具。也支持 maps, DOM 和其他对象模型。.

Lang 
Commons-Lang 提供了许多许多通用的工具类集，提供了一些java.lang中类的扩展功能

Latka 
Commons-Latka 是一个HTTP 功能测试包，用于自动化的QA,验收和衰减测试.

Launcher 
Launcher 组件是一个交叉平台的Java 应用载入器。Commons-launcher 消除了需要批处理或者Shell脚本来载入Java 类。.原始的 Java 类来自于Jakarta Tomcat 4.0 项目

Logging 
Commons-Logging 是一个各种 logging API实现的包裹类.

Math 
Math 是一个轻量的，自包含的数学和统计组件，解决了许多非常通用但没有及时出现在Java标准语言中的实践问题.

Modeler 
Commons-Modeler 提供了建模兼容JMX规范的Mbean的机制.

Net 
Net 是一个网络工具集，基于 NetComponents 代码，包括 FTP 客户端等等。

Pool 
Commons-Pool 提供了通用对象池接口，一个用于创建模块化对象池的工具包，以及通常的对象池实现.

Primitives 
Commons-Primitives提供了一个更小，更快和更易使用的对Java基本类型的支持。当前主要是针对基本类型的 collection。.

Validator 
The com.zy.commons-validator提供了一个简单的，可扩展的框架来在一个XML文件中定义校验器 (校验方法)和校验规则。支持校验规则的和错误消息的国际化。
```
#google工具类
```
<dependency> 
<groupId>com.google.guava</groupId> 
<artifactId>guava</artifactId> 
<version>23.0</version> 
</dependency>
```
```
基础工具[Basic utilities]：让我们更愉快的使用 Java 语言。

    使用和避免 null [Using and avoiding null]：null的定义很模糊，可能导致令人疑惑的错误，有时会让我们很不爽。很多的 Guava 工具类对null都是快速失败的，拒绝使用null，，而不是盲目的接收它们。
    前置条件[Preconditions]：让你的方法更容易进行前置条件的检查。
    通用 object 方法[Common object methods]：简化Object方法的实现，例如hashCode()和toString().
    排序[Ordering]：Guava 有强大且流畅的Comparator类。
    可抛的[Throwable]：简化异常和错误的检查机制。

集合[Collections]：Guava 扩展了 JDK 的集合体系，这是 Guava 最成熟且最受欢迎的部分。

    不可变集合：为了进行防御性编程、使用常量集合和提高效率。
    新集合类型：提供了多集合、多 Map、多表、双向 Map 等。
    强大的集合工具类：普通的操作并没有在java.util.Collections中提供。
    扩展工具类：装饰Collection？实现Iterator？我们让类似的操作变的更简单。

图[Graphs]：这是一个图结构数据的模型类库，它展现了实体以及图和实体之间的关系，主要的特点包括：

    图[Graph]：图的边缘是没有自己标识和信息的匿名实体。
    值图[ValueGraph]：图的边缘关联着非唯一的值。
    网络[Network]：图的边缘是唯一的对象。
    支持可变的、不可变的、定向的和无向的图以及其他一些属性。

缓存[Caches]：支持本地缓存，也支持多种缓存过期行为。

函数风格[Functional idioms]：Guava 的函数风格能够显著的简化代码，但请谨慎使用。

并发[Concurrency]：强大而简单的抽象，让编写正确的并发代码更简单。

    ListenableFuture： Future，结束时触发回调 。
    Service：开启和关闭服务，帮助我们处理困难的状态逻辑。

字符串[Strings]：非常有用的字符串处理工具，包括分割、拼接等等。

原生类型[Primitives]：扩展了 JDK 没有提供的原生类型（像int和char）操作，包含了某些类型的无符号变量。

区间[Ranges]：Guava 强大的 API 提供了基于Comparable类型区间比较功能，包连续类型和离散类型。

输入输出流[I/O]：针对 Java 5 和 Java 6 版本，简化了 I/O 操作，尤其是 I/O 流和文件操作。

散列[Hashing]：提供了比Object.hashCode()更负责的哈希实现，包含了 Bloom 过滤器。

事件总线[EventBus]：在不需要组件之间显示注册的情况下，提供了组件之间的发布-订阅模式的通信。

数学运算[Math]：优化了 JDK 已经提供的数学工具类，并彻底的测试了 JDK 没有提供的数学工具类。

反射[Reflection]：对应 Java 反射能力的 Guava 工具类。
```

#hutool
https://hutool.cn/docs/#/