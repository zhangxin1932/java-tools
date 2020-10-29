package com.zy.commons.httpclient.ssl;

import com.netflix.config.ConcurrentCompositeConfiguration;

public interface SSLOptionFactory {
  static SSLOptionFactory createSSLOptionFactory(String tag, ConcurrentCompositeConfiguration configSource) {
    String name = SSLOption.getStringProperty(configSource,
        null,
        "ssl." + tag + ".sslOptionFactory",
        "ssl.sslOptionFactory");
    return createSSLOptionFactory(name);
  }

  static SSLOptionFactory createSSLOptionFactory(String className) {
    if (className != null && !className.isEmpty()) {
      try {
        return (SSLOptionFactory) Class.forName(className).newInstance();
      } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
        throw new IllegalStateException("Failed to create SSLOptionFactory.", e);
      }
    }
    return null;
  }

  SSLOption createSSLOption();
}
