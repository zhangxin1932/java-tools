#0.总结
##0.1推荐加密算法
```
HASH算法:   SHA256
对称算法:   AES_GCM
不对称算法:  RSA2048
```
##0.2已知不安全加密算法
```
RSA(1024及以下), RSAWithSHA1, 
AES_ECB, DES, 3DES, SKIPJACK, RC2, 
MD2, MD4, MD5, SHA1
```
##0.3AES优先选择:
```
分组密码算法: AES_CBC, AES_GCM
流密码算法: AES_DFB

个人数据的传输, 存储(DB, redis, 文件)建议用AES_CBC加密
有搜索的优先用AES_ECB或HMAC
```
#1.密码的常用术语：
```
1.密码体制：由明文空间、密文空间、密钥空间、加密算法和解密算法5部分组成。
2.密码协议：也称为安全协议，是指以密码学为基础的消息交换的通信协议，目的是在网络环境中提供安全的服务。
3.柯克霍夫原则：数据的安全基于密钥而不是算法的保密。即系统的安全取决于密钥，对密钥保密，对算法公开。——现代密码学设计的基本原则。
```
#2.密码的分类：
##2.1按照时间分为
```
古典密码
现代密码
```
##2.2按照加密算法是否公开分为
```
受限制的算法==>军事领域
基于密钥的算法（之所以把算法公开主要是防止加密算法的发明人利用它做一些不为人知的事）
```
##2.3按照密码体制的不同可以分为
```
对称密码
    加密秘钥与解密秘钥相同
非对称密码
    加密秘钥与解密秘钥不同, 秘钥分为公钥和私钥
```
##2.4按照明文的处理方法可以分为
```
分组密码：加密时将明文分成固定长度的组，用同一密钥和算法对每一块加密，输出也是固定长度的密文。多用于网络加密。

流密码：也称为序列密码。加密时每次加密一位或者一个字节明文。
　　
散列函数（又称为hash函数），可以用来验证数据的完整性。
    它的特点是：长度不受限制、hash值容易计算、散列的运算是不可逆的。
　　与散列函数相关的算法有：消息摘要算法（MD5等）、安全散列算法（SHA）、消息认证码算法（MAC）。

数字签名：主要针对以数字的形式存储的消息进行的处理。
```
#3.安全机制
##3.1OSI与TCP/IP安全体系
###3.1.1TCP/IP安全体系结构
```
网络接口层-->网络层-->传输层-->应用层
```
###3.1.2安全服务与安全机制
```
安全服务                          安全机制
抗否认性服务                      公正机制
数据完整性服务                    数据完整性机制
数据保密性服务                    加密机制  +  业务流填充机制
访问控制服务                      访问控制机制  +  路由控制机制
认证(鉴别)服务                    认证机制  +  数字签名机制
```
```
业务流填充机制：
    在数据传输的过程中填入一些额外的信息混淆真实的数据。
```
##3.2Java安全组成、包、及第三方拓展
###3.2.1java安全组成
```
JCA(java cryptography architecture)
    java加密体系结构
JCE(java cryptography extension)
    java加密扩展包
JSSE(java secure sockets extension)
    java安全套接字API

JAAS(java authentication authorization service)
    Java验证和授权API
    提供了灵活和可伸缩的机制来保证客户端或服务器端的Java程序。
```
###3.2.2java本身安全包
```
java.security:消息摘要

javax.crypto:安全消息摘要，消息认证（鉴别）码

java.net.ssl：安全套接字（常用的类：HttpsURLConnection、SSLContext）
```
```
jdk自带的加密解密配置:
%JAVA_HOME%/jre/lib/security/java.security

security.provider.1=sun.security.provider.Sun
security.provider.2=sun.security.rsa.SunRsaSign
security.provider.3=sun.security.ec.SunEC
security.provider.4=com.sun.net.ssl.internal.ssl.Provider
security.provider.5=com.sun.crypto.provider.SunJCE
security.provider.6=sun.security.jgss.SunProvider
security.provider.7=com.sun.security.sasl.Provider
security.provider.8=org.jcp.xml.dsig.internal.dom.XMLDSigRI
security.provider.9=sun.security.smartcardio.SunPCSC
security.provider.10=sun.security.mscapi.SunMSCAPI

使用jdk以外的扩展包, 需要修改资源文件, 并增加相关的内容, 
这是使用jdk以外的扩展包的方式之一
```
###3.2.3java第三方扩展
```
Bouncy Castle:支持两种方案：①配置；②调用
Commons Codec：Apache，Base64、二进制、十六进制、字符集编码；URL编码/解码
```
#4.算法分类


