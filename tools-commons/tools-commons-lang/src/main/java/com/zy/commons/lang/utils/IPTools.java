package com.zy.commons.lang.utils;

import com.zy.commons.lang.regex.RegexUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.Objects;

public final class IPTools {

    private IPTools() {
        throw new RuntimeException("IPTools can not instantiated.");
    }

    public static final String LOCALHOST_V4 = "127.0.0.1";
    public static final String LOCALHOST_V6 = "0:0:0:0:0:0:0:1";
    public static final String UNKNOWN = "known";

    /**
     * 获取 本机 IP 方法一
     * @return
     */
    public static String getLocalIP() {
        String ip;
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface nif = networkInterfaces.nextElement();
                Enumeration<InetAddress> inetAddresses = nif.getInetAddresses();
                while (inetAddresses.hasMoreElements()) {
                    ip = inetAddresses.nextElement().getHostAddress();
                    if (isLocalIP(ip)) {
                        return ip;
                    }
                }
            }
        } catch (SocketException e) {
            // e.printStackTrace();
        }
        return LOCALHOST_V4;
    }

    /**
     * 内网 ip 网段:
     * 10.xxx.xxx.xxx
     * 192.168.xxx.xxx
     * 172.16.xxx.xxx - 172.31.xxx.xxx
     * @param ip
     * @return
     */
    private static boolean isLocalIP(String ip) {
        if (Objects.isNull(ip)) {
            return false;
        }
        if (!ip.startsWith("192.168") && !ip.startsWith("10.")) {
            if (ip.startsWith("172.")) {
                int second = Integer.parseInt(ip.split("\\.")[1]);
                return second >= 16 && second <= 31;
            }
            return false;
        }
        return true;
    }

    /**
     * 获取 本机 IP 方法一
     * 通过InetAddress的实例对象包含以数字形式保存的IP地址，同时还可能包含主机名
     * （如果使用主机名来获取InetAddress的实例，或者使用数字来构造，并且启用了反向主机名解析的功能）。
     * InetAddress类提供了将主机名解析为IP地址（或反之）的方法。
     * @return
     */
    public static String findLocalIP() {
        try {
            // 获取计算机名称和ip地址
            InetAddress inetAddress = InetAddress.getLocalHost();
            // 获取ip地址
            return inetAddress.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return LOCALHOST_V4;
        }
    }

    /**
     * 获取用户真实IP地址
     * 可能的请求路径是: 客户端 --> 多级代理 --> 负载均衡器 (HAProxy, Nginx 等) --> web 应用服务器
     * @param request
     * @return
     *
     * 0.如果使用的是Druid连接池，可以参考使用：
     * com.alibaba.druid.util.DruidWebUtils#getRemoteAddr方法，但这个是经过多级代理的IP地址，需要自己处理下获取第一个。
     *
     * 1.这些请求头都不是http协议里的标准请求头，也就是说这个是各个代理服务器自己规定的表示客户端地址的请求头。
     * 如果哪天有一个代理服务器软件用oooo-client-ip这个请求头代表客户端请求，那下面的代码就不行了。
     *
     * 2.这些请求头不是代理服务器一定会带上的，网络上的很多匿名代理就没有这些请求头，
     * 所以获取到的客户端ip不一定是真实的客户端ip。代理服务器一般都可以自定义请求头设置。
     *
     * 3.即使请求经过的代理都会按自己的规范附上代理请求头，上面的代码也不能确保获得的一定是客户端ip。
     * 不同的网络架构，判断请求头的顺序是不一样的。
     *
     * 4.最重要的一点，请求头都是可以伪造的。
     * 如果一些对客户端校验较严格的应用（比如投票）要获取客户端ip，应该直接使用ip=request.getRemoteAddr()，
     * 虽然获取到的可能是代理的ip而不是客户端的ip，但这个获取到的ip基本上是不可能伪造的，也就杜绝了刷票的可能。
     * (有分析说arp欺骗+syn有可能伪造此ip，如果真的可以，这是所有基于TCP协议都存在的漏洞)，这个ip是tcp连接里的ip。
     *
     * 伪代码：
     *
     * 1）ip = request.getHeader(“X-FORWARDED-FOR “)
     *
     * 2）如果该值为空或数组长度为0或等于”unknown”，那么：
     * ip = request.getHeader(“Proxy-Client-IP”)
     *
     * 3）如果该值为空或数组长度为0或等于”unknown”，那么：
     * ip = request.getHeader(“WL-Proxy-Client-IP”)
     *
     * 4）如果该值为空或数组长度为0或等于”unknown”，那么：
     * ip = request.getHeader(“HTTP_CLIENT_IP”)
     *
     * 5）如果该值为空或数组长度为0或等于”unknown”，那么：
     * ip = request.getHeader(“X-Real-IP”)
     *
     * 6）如果该值为空或数组长度为0或等于”unknown”，那么：
     * ip = request.getRemoteAddr ()
     */
    public static String getClientIP(HttpServletRequest request) {
        //  X-Forwarded-For, 这是一个 Squid 开发的字段，只有在通过了HTTP代理或者负载均衡服务器时才会添加该项。
        // 格式为X-Forwarded-For:client1,proxy1,proxy2，一般情况下，第一个ip为客户端真实ip，后面的为经过的代理服务器ip。现在大部分的代理都会加上这个请求头。
        String ip = request.getHeader("x-forwarded-for");
        // Proxy-Client-IP/WL- Proxy-Client-IP, 这个一般是经过apache http服务器的请求才会有，用apache http做代理时一般会加上Proxy-Client-IP请求头，而WL-Proxy-Client-IP是他的 weblogic 插件加上的头。
        if (Objects.isNull(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            // HTTP_CLIENT_IP, 有些代理服务器会加上此请求头。
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            // X-Real-IP, nginx 代理一般会加上此请求头。
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
            if (Objects.equals(LOCALHOST_V4, ip) || Objects.equals(LOCALHOST_V6, ip)) {
                // 根据网卡获取本机 IP
                ip = getLocalIP();
            }
        }
        if (ip.contains(",")) {
            return ip.split(",")[0];
        } else {
            return ip;
        }
    }

    /**
     * long->ip
     * 1.采用StringBuffer方便字符串拼接。
     * 2.ip第一位：整数直接右移24位。
     *   ip第二位：整数先高8位置0.再右移16位。
     *   ip第三位：整数先高16位置0.再右移8位。
     *   ip第四位：整数高24位置0.
     * 3.将他们用分隔符拼接即可。
     * @param longIP
     * @return
     */
    public static String longToIpv4(long longIP) {
        return String.valueOf(longIP >>> 24) + "." +
                ((longIP & 16777215L) >>> 16) + "." +
                ((longIP & 65535L) >>> 8) + "." +
                (longIP & 255L);
    }

    /*ip->long：
     *1.将ip地址按字符串读入，用分隔符分割开后成为一个字符串数组{xyzo}。
     * 2.将数组里的字符串强转为long类型后执行：x^24+y^16+z^8+o  得到最后的返回值。
     * 3.这里的加权采用移位(<<)完成。
     * @param strIp :ip地址 例：x.y.z.o
     * @return  转换后的long类型值
     */
    public static long ipv4ToLong(String strIP) {
        if (RegexUtils.validRegex(strIP, RegexUtils.REGEX_IPV4)) {
            long[] ip = new long[4];
            int position1 = strIP.indexOf(".");
            int position2 = strIP.indexOf(".", position1 + 1);
            int position3 = strIP.indexOf(".", position2 + 1);
            ip[0] = Long.parseLong(strIP.substring(0, position1));
            ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
            ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
            ip[3] = Long.parseLong(strIP.substring(position3 + 1));
            return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        } else {
            return 0L;
        }
    }
}
