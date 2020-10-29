package com.zy.commons.httpclient.httpclient.apache;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 扩展SSLSocketFactory，设置算法和协议列表。
 */
public class SSLSocketFactoryImpl extends SSLSocketFactory {

    private SSLSocketFactory sslSocketFactory;

    public static String[] ciphers = new String[] {"TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_ECDSA_WITH_AES_256_GCM_SHA384",
            "TLS_DHE_RSA_WITH_AES_128_GCM_SHA256",
            "TLS_ECDHE_RSA_WITH_AES_256_CBC_SHA",
            "TLS_ECDHE_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_128_CBC_SHA",
            "TLS_RSA_WITH_AES_256_CBC_SHA",
            "TLS_RSA_WITH_3DES_EDE_CBC_SHA"
    };

    public static String[] protocols = new String[] {"TLSv1.1", "TLSv1.2", "TLSv1.3"};

    public SSLSocketFactoryImpl(SSLSocketFactory sslSocketFactory) {
        this.sslSocketFactory = sslSocketFactory;
    }

    @Override
    public String[] getDefaultCipherSuites() {
        return this.sslSocketFactory.getDefaultCipherSuites();
    }

    @Override
    public String[] getSupportedCipherSuites() {
        return this.ciphers;
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException {
        return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(socket, host, port, autoClose));
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress inetAddress, int localPort) throws IOException, UnknownHostException {
        return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host, port, inetAddress, localPort));
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host, port));
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(address, port, localAddress, localPort));
    }

    private SSLSocket wrapSocket(SSLSocket socket) {
        socket.setEnabledProtocols(protocols);
        socket.setEnabledCipherSuites(ciphers);
        return socket;
    }

}