```
散列算法进行身份验证,采用非对称加密算法管理对称算法的密钥，然后用对称加密算法加密数据
```


##4.1BASE64加密算法
```
base64算法是基于64个字符的一种替换算法。
base64加密的产生是电子邮件的“历史问题”——邮件只能传输ASCII码。
base64比较经典的就是加密后以等号结尾。
不是安全领域下的加解密算法，只是一个编码算法
该算法可以由几种方式实现：
    JDK8、Bouncy Castle、Commons Codec。
```
```
base64加密的应用场景：
1.X.509公钥证书
2.文本传输(如一个xml包含另一个xml时)
3.HTTP协议(其key,value要进行URLEncode, 避免空格,等号等字符导致解析出问题)
4.电子邮件（SMTP协议）(有些协议不支持不可见字符的传递，只能用大于32的可见字符来传递信息)
5.图片base64编码
```
##4.2消息摘要算法加密(验证数据的完整性)
```
消息摘要算法主要分为3类：
MD（Message Digest）
SHA（Secure Hash Algorithm）
MAC（Message Authentication Code）
以上3类算法的主要作用是验证数据的完整性——是数字签名的核心算法。
```

```
应用场景：
主要用于验证，防止信息被修。
具体用途如：文件校验、数字签名、鉴权协议
```

###4.2.1消息摘要算法-MD
```
MD算法家族有3类MD2、MD4、MD5，MD家族生成的都是128位的信息摘要。

算法    摘要长度      实现方
MD2		128	         JDK
MD4		128	         Bouncy Castle
MD5		128	         JDK

JDK本身提供了MD2和MD5的实现，
apache的Commons Codec在JDK的基础上进行了改良，
使用Commons Codec提供接口进行MD2和MD5加密将会简单很多。
```
```
MD算法的应用

注册时:
　　应用程序服务器将用户提交的密码进行MD5,即:
        数据库中存放的用户名是明文,
        而密码是密文（16进制字符串）摘要算法,得到32位的16进制字符串（密文）。
        把用户名（明文）和密码（密文）进行信息持久化存储到数据库中，
        最后返回注册结果。
登录时:
　　应用程序服务器同样对密码进行MD5摘要,
    然后将用户提交的用户名和密码的摘要信息和数据库中存储的信息进行比对，
    最后返回登录结果。
```
###4.2.2消息摘要算法——SHA　
```
安全散列算法，固定长度的摘要信息。
被认为是MD5的继承者。
是一个系列，包括SHA-1、SHA-2（SHA-224、SHA-256、SHA-384、SHA-512），
也就是除了SHA-1，其他的4种都被称为是SHA-2。
每种算法的摘要长度和实现方如下：

算法          摘要长度          实现方
SHA-1           160             JDK
SHA-224         224             Bouncy Castle
SHA-256         256             JDK
SHA-384         384             JDK
SHA-512         512             JDK

SHA算法的实现和MD算法的实现大同小异，也是JDK提供了默认的几种实现，
apache的Commons Codec在JDK的基础上进行了优化，使其更好用，
而Bouncy Castle是JDK的拓展，提供了JDK和Commons Codec没有的SHA-224的实现。
```
```
SHA算法的应用
　　在浏览器的证书管理器中证书：WEB证书一般采用SHA算法。

消息摘要算法是为了防止消息在传输过程中的篡改

如QQ账号一键登录，通常腾讯会给每一个接入方一个key,
可能会约定一个消息传送的格式, 例如：
http://**?msg=12Hsad74mj&&timestamp=1309488734,
其中msg=摘要信息+key+时间戳。
```
###4.2.3消息摘要算法——HMAC
```
MAC(Message Authentication Code)，
兼容了MD和SHA的特性，并且在它们的基础上加入了密钥。
因此MAC也称为HMAC（keyed-Hash Message Authentication Code）含有密钥的散列函数算法。

• MD系列：HmacMD2、HmacMD4、HmacMD5
• SHA系列：HmacSHA1、HmacSHA224、HmacSHA256、HmacSHA384、HmacSHA512
每种算法的摘要长度和实现方如下：

算法          摘要长度        实现方
HmacMD2         128          Bouncy Castle
HmacMD4         128          Bouncy Castle
HmacMD5         128          JDK
HmacSHA1        160          JDK
HmacSHA224      224          Bouncy Castle
HmacSHA256      256          JDK
HmacSHA384      384          JDK
HmacSHA512      512          JDK
```
##4.3对称加密算法加密
```
对称密钥（Symmetric-key algorithm）又称为共享密钥加密，对称密钥在加密和解密的过程中使用的密钥是相同的，
常见的对称加密算法有DES、3DES、AES、RC5、RC6。  
优点是，计算速度快，如果每个客户端与服务端单独维护一个密钥，那么服务端需要管理的密钥将是成千上万，这会给服务端带来噩梦。
缺点是，密钥需要在通讯的两端共享，让彼此知道密钥是什么对方才能正确解密，如果所有客户端都共享同一个密钥，那么这个密钥就像万能钥匙一样，可以凭借一个密钥破解所有人的密文了。
```

