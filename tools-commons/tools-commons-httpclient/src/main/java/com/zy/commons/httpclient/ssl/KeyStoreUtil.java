package com.zy.commons.httpclient.ssl;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Collection;

public final class KeyStoreUtil {
  private KeyStoreUtil() {

  }

  public static KeyStore createKeyStore(String storeName, String storetype, char[] storeValue) {
    InputStream is = null;
    try {
      KeyStore keystore = KeyStore.getInstance(storetype);
      is = new FileInputStream(storeName);
      keystore.load(is, storeValue);
      return keystore;
    } catch (Exception e) {
      throw new IllegalArgumentException("Bad key store or value." + e.getMessage());
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          ignore();
        }
      }
    }
  }

  @SuppressWarnings({"rawtypes", "unchecked"})
  public static CRL[] createCRL(String crlFile) {
    InputStream is = null;
    try {
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      is = new FileInputStream(crlFile);
      Collection c = cf.generateCRLs(is);
      return  (CRL[]) c.toArray(new CRL[c.size()]);
    } catch (CertificateException e) {
      throw new IllegalArgumentException("bad cert file.");
    } catch (FileNotFoundException e) {
      throw new IllegalArgumentException("crl file not found.");
    } catch (CRLException e) {
      throw new IllegalArgumentException("bad crl file.");
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          ignore();
        }
      }
    }
  }

  public static KeyManager[] createKeyManagers(final KeyStore keystore, char[] keyValue) {
    try {
      KeyManagerFactory kmFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
      kmFactory.init(keystore, keyValue);
      return kmFactory.getKeyManagers();
    } catch (Exception e) {
      throw new IllegalArgumentException("Bad key store." + e.getMessage());
    }
  }

  public static TrustManager[] createTrustManagers(final KeyStore keystore) {
    try {
      TrustManagerFactory tmFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
      tmFactory.init(keystore);
      return tmFactory.getTrustManagers();
    } catch (Exception e) {
      throw new IllegalArgumentException("Bad trust store." + e.getMessage());
    }
  }

  private static void ignore() {
  }
}
