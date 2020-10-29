package com.zy.commons.lang.security;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
 * https://blog.csdn.net/lidaidai001/article/details/93599319
 *
 * 单向散列加密算法常用于提取数据，验证数据的完整性。
 * 发送者将明文通过单向加密算法加密生成定长的密文串，然后将明文和密文串传递给接收方。
 * 接收方在收到报文后，将解明文使用相同的单向加密算法进行加密，得出加密后的密文串。
 * 随后与发送者发送过来的密文串进行对比，若发送前和发送后的密文串相一致，则说明传输过程中数据没有损坏；
 * 若不一致，说明传输过程中数据丢失了。
 * 其次也用于密码加密传递存储。
 * 单向加密算法只能用于对数据的加密，无法被解密，其特点为定长输出、雪崩效应。
 *
 *
 * MD5加密算法
 * MD5加密算法用的是哈希函数，一般应用于对信息产生信息摘要，防止信息被篡改。
 * 最常见的使用是对密码加密、生成数字签名。从严格意义上来说，MD5是摘要算法，并非加密算法。
 * MD5 生成密文时，无论要加密的字符串有多长，它都会输出长度为 128bits 的一个密文串，通常16 进制时为 32 个字符。
 *
 * SHA1加密算法
 * 与MD5一样，也是目前较流行的摘要算法。
 * 但SHA1 比 MD5 的 安全性更高。对
 * 长度小于 2 ^ 64 位的消息，SHA1会产生一个 160 位的 消息摘要。
 * 基于 MD5、SHA1 的信息摘要特性以及不可逆，可以被应用在检查文件完整性， 数字签名等场景。
 *
 * SHA256加密算法
 * SHA256是SHA2算法中的一种，如SHA2加密算法中有：SHA244、SHA256、SHA512等。
 * SHA2属于SHA1的升级，SHA1是160位的哈希值，而SHA2是组合值，有不同的位数，其中最受欢迎的是256位（SHA256算法）。
 * SSL行业选择SHA作为数字签名的散列算法，从2011到2015，一直以SHA-1位主导算法。
 * 但随着互联网技术的提升，SHA-1的缺点越来越突显。
 * 从去年起，SHA-2成为了新的标准，所以现在签发的SSL证书，必须使用该算法签名。
 *
 */
@Slf4j
public class HashAlgorithmTools {

    private static final String HMAC_SHA256 = "HmacSHA256";
    private static final String HMAC_SHA1 = "HmacSHA1";
    private static final String SHA256 = "SHA-256";
    private static final String SHA1 = "SHA";
    private static final String MD5 = "MD5";

    /**
     * HmacSHA1 加密
     * @param msg 消息 UTF-8
     * @param key  秘钥 UTF-8
     * @return 加密后字符串
     */
    public static String encryptByHmacSHA1(String msg, String key) {
        try {
            Mac hmacSHA1 = Mac.getInstance(HMAC_SHA1);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA1);
            hmacSHA1.init(secretKey);
            byte[] bytes = hmacSHA1.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            log.error("failed to encrypt by HmacSHA1.", e);
            throw new RuntimeException("failed to encrypt by HmacSHA1.");
        }
    }

    /**
     * HmacSHA256 加密
     * @param msg 消息 UTF-8
     * @param key  秘钥 UTF-8
     * @return 加密后字符串
     */
    public static String encryptByHmacSHA256(String msg, String key) {
        try {
            Mac hmacSHA256 = Mac.getInstance(HMAC_SHA256);
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            hmacSHA256.init(secretKey);
            byte[] bytes = hmacSHA256.doFinal(msg.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            log.error("failed to encrypt by HmacSHA256.", e);
            throw new RuntimeException("failed to encrypt by HmacSHA256.");
        }
    }

    /**
     * SHA256 生成摘要信息
     * @param msg 消息 UTF-8
     * @return
     */
    public static String encryptBySHA256(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA256);
            return Base64.encodeBase64String(md.digest(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("failed to encrypt by SHA256.", e);
            throw new RuntimeException("failed to encrypt by SHA256.");
        }
    }

    /**
     * SHA1 生成摘要信息
     * @param msg 消息 UTF-8
     * @return
     */
    public static String encryptBySHA1(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance(SHA1);
            return Base64.encodeBase64String(md.digest(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("failed to encrypt by SHA1.", e);
            throw new RuntimeException("failed to encrypt by SHA1.");
        }
    }

    /**
     * MD5 生成摘要信息
     * @param msg 消息 UTF-8
     * @return
     */
    public static String encryptByMD5(String msg) {
        try {
            MessageDigest md = MessageDigest.getInstance(MD5);
            return Base64.encodeBase64String(md.digest(msg.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            log.error("failed to encrypt by MD5.", e);
            throw new RuntimeException("failed to encrypt by MD5.");
        }
    }

    /**
     * 获取文件的 MD5 值
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String fileMD5Hex(String filePath) throws IOException {
        return DigestUtils.md5Hex(new FileInputStream(filePath));
    }

}