```
应用场景:
对称加密算法用来对敏感数据等信息进行加密
数据加密标准，速度较快，适用于加密大量数据的场合
适用于8位的小型单片机或者普通的32位微处理器,并且适合用专门的硬件实现，硬件实现能够使其吞吐量（每秒可以到达的加密/解密bit数）达到十亿量级。
也适用于RFID系统
```
###4.3.1DES(Data Encryption Standard)
```
在使用BC进行DES加解密的时候
除了需要使用Security.addProvider()方法增加一个BouncyCastle，
还需要指定密钥生成器的提供者为BC，否则会默认使用sun的JCE。
```
###4.3.2=>3DES
```
3DES的好处是密钥长度增加。迭代次数增加。
```
###4.3.3AES加密
```
AES产生的原因是3重DES的效率比较低而DES的安全性较低。
AES是目前使用最多的对称加密算法，AES还有一个优势是至今尚未被破解。
AES通常用于移动通信系统的加密以及基于SSH协议的软件（SSH Client、SecurityCRT）的加密。
密钥长度以及实现方如下：
秘钥长度    默认长度      工作模式        填充方式          实现方
128          128         ECB,CBC,PCBC    NoPadding          JDK 
192                      CTR,CTS,CFB     PKCS5Padding       (192,256秘钥
256                      CFB8到128,OFB   ISO10126Padding    需要获得
                         OFB8到128                          无政策限制权限)
                         
无政策限制权限文件是指：
因为某些国家的进口管制限制，java发布的运行环境包中的一些加解密有一定的限制。
一般的JDK的jre的security包中的
local_policy.jar和US_export_policy.jar都是没有内容的，
所以只能使用128位加密。    

 * 如需要更高的支持需要从oracle官网下载jce包, 下载地址:
 * https://www.oracle.com/technetwork/java/javase/downloads/jce-all-download-5170447.html
 * 然后:
 * 在%JDK_Home%/jre/lib/security目录下(不要进入policy目录)放置： local_policy.jar和US_export_policy.jar
 * 如果安装了JRE,
 * 在%JRE_Home%/jre/lib/security目录下(不要进入policy目录)放置： local_policy.jar和US_export_policy.jar                 
```
###4.3.4PBE(Password Based Encryption)
```
基于口令的加密。
PBE算法结合了消息摘要算法和对称加密算法的优点，是一种特殊的对称加密算法。
因为口令是比较好记的，就容易通过穷举、猜测的方式获得口令——
针对这种情况，我们采用的方式是加盐（Salt），
通过加入一些额外的内容（通常是随机字符）去扰乱。
实现的方式有2种：JDK和BC。
```
```
应用场景:
    登录注册
```
##4.4非对称加密算法
```
加密密钥分为公钥和私钥。
可以使用公钥加密私钥解密，也可以使用私钥加密公钥解密。
非对称加密算法主要有：
DH（Diffie-Hellman）密钥交换算法、
RSA（基于因子分解）、
Elgamal（基于离散对数）、
ECC（Elliptical Curve Cryptography，椭圆曲线加密）。
```

