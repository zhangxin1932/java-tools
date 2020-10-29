package com.zy.commons.lang.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import sun.security.ec.ECKeyPairGenerator;
import javax.crypto.Cipher;
import javax.crypto.NullCipher;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.HashMap;
import java.util.Map;

/**
 * 非对称算法
 * ECC（椭圆加密算法）是一种公钥加密体制
 * 主要优势是在某些情况下它比其他的方法使用更小的密钥——比如RSA加密算法——提供相当的或更高等级的安全。
 * 不过一个缺点是加密和解密操作的实现比其他机制时间长。
 * 它相比RSA算法，对 CPU 消耗严重。
 *
 */
@Slf4j
public class ECCTools {
    private static final String ALGORITHM = "EC";
    private static final String PUBLIC_KEY = "ECCPublicKey";
    private static final String PRIVATE_KEY = "ECCPrivateKey";

    /**
     * 用私钥解密
     * @param data
     * @param key
     * @return
     */
    public static String decrypt(String data, String key) {
        // 对密钥解密
        byte[] keyBytes = Base64.decodeBase64(key);
        // 取得私钥
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            ECPrivateKey priKey = (ECPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
            ECPrivateKeySpec ecPrivateKeySpec = new ECPrivateKeySpec(priKey.getS(),priKey.getParams());
            // 对数据解密
            Cipher cipher = new NullCipher();
            cipher.init(Cipher.DECRYPT_MODE, priKey, ecPrivateKeySpec.getParams());
            return new String(Base64.decodeBase64(cipher.doFinal(data.getBytes(StandardCharsets.UTF_8))), StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("failed to decrypt by public secret key.", e);
            throw new RuntimeException("failed to decrypt by public secret key.");
        }
    }

    /**
     * 用公钥加密
     * @param data
     * @param privateKey
     * @return
     */
    public static String encrypt(byte[] data, String privateKey) {
        // 对公钥解密
        byte[] keyBytes = Base64.decodeBase64(privateKey);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            ECPublicKey pubKey = (ECPublicKey) keyFactory.generatePublic(x509KeySpec);
            ECPublicKeySpec ecPublicKeySpec = new ECPublicKeySpec(pubKey.getW(), pubKey.getParams());
            Cipher cipher = new NullCipher();
            cipher.init(Cipher.ENCRYPT_MODE, pubKey, ecPublicKeySpec.getParams());
            byte[] encrypt = cipher.doFinal(data);
            return Base64.encodeBase64String(encrypt);
        } catch (Exception e) {
            log.error("failed to encrypt by private secret key.", e);
            throw new RuntimeException("failed to encrypt by private secret key.");
        }
    }

    /**
     * 取得私钥
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPrivateKey(Map<String, Object> keyMap){
        Key key = (Key) keyMap.get(PRIVATE_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 取得公钥
     * @param keyMap
     * @return
     * @throws Exception
     */
    public static String getPublicKey(Map<String, Object> keyMap) {
        Key key = (Key) keyMap.get(PUBLIC_KEY);
        return Base64.encodeBase64String(key.getEncoded());
    }

    /**
     * 初始化密钥对
     * @param keySize
     * @return
     */
    public static Map<String, Object> initKey(int keySize) {
        try {
            ECKeyPairGenerator generator = new ECKeyPairGenerator();
            generator.initialize(keySize, new SecureRandom());

            KeyPair keyPair1 = generator.generateKeyPair();
            ECPublicKey publicKey = (ECPublicKey) keyPair1.getPublic();
            ECPrivateKey privateKey = (ECPrivateKey) keyPair1.getPrivate();

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(PUBLIC_KEY, publicKey);
            keyMap.put(PRIVATE_KEY, privateKey);
            return keyMap;
        } catch (Exception e) {
            log.error("failed to init secret key.", e);
            throw new RuntimeException("failed to init secret key.");
        }
    }
}
