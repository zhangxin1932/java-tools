package com.zy.commons.lang;

import com.zy.commons.lang.security.AESTools;
import org.junit.Test;

public class AESToolsTest {
    @Test
    public void fn01() {
        // 生成秘钥方法2:
        String key = AESTools.generateKey(AESTools.AESType.AES_CBC_256);
        // 生成秘钥方法1: String key = createAESKey(AESType.AES_128);
        System.out.println("密钥：" + key);
        String plaintext = "hello world!hello world!hello world!hello world!hello world!hello world!hello world!";
        String ciphertext = AESTools.encryptAES(plaintext, key, AESTools.AESType.AES_CBC_256, "hello");
        System.out.println("秘文：" + ciphertext);
        plaintext = AESTools.decryptAES(ciphertext, key, AESTools.AESType.AES_CBC_256, "hello");
        System.out.println("明文：" + plaintext);
    }

    @Test
    public void fn02() {
        // 这里是常用账号的加密方式, 秘钥存在了脑子里
        String key = "***";
        String plaintext = "&&&";
        String encrypt = AESTools.encryptAES(plaintext, key, AESTools.AESType.AES_ECB_128, null);
        System.out.println(encrypt);
        String decrypt = AESTools.decryptAES(encrypt, key, AESTools.AESType.AES_ECB_128, null);
        System.out.println(decrypt);
    }
}
