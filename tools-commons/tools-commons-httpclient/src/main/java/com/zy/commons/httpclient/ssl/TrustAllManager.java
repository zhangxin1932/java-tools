package com.zy.commons.httpclient.ssl;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.X509ExtendedTrustManager;
import java.net.Socket;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class TrustAllManager extends X509ExtendedTrustManager {

  @Override
  public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
  }

  @Override
  public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return null;
  }

  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType,
      Socket socket) throws CertificateException {
  }

  @Override
  public void checkClientTrusted(X509Certificate[] chain, String authType,
      SSLEngine engine) throws CertificateException {
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType,
      Socket socket) throws CertificateException {
  }

  @Override
  public void checkServerTrusted(X509Certificate[] chain, String authType,
      SSLEngine engine) throws CertificateException {
  }
}
