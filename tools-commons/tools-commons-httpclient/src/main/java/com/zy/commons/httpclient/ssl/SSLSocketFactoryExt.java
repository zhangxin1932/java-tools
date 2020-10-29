package com.zy.commons.httpclient.ssl;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 扩展SSLSocketFactory，设置算法和协议列表。
 *
 */
public class SSLSocketFactoryExt extends SSLSocketFactory {
  private SSLSocketFactory sslSocketFactory;

  private String[] ciphers;

  private String[] protos;

  public SSLSocketFactoryExt(SSLSocketFactory factory, String[] ciphers, String[] protos) {
    this.sslSocketFactory = factory;
    this.ciphers = ciphers;
    this.protos = protos;
  }

  @Override
  public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
    return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(s, host, port, autoClose));
  }

  @Override
  public String[] getDefaultCipherSuites() {
    return this.sslSocketFactory.getDefaultCipherSuites();
  }

  @Override
  public String[] getSupportedCipherSuites() {
    return this.sslSocketFactory.getSupportedCipherSuites();
  }

  @Override
  public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
    return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host, port));
  }

  @Override
  public Socket createSocket(InetAddress host, int port) throws IOException {
    return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host, port));
  }

  @Override
  public Socket createSocket(String host, int port, InetAddress localHost,
      int localPort) throws IOException, UnknownHostException {
    return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(host,
        port,
        localHost,
        localPort));
  }

  @Override
  public Socket createSocket(InetAddress address, int port, InetAddress localAddress,
      int localPort) throws IOException {
    return wrapSocket((SSLSocket) this.sslSocketFactory.createSocket(address,
        port,
        localAddress,
        localPort));
  }

  private SSLSocket wrapSocket(SSLSocket socket) {
    socket.setEnabledProtocols(protos);
    socket.setEnabledCipherSuites(ciphers);
    return socket;
  }
}
