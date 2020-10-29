package com.zy.commons.httpclient.ssl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 和应用相关的信息，方便定制使用。 目前主要包含密码解密、证书路径、IP和Port等内容。
 *
 */
public abstract class SSLCustom {
  private static final Logger LOG = LoggerFactory.getLogger(SSLCustom.class);

  public static SSLCustom defaultSSLCustom() {
    final SSLCustom custom = new SSLCustom() {
      @Override
      public char[] decode(char[] encrypted) {
        return encrypted;
      }

      @Override
      public String getFullPath(String filename) {
        return filename;
      }
    };
    return custom;
  }

  public static SSLCustom createSSLCustom(String name) {
    try {
      if (name != null && !name.isEmpty()) {
        return (SSLCustom) Class.forName(name).newInstance();
      }
    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
      LOG.warn("init SSLCustom class failed, name is " + name);
    }
    return defaultSSLCustom();
  }

  public abstract char[] decode(char[] encrypted);

  public abstract String getFullPath(String filename);

  public String getHost() {
    return null;
  }
}
