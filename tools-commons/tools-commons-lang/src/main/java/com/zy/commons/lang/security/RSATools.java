package com.zy.commons.lang.security;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.springframework.util.Assert;

/**
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
 * RSA 是可逆的, 既能加密, 也能解密
 * 由于密钥和密文的二进制数据普遍较长，故使用 Base64 而非 HexStr 对字节进行编码
 * 明文数据的字符集为 UTF-8
 * RSA算法是第一个能同时用于加密和数字签名的算法，也易于理解和操作。
 * 参考资源： 
 * https://www.cnblogs.com/pcheng/p/9629621.html
 */
@Slf4j
public class RSATools {

    private static final String ALGORITHM = "RSA";
    private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
    private static final String PUBLIC_SECRET_KEY = "publicKey";
    private static final String PRIVATE_SECRET_KEY = "privateKey";

    /**
     * @param keySize 密钥位数
     * @return 一对 公钥 + 私钥
     */
    public static Map<String, Object> initKey(int keySize) {
        try {
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGen.initialize(keySize < 1024 ? 1024 : keySize);
            KeyPair keyPair = keyPairGen.generateKeyPair();

            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_SECRET_KEY, publicKey);
            keyMap.put(PRIVATE_SECRET_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            log.error("failed to init secret key.", e);
            throw new RuntimeException("failed to init secret key.");
        }
    }