```
非对称密钥（public-key cryptography），又称为公开密钥加密，
服务端会生成一对密钥，一个私钥保存在服务端，仅自己知道，另一个是公钥，公钥可以自由发布供任何人使用。
客户端的明文通过公钥加密后的密文需要用私钥解密。
非对称密钥在加密和解密的过程的使用的密钥是不同的密钥，加密和解密是不对称的，所以称之为非对称加密。
与对称密钥加密相比，非对称加密无需在客户端和服务端之间共享密钥，只要私钥不发给任何用户，即使公钥在网上被截获，也无法被解密，仅有被窃取的公钥是没有任何用处的。
常见的非对称加密有RSA，非对称加解密的过程：

1.服务端生成配对的公钥和私钥
2.私钥保存在服务端，公钥发送给客户端
3.客户端使用公钥加密明文传输给服务端
4.服务端使用私钥解密密文得到明文
```

```
应用场景:
小量的机密数据，可以采用非对称加密算法
```
###4.4.1DH（密钥交换）算法
```
如何安全地传送密钥是对称加密算法的症结所在。
密钥交换算法是通过构建本地密钥来解决对称加密算法中的密钥传递的问题的。

秘钥长度            默认      工作模式        填充方式        实习方
512-1024(64倍数)   1024         无               无            无

实现该算法的步骤和所需要的类如下：
1.初始化发送方密钥
-KeyPairGenerator
-KeyPair（密钥载体，密钥对，包括公约和私钥）
-PublicKey
2.初始化接收方密钥
-KeyFactory（可以生成公钥和私钥）
-X509EncodedKeySpec（根据ASN.1标准进行密钥编码）
-DHPublicKey
-DHParameterSpec
-KeyPairGenerator
-PrivateKey
3.密钥构建
-KeyAgreement（提供密钥一致性或密钥交换协议的功能）
-SecretKey（生成一个分组的秘密密钥）
-KeyFactory
-X509EncodedKeySpec
-PublicKey
4.加解密
-Cipher（JCE框架的核心）
```
###4.4.2RSA
```
在RSA算法中公钥的长度远远小于私钥的长度。

秘钥长度      默认    工作模式        填充方式                  实现方
512-         1024      ECB     NoPadding,PKCS1Padding           JDK
65536                          OAEPWITHMD5AndMGF1Padding
(64整数倍)                     OAEPWITHSHA1AndMGF1Padding
                              OAEPWITHSHA256AndMGF1Padding
                              OAEPWITHSHA384AndMGF1Padding
                              OAEPWITHSHA512AndMGF1Padding
RSA有两种模式公钥加密私钥解密和私钥加密公钥解密两种模式.
```
###4.4.3Elgamal算法
```
和RSA不同的是它只提供公钥加密私钥解密，它依靠BouncyCastle实现。

秘钥长度      默认    工作模式        填充方式                  实现方
160-         1024      ECB     NoPadding,PKCS1Padding           BC
16384                          OAEPWITHMD5AndMGF1Padding
                               OAEPWITHSHA1AndMGF1Padding
                               OAEPWITHSHA224AndMGF1Padding
                               OAEPWITHSHA256AndMGF1Padding
                               OAEPWITHSHA384AndMGF1Padding
                               OAEPWITHSHA512AndMGF1Padding
                               ISO9796-1Padding

```
##4.5数字签名
```
数字签名是带有密钥（公钥、私钥）的消息摘要算法。
主要作用是验证数据的完整性、认证数据来源、抗否认。
在数字签名的实现中我们使用私钥签名、公钥验证。
常用的数字签名算法包括RSA、DSA、ECDSA。
```

