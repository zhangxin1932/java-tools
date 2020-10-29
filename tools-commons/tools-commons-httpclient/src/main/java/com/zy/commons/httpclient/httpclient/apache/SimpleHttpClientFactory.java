package com.zy.commons.httpclient.httpclient.apache;

import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * https://github.com/apache/httpcomponents-client/blob/4.5.x/httpclient/src/examples/org/apache/http/examples/client/ClientCustomSSL.java
 *
 * https://blog.csdn.net/a442828032/article/details/80190633
 * HttpClient4.5 对HostnameVerifier的处理策略
 * 除了信任验证和客户端身份验证在SSL/TLS协议层进行之外,HttpClient可以有选择的验证目标主机名是否跟服务端存储在X.509认证里的一致,
 * 一旦连接已经建立,这种验证可以为服务器认证提供额外的保障,javax.net.ssl.HostnameVerifier 接口代表了主机名验证的一种策略,
 * HttpClient附带了两中javax.net.ssl.HostnameVerifier的实现,注意:不要把主机名验证跟SSL信任验证混淆
 *  DefaultHostnameVerifier:
 *      HttpClient使用的默认实现,与RFC2818兼容,主机名必须匹配证书指定的任何可替换的名称,或者没有可替换名称下证书主体中指定的具体的CN,CN和可替换名称中都可能有通配符。
 *  NoopHostnameVerifier:
 *      这个主机名验证器基本上就是把主机名验证关闭了,它接受任何有效的SSL会话来匹配目标主机。
 * 默认HttpClient使用DefaultHostnameVerifier实现,如果有需要的话你可以指定一个不同的主机名验证器
 * SSLContext sslContext = SSLContexts.createSystemDefault();
 * SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory( sslContext, NoopHostnameVerifier.INSTANCE);
 *
 * HttpClient4.4使用Mozilla基金会维护的公共后缀列表去确保SSL证书的通配符不会被多个通用顶级域名误用,
 * HttpClient会附带一个该列表的最新的拷贝,最新的修正版在https://publicsuffix.org/list/,强烈建议从源数据每天更新一次并且保持一份本地拷贝。
 * PublicSuffixMatcher publicSuffixMatcher = PublicSuffixMatcherLoader.load( PublicSuffixMatcher.class.getResource("my-copy-effective_tld_names.dat"));
 * DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(publicSuffixMatcher);
 * 你可以通过使用null匹配来关闭公共后缀列表验证
 * DefaultHostnameVerifier hostnameVerifier = new DefaultHostnameVerifier(null);
 *
 */
public final class SimpleHttpClientFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleHttpClientFactory.class);

    private static final String CLIENT_AGREEMENT = "TLS";

    public static CloseableHttpClient buildWithSSLOSecret(HttpProfile profile) {
        LayeredConnectionSocketFactory ssl = null;
        final SSLContext sslContext;
        String[] supportedCipherSuites = SSLSocketFactoryImpl.ciphers;
        String[] supportedProtocols = SSLSocketFactoryImpl.protocols;
        try {
            sslContext = SSLContext.getInstance(CLIENT_AGREEMENT);
            sslContext.init(null, null, new SecureRandom());
            ssl = new SSLConnectionSocketFactory(new SSLSocketFactoryImpl(sslContext.getSocketFactory()),
                    supportedProtocols,
                    supportedCipherSuites,
                    SSLConnectionSocketFactory.getDefaultHostnameVerifier());

        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            LOGGER.warn("failed to initialize SSLContext, {}, use default socket factory.", e.getMessage());
        }
        // 设置连接管理器
        final Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create().
                register("http", PlainConnectionSocketFactory.getSocketFactory()).
                register("https", ssl != null ? ssl : SSLConnectionSocketFactory.getSocketFactory()).
                build();
        final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(sfr);
        connectionManager.setMaxTotal(profile.getMaxConnectionPerRoute());
        connectionManager.setValidateAfterInactivity(profile.getValidateAfterInactivity());
        SocketConfig socketConfig = getSocketConfig(profile);
        connectionManager.setDefaultSocketConfig(socketConfig);
        // 请求配置项: 含各种超时时间设置, 代理设置
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(profile.getConnectionTimeoutInMills())
                .setSocketTimeout(profile.getSocketTimeoutInMills())
                .setConnectionRequestTimeout(profile.getConnectionTimeoutInMills())
                .build();
        // 构建客户端
        return HttpClientBuilder.create()
                .setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfig)
                .setRetryHandler(new DefaultHttpRequestRetryHandler(profile.getRetryTimes()))
                .build();
    }

    private static SocketConfig getSocketConfig(HttpProfile profile) {
        return SocketConfig.custom()
                .setTcpNoDelay(true)
                .setSoKeepAlive(true)
                .setSoLinger(0)
                .setSoTimeout(profile.getSocketTimeoutInMills())
                .setSoReuseAddress(true)
                .build();
    }

    private static class DefaultHttpRequestRetryHandler implements HttpRequestRetryHandler {

        private int retryTimes;

        private DefaultHttpRequestRetryHandler(int retryTimes) {
            this.retryTimes = retryTimes;
        }

        @Override
        public boolean retryRequest(IOException e, int executionCount, HttpContext httpContext) {
            if (e != null) {
                LOGGER.warn("Execute retry, exception is {}, executionCount is {}.", e.getClass(), executionCount);
            }
            if (executionCount >= retryTimes) {
                LOGGER.warn("Already retry {} times, it will not retry again.", executionCount);
                return false;
            }
            return true;
        }
    }
}
