package com.zy.commons.lang;

import com.zy.commons.lang.security.ECCTools;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ECCToolsTest {
    @Test
    public void fn01() {
        Map<String, Object> map = ECCTools.initKey(256);
        String publicKey = ECCTools.getPublicKey(map);
        String encrypt = ECCTools.encrypt("hello".getBytes(StandardCharsets.UTF_8), publicKey);
        System.out.println(publicKey);
        System.out.println(encrypt);
        System.out.println("------------");
        String privateKey = ECCTools.getPrivateKey(map);
        String decrypt = ECCTools.decrypt(encrypt, privateKey);
        System.out.println(privateKey);
        System.out.println(decrypt);
    }
}