```数据在浏览器和服务器之间传输时，有可能在传输过程中被冒充的盗贼把内容替换了，
   那么如何保证数据是真实服务器发送的而不被调包呢，同时如何保证传输的数据没有被人篡改呢，
   要解决这两个问题就必须用到数字签名，
   数字签名就如同日常生活的中的签名一样，一旦在合同书上落下了你的大名，
   从法律意义上就确定是你本人签的字儿，这是任何人都没法仿造的，因为这是你专有的手迹，任何人是造不出来的。
   
   那么在计算机中的数字签名怎么回事呢？
   数字签名就是用于验证传输的内容是不是真实服务器发送的数据，发送的数据有没有被篡改过，
   它就干这两件事，是非对称加密的一种应用场景。
   不过他是反过来用私钥来加密，通过与之配对的公钥来解密。
   
   (1)发送报文时，发送方用一个哈希函数从报文文本中生成报文摘要,
   然后用自己的私人密钥对这个摘要进行加密，
   这个加密后的摘要将作为报文的数字签名和报文一起发送给接收方，
   (2)接收方首先用与发送方一样的哈希函数从接收到的原始报文中计算出报文摘要，
   接着再用发送方的公用密钥来对报文附加的数字签名进行解密，
   如果这两个摘要相同、那么接收方就能确认该数字签名是发送方的。
```

###4.5.1RSA
```
该算法是数字签名的经典算法。主要包括MD和SHA两类:
秘钥长度是512 - 65536(64的整数倍)
签名长度===秘钥长度
JDK实现的算法有:
    MD2WithRSA, MD5WithRSA, SHA1WithRSA
    秘钥默认长度是1024
BC实现的算法有:
    SHA224WithRSA,SHA256WithRSA,SHA384WithRSA,SHA512WithRSA
    RIPEMD128WithRSA,RIPEMD160WithRSA
    秘钥默认长度是2048

```
###4.5.2DSA
```
DSS（Digital Signature Standard），数字签名标准，
通过这个标准逐步形成了DSA（Digital Signature Algorithm），数字签名算算法。
DSA仅仅包括数字签名，不能进行加解密。
该算法到的实现和RSA数字签名的实现大同小异

秘钥长度是512 - 1024(64的整数倍)
签名长度===秘钥长度
秘钥默认长度===1024
JDK实现的算法有:
    SHA1WithRSA
BC实现的算法有:
    SHA224WithRSA,SHA256WithRSA,SHA384WithRSA,SHA512WithRSA

```
###4.5.3ECDSA
```
微软的Office、Windows操作系统的验证就是ECDSA算法——
椭圆曲线数字签名算法（Elliptic Curve Digital Signature Algorithm），
在2000年的时候称为了ANSI和IEEE的标准。
特点是：速度快、签名短、强度高。
在JDK1.7update4之后提供了对ECDSA的支持。
该签名的算法也和RSA的数字签名算法也是大同小异。

秘钥长度是112 - 517
秘钥默认长度===256
算法                  签名长度        实现方
NONEWithECDSA           128          JDK/BC
RIPEMD160WithECDSA      160            BC
SHA1WithECDSA           160          JDK/BC
SHA224WithECDSA         224            BC
SHA256WithECDSA         256          JDK/BC
SHA384WithECDSA         384          JDK/BC
SHA512WithECDSA         512          JDK/BC
```
##4.6数字证书（Certificate Authority）
```
数字证书简称CA，它由权威机构给某网站颁发的一种认可凭证，这个凭证是被大家（浏览器）所认可的，
为什么需要用数字证书呢，难道有了数字签名还不够安全吗？
有这样一种情况，就是浏览器无法确定所有的真实服务器是不是真的是真实的，
举一个简单的例子：
A厂家给你们家安装锁，同时把钥匙也交给你，只要钥匙能打开锁，你就可以确定钥匙和锁是配对的，如果有人把钥匙换了或者把锁换了，你是打不开门的，你就知道肯定被窃取了，
但是如果有人把锁和钥匙替换成另一套表面看起来差不多的，但质量差很多的，虽然钥匙和锁配套，但是你却不能确定这是否真的是A厂家给你的，
那么这时候，你可以找质检部门来检验一下，这套锁是不是真的来自于A厂家，质检部门是权威机构，他说的话是可以被公众认可的。

同样的， 因为如果有人（张三）用自己的公钥把真实服务器发送给浏览器的公钥替换了，
于是张三用自己的私钥执行相同的步骤对文本Hash、数字签名，最后得到的结果都没什么问题，
但事实上浏览器看到的东西却不是真实服务器给的，而是被张三从里到外（公钥到私钥）换了一通。
那么如何保证你现在使用的公钥就是真实服务器发给你的呢？
我们就用数字证书来解决这个问题。
数字证书一般由数字证书认证机构（Certificate Authority）颁发，
证书里面包含了真实服务器的公钥和网站的一些其他信息，
数字证书机构用自己的私钥加密后发给浏览器，
浏览器使用数字证书机构的公钥解密后得到真实服务器的公钥。
这个过程是建立在被大家所认可的证书机构之上得到的公钥，所以这是一种安全的方式。
```
#5. SSL/TLS
```
SSL（Secure Sockets Layer，安全套接层）是1994年由网景公司（Netscape）设计的一种协议，并在该公司的Web浏览器上进行了实现。
随后，很多Web浏览器都采用了这一协议，使其成为了事实上的行业标准。
SSL已经于1995年发布了3.0版本，但在2014年，SSL3.0协议被发现存在可能导致POODLE攻击的安全漏洞，因此SSL3.0已经不安全了。
```