    /**
     * 从密钥对中取 Base64 编码的私钥
     *
     * @param keyMap 一对 公钥 + 私钥
     * @return Base64 编码的私钥
     */
    public static String getPrivateKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PRIVATE_SECRET_KEY);
        byte[] keyBytes = key.getEncoded();
        return Base64.encodeBase64String(keyBytes);
    }

    /**
     * 从密钥对中取 Base64 编码的公钥
     * @param keyMap 一对 公钥 + 私钥
     * @return Base64 编码的公钥
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_SECRET_KEY);
        byte[] keyBytes = key.getEncoded();
        return Base64.encodeBase64String(keyBytes);
    }



    ///////////////////// 私钥加密， 公钥解密 开始 ///////////////////////////

    /**
     * 私钥加密
     *
     * @param data       明文数据
     * @param privateKey Base64 编码的私钥
     * @return Base64 编码的密文
     */
    public static String encryptByPrivateKey(String data, String privateKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        byte[] cipherBytes;
        try {
            cipherBytes = encryptByPrivateKey(dataBytes, keyBytes);
            return Base64.encodeBase64String(cipherBytes);
        } catch (Exception e) {
            log.error("failed to encrypt by private secret key.", e);
            throw new RuntimeException("failed to encrypt by private secret key.");
        }
    }

    /**
     * 私钥加密（字节形式）
     *
     * @param data       明文数据
     * @param privateKey 私钥
     * @return 密文
     */
    public static byte[] encryptByPrivateKey(byte[] data, byte[] privateKey) throws InvalidKeyException, IllegalBlockSizeException {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key key = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("failed to encrypt by private secret key.", e);
            throw new RuntimeException("failed to encrypt by private secret key.");
        }
    }

    /**
     * 公钥解密
     *
     * @param data      Base64 编码的密文数据
     * @param publicKey Base64 编码的公钥
     * @return UTF-8编码的明文
     */
    public static String decryptByPublicKey(String data, String publicKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        byte[] dataBytes = Base64.decodeBase64(data);
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        byte[] plainBytes;
        try {
            plainBytes = decryptByPublicKey(dataBytes, keyBytes);
            return new String(plainBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("failed to decrypt by public secret key.", e);
            throw new RuntimeException("failed to decrypt by public secret key.");
        }
    }

    /**
     * 公钥解密（字节形式）
     *
     * @param data      数据
     * @param publicKey 公钥
     * @return UTF-8 编码的明文
     */
    public static byte[] decryptByPublicKey(byte[] data, byte[] publicKey) throws InvalidKeyException {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key key = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("failed to decrypt by public secret key.", e);
            throw new RuntimeException("failed to decrypt by public secret key.");
        }
    }

    ///////////////////// 私钥加密， 公钥解密 结束 ///////////////////////////




    ///////////////////// 公钥加密， 私钥解密 开始 ///////////////////////////

    /**
     * 公钥加密
     *
     * @param data      明文数据
     * @param publicKey Base64 编码的公钥
     * @return Base64 编码的密文
     */
    public static String encryptByPublicKey(String data, String publicKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        byte[] cipherBytes;
        try {
            cipherBytes = encryptByPublicKey(dataBytes, keyBytes);
            return Base64.encodeBase64String(cipherBytes);
        } catch (Exception e) {
            log.error("failed to encrypt by private secret key.", e);
            throw new RuntimeException("failed to encrypt by private secret key.");
        }
    }

    /**
     * 公钥加密（字节形式）
     *
     * @param data      明文数据
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] encryptByPublicKey(byte[] data, byte[] publicKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        try {
            X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key key = keyFactory.generatePublic(x509KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.ENCRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("failed to encrypt by public secret key.", e);
            throw new RuntimeException("failed to encrypt by public secret key.");
        }
    }

    /**
     * 私钥解密
     *
     * @param data       Base64 编码的密文数据
     * @param privateKey Base64 编码的私钥
     * @return 明文数据
     */
    public static String decryptByPrivateKey(String data, String privateKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        byte[] dataBytes = Base64.decodeBase64(data);
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        byte[] plainBytes = decryptByPrivateKey(dataBytes, keyBytes);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }

    /**
     * 私钥解密（字节形式）
     *
     * @param data       密文数据
     * @param privateKey 私钥
     * @return 明文数据
     */
    public static byte[] decryptByPrivateKey(byte[] data, byte[] privateKey)  {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        try {
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            Key key = keyFactory.generatePrivate(pkcs8KeySpec);
            Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
            cipher.init(Cipher.DECRYPT_MODE, key);
            return cipher.doFinal(data);
        } catch (Exception e) {
            log.error("failed to decrypt by private secret key.", e);
            throw new RuntimeException("failed to decrypt by private secret key.");
        }
    }

    ///////////////////// 公钥加密， 私钥解密 结束 ///////////////////////////




    ///////////////////// 私钥签名， 公钥验签 开始 ///////////////////////////
    /**
     * 私钥签名
     *
     * @param data       明文数据
     * @param privateKey Base64 编码的私钥
     * @return Base64 编码的签名值
     */
    public static String sign(String data, String privateKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        byte[] signBytes;
        try {
            signBytes = sign(dataBytes, keyBytes);
            return Base64.encodeBase64String(signBytes);
        } catch (Exception e) {
            log.error("failed to sign by private secret key.", e);
            throw new RuntimeException("failed to sign by private secret key.");
        }
    }

    /**
     * 私钥签名（字节形式）
     *
     * @param data       明文数据
     * @param privateKey 私钥
     * @return 签名值
     */
    public static byte[] sign(byte[] data, byte[] privateKey) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(privateKey, "privateKey can't be null!");
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(privateKey);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey priKey = keyFactory.generatePrivate(pkcs8KeySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            log.error("failed to sign by private secret key.", e);
            throw new RuntimeException("failed to sign by private secret key.");
        }
    }

    /**
     * 公钥验签
     *
     * @param data      明文数据
     * @param publicKey Base64 编码的公钥
     * @param sign      Base64 编码的签名值
     * @return 是否一致
     */
    public static boolean verifySign(String data, String publicKey, String sign) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        Assert.notNull(sign, "sign can't be null!");
        byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
        byte[] keyBytes = Base64.decodeBase64(publicKey);
        byte[] signBytes = Base64.decodeBase64(sign);
        try {
            return verifySign(dataBytes, keyBytes, signBytes);
        } catch (Exception e) {
            log.error("failed to verifySign with public secret key.", e);
            throw new RuntimeException("failed to verifySign with public secret key.");
        }
    }

    /**
     * 公钥验签（字节形式）
     *
     * @param data      明文数据
     * @param publicKey 公钥
     * @param sign      签名值
     * @return 是否一致
     */
    public static boolean verifySign(byte[] data, byte[] publicKey, byte[] sign) {
        Assert.notNull(data, "data can't be null!");
        Assert.notNull(publicKey, "publicKey can't be null!");
        Assert.notNull(sign, "sign can't be null!");
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKey);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey pubKey = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(data);
            return signature.verify(sign);
        } catch (Exception e) {
            log.error("failed to verifySign with public secret key.", e);
            throw new RuntimeException("failed to verifySign with public secret key.");
        }
    }

    ///////////////////// 私钥签名， 公钥验签 结束 ///////////////////////////
}