```
TLS（Transport Layer Security ，传输层安全）是IETF在SSL3.0的基础上设计的协议。在1999年作为RFC2246发布的TLS1.0，实际上相当于SSL3.1。
2006年，TLS1.1以以RFC4346的形式发布，这个版本中增加了针对CBC攻击的策略并加入了AES对称加密算法。TLS1.2中新增了对GCM、CCM认证加密的支持，此外还新增了HMAC-SHA256，并删除了IDEA和DES，将伪随机函数（PRF）改为基于SHA-256来实现。
```
##5.1用于查看SSL/TLS的相关命令
```
查看openssl证书

/etc/pki/CA 
      newcerts    存放CA签署(颁发)过的数字证书(证书备份目录)
      private         用于存放CA的私钥
      crl                 吊销的证书
/etc/pki/tls
      openssl.cnf     openssl的CA主配置文件
      private          证书密钥存放目录
      cert.pem       软链接到certs/ca-bundle.crt
```

```
查看TLS版本
centos/redhat:
yum info  gnutls

debian
dpkg   -l|grep gnutls
```
```
查看linux是否支持TLS1.0, TLS1.1及TLS1.2

openssl s_client -connect intl.jdair.net:443 -tls1
openssl s_client -connect intl.jdair.net:443 -tls1_1
openssl s_client -connect intl.jdair.net:443 -tls1_2
```
##5.2SSL/TLS的工作
```
我们想要实现通过本地的Web浏览器访问网络上的Web服务器，并进行安全通信。
举个例子来说就是，用户希望通过Web浏览器向xx银行发送信用卡号。在这里，我们有几个必须要解决的问题。
（1）用户的信用卡号和地址在发送到xx银行的过程中不能被窃听。
（2）用户的信用卡号和地址在发送到xx银行的过程中不能被篡改。
（3）确认通信对方的Web服务器是真正的xx银行。
在这里，
（1）是机密性问题，
（2）是完整性问题，
（3）则是认证的问题。
要确保机密性，可以使用对称密码。
由于对称密码的密钥不能被攻击者预测，因此我们使用伪随机数生成器来生成密钥。
若要将对称密码的密钥发送给通信对象，可以使用公钥密码或者Diffie-Hellman密钥交换。
要识别篡改，对数据进行认证，可以使用消息认证码。消息认证码是使用单向散列函数来实现的。
要对通信对象进行认证，可以使用对公钥加上数字签名所生成的证书。

SSL/TLS就是将对称密码、公钥密码、单向散列函数、
消息认证码、伪随机数生成器、数字签名等技术相结合来实现安全通信的。
```
##5.3TLS加密套件
```
https://www.iana.org/assignments/tls-parameters/tls-parameters.xhtml
```

```
TLS定义了几百个加密套件，可是在HTTP2面前基本都被否定了，列入了加密套件黑名单。
剩余的加密套件中，有20来个是被推荐的，还有二十来个没有被推荐

实现TLS1.2的话，下述加密套件中的一个是必要的:
TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256，
TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384，
TLS_DHE_RSA_WITH_AES_128_GCM_SHA256

另外，还要为了兼容性实现一下HTTP2黑名单中的部分套件:
TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA，
TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA，
TLS_RSA_WITH_AES_128_CBC_SHA，
TLS_RSA_WITH_AES_256_CBC_SHA，
TLS_RSA_WITH_3DES_EDE_CBC_SHA
```

#6.https(HyperText Transfer Protocol over Secure Socket Layer)
```
是一种网络安全传输协议, HTTPS，也称作HTTP over TLS。
TLS的前身是SSL，
TLS 1.0通常被标示为SSL 3.1，
TLS 1.1为SSL 3.2，
TLS 1.2为SSL 3.3。
HTTPS是位于安全层之上的HTTP，这个安全层位于TCP之上
```

##6.1HTTP的几个缺点
```
HTTP协议的实现本身非常简单，不论是谁发过来的请求都会返回响应，因此不确认通信方，会存在以下隐患：

    无法确认请求发送至目标的Web服务器是否是按真实意图返回响应的那台服务器。有可能是伪装的Web服务器。
    无法确认响应返回到的客户端是否是按真实意图接收响应的那个客户端，有可能是伪装的客户端。
    无法确定正在通信的对方是否具备访问权限，因为某些Web服务器上保存着重要的信息，只想发给特定用户通信的权限。
    无法判断请求是来自何方，出自何手。
    有人在通信的过程中抓取到了数据包, 可能导致数据被窃取。

即使是无意义的请求也会照单全收。无法阻止海量请求下的DoS攻击。

```
##6.2 https相关加密算法
```
HTTPS一般使用的加密与HASH算法如下：
　　非对称加密算法：RSA，DSA/DSS
　　对称加密算法：AES，RC4，3DES
　　HASH算法：MD5，SHA1，SHA256

其中
    非对称加密算法用于在握手过程中加密生成的密码，
    对称加密算法用于对真正传输的数据进行加密，
    HASH算法用于验证数据的完整性。

由于浏览器生成的密码是整个数据加密的关键，因此在传输的时候使用了非对称加密算法对其加密。
非对称加密算法会生成公钥和私钥，公钥只能用于加密数据，因此可以随意传输。
而网站的私钥用于对数据进行解密，所以网站都会非常小心的保管自己的私钥，防止泄漏。

TLS握手过程中如果有任何错误，都会使加密连接断开，从而阻止了隐私信息的传输
```

```
SSL 协议既用到了公钥加密技术又用到了对称加密技术，
对称加密技术虽然比公钥加密技术的速度快，
可是公钥加密技术提供了更好的身份认证技术。
SSL 的握手协议非常有效的让客户和服务器之间完成相互之间的身份认证，其主要过程如下：

①客户端向服务器请求HTTPS连接:
客户端向服务器传送客户端SSL 协议的版本号，加密算法的种类，
产生的随机数，以及其他服务器和客户端之间通讯所需要的各种信息。

②服务器确认并返回证书。
服务器向客户端传送SSL 协议的版本号，加密算法的种类，
随机数以及其他相关信息，同时服务器还将向客户端传送自己的证书。

③客户端验证服务器发来的证书。
客户端利用服务器传过来的信息验证服务器的合法性，服务器的合法性包括：
证书是否过期，
发行服务器证书的CA 是否可靠，
发行者证书的公钥能否正确解开服务器证书的“发行者的数字签名”，
服务器证书上的域名是否和服务器的实际域名相匹配。
如果合法性验证没有通过，通讯将断开;
如果合法性验证通过，将继续进行第四步。

④信息验证通过，客户端生成随机密钥A，用公钥加密后发给服务器。
从第③步验证过的证书里面可以拿到服务器的公钥，客户端生成的随机密钥就使用这个公钥来加密，
加密之后，只有拥有该服务器(持有私钥)才能解密出来，保证安全。

⑤服务器用私钥解密出随机密钥A，以后通信就用这个随机密钥A来对通信进行加密。
我们这个握手过程并没有将验证客户端身份的逻辑加进去。
因为在大多数的情况下，HTTPS只是验证服务器的身份而已。
如果要验证客户端的身份，需要客户端拥有证书，在握手时发送证书，而这个证书是需要成本的。
```


https://www.jianshu.com/p/7c01759c28dd