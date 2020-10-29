package com.zy.commons.lang.security;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Objects;

/**
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
 * AES算法工具类
 * AES 是可逆的, 既能加密, 也能解密
 * <p>
 * 由于美国软件出口限制，JDK默认使用的AES算法最高只能支持128位。
 * 如需要更高的支持需要从oracle官网下载jce包, 下载地址:
 * https://www.oracle.com/technetwork/java/javase/downloads/jce-all-download-5170447.html
 * 然后:
 * 在%JDK_Home%/jre/lib/security目录下(不要进入policy目录)放置： local_policy.jar和US_export_policy.jar
 * 如果安装了JRE,
 * 在%JRE_Home%/jre/lib/security目录下(不要进入policy目录)放置： local_policy.jar和US_export_policy.jar
 * <p>
 * 对应的AES加解密的KEY的长度：128-16、192-24、256-32.
 * <p>
 * <p>
 * <p>
 * <p>
 * AES五种加密模式
 * https://blog.csdn.net/whatday/article/details/97266912
 * 一、电码本模式（ECB）
 * 将整个明文分成若干段相同的小段，然后对每一小段进行加密。
 * 优点：操作简单，易于实现；分组独立，易于并行；误差不会被传送。——简单，可并行，不传送误差。
 * 缺点：掩盖不了明文结构信息，难以抵抗统计分析攻击。——可对明文进行主动攻击。
 * <p>
 * 二、密码分组链模式（CBC）
 * 先将明文切分成若干小段，然后每一小段与初始块或者上一段的密文段进行异或运算后，再与密钥进行加密。
 * 优点：能掩盖明文结构信息，保证相同密文可得不同明文，所以不容易主动攻击，安全性好于ECB，适合传输长度长的报文，是SSL和IPSec的标准。
 * 缺点：（1）不利于并行计算；（2）传递误差——前一个出错则后续全错；（3）第一个明文块需要与一个初始化向量IV进行抑或，初始化向量IV的选取比较复杂。
 * 初始化IV的选取方式：固定IV，计数器IV，随机IV（只能得到伪随机数，用的最多），瞬时IV（难以得到瞬时值）
 * <p>
 * 三、输出反馈模式（OFB）
 * 密码算法的输出（指密码key而不是密文）会反馈到密码算法的输入中，OFB模式并不是通过密码算法对明文直接加密，而是通过将明文分组和密码算法的输出进行XOR来产生密文分组。
 * 优点：隐藏了明文模式；结合了分组加密和流密码（分组密码转化为流模式）；可以及时加密传送小于分组的数据。
 * 缺点：不利于并行计算；需要生成秘钥流；对明文的主动攻击是可能的。
 * <p>
 * 四、计数器模式（CTR）
 * 完全的流模式。将瞬时值与计数器连接起来，然后对此进行加密产生密钥流的一个密钥块，再进行XOR操作 。
 * 优点：不泄露明文；仅需实现加密函数；无需填充；可并行计算。
 * 缺点：需要瞬时值IV，难以保证IV的唯一性。
 * <p>
 * 五、密码反馈模式（CFB）
 * 把分组密码当做流密码使用，即密码反馈模式可将DES分组密码置换成流密码。流密码具有密文和明文长度一致、运行实时的性质，这样数据可以在比分组小得多的单元里进行加密。如果需要发送的每个字符长为8比特，就应使用8比特密钥来加密每个字符。如果长度超过8比特，则造成浪费。但是要注意，由于CFB模式中分组密码是以流密码方式使用，所以加密和解密操作完全相同，因此无法适用于公钥密码系统，只能适用于对称密钥密码系统。
 * 密码反馈模式也需要一个初始量，无须保密，但对每条消息必须有一个不同的初始量。
 * 优点：可以处理任意长度的消息，能适应用户不同数据格式的需要。可实现自同步功能。就有有限步的错误传播，除能获得保密性外，还可用于认证。
 * 缺点：对信道错误较敏感，且会造成错误传播。数据加密的速率被降低。
 * <p>
 * <p>
 * <p>
 * <p>
 * AES 的一些对比及思考
 * 1.对比CBC和CTR
 * （1）CBC需要填充；CTR不用填充。
 * （2）CBC不可并行；CTR可并行速度快。
 * （3）CBC需要实现加密和解密函数；CTR实现简单，仅需实现加密函数。
 * （4）鲁棒性：CBC强于CTR——使用重复瞬时值，CBC会泄露初始明文块，CTR会泄露所有信息。
 * 如果有好的瞬时值选择策略，采用CTR，否则采用CBC。
 * 如加密成绩单，可选用CTR，因为学号唯一。可作为瞬时值。
 * <p>
 * 2.分组密码填充
 * 目的：将明文填充到满足分组大小，解密后再把填充去掉。
 * 如何填充：缺几个字节填充几个自己的几（如缺5个字节，填充5个字节的5）；如果不需要填充，则添加一个分组，分组中填充分组大小（如分组大小为64，填充16个字节的16）
 * <p>
 * 3.分组密码模式的安全性
 * 任何分组密码模式都存在信息的泄露，没有一个是完美的，任何分组模式都可能会泄露信息，这只是一个概率的问题。
 * 碰撞概率计算：
 * M个明文块，块长为N，以两个块组成一对，
 * 不同块的对数为：M(M-1)/2
 * 两个块相等的概率为：1/2的n次方
 * 密文块相等的数量期望为：M(M-1)/2的n+1次方。
 * 则当M(M-1)=2的n+1次方时候，即M约等于2的n/2次方时发生碰撞的概率约等于1。
 * 例如：分组长度为64，则当块数为2的32次方时，即加密数据2的32次方*64bit=256G时便会发生碰撞。
 * <p>
 * 结论：分组密码的安全不仅和秘钥长度有关还和分组长度有关。
 */
@Slf4j
public class AESTools {

    /*
     * 加密模式包括ECB, CBC
     *   --ECB模式是分组的模式
     *      --private static final String ECB_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
     *   --CBC是分块加密后，每块与前一块的加密结果异或后再加密,第一块加密的明文是与IV变量进行异或
     *      --private static final String CBC_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
     *
     */

    /**
     * 随机生成密钥的数据源
     */
    private static final String KEY_SOURCE = "qwertyuiopasdfghjklzxcvbnm1234567890";
    private static final String ALGORITHM_NAME = "AES";
    private static final int IVSize = 16;

    /**
     * AES加密，返回秘文
     *
     * @param plaintext 明文 utf-8
     * @param aesKey    Base64 编码过的秘钥, utf-8
     * @param type      AES 类型
     * @param iv        向量  utf-8, ECB 模式, 传 null
     * @return 秘文
     */
    public static String encryptAES(String plaintext, String aesKey, AESType type, String iv) {
        byte[] result = encrypt(plaintext.getBytes(StandardCharsets.UTF_8), aesKey.getBytes(StandardCharsets.UTF_8), type, generateIV(iv));
        return Base64.encodeBase64String(result);
    }

    /**
     * 加密
     *
     * @param content 需要加密的内容  utf-8
     * @param aesKey  秘钥  utf-8
     * @param type    AES 类型
     * @param iv      向量  utf-8, ECB 模式, 传 null
     * @return 加密字节数组  utf-8
     */
    public static byte[] encrypt(byte[] content, byte[] aesKey, AESType type, IvParameterSpec iv) {
        try {
            SecretKeySpec key = getSecretKeySpec(type.getKeySize(), aesKey);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(type.getMode());
            // 初始化
            if (Objects.isNull(iv)) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.ENCRYPT_MODE, key, iv);
            }
            return cipher.doFinal(content);
        } catch (Exception e) {
            log.error("failed to encrypt data.", e);
            throw new RuntimeException("failed to encrypt data.");
        }
    }

    /**
     * AES解密
     *
     * @param encryptText 秘文 utf-8
     * @param aesKey      秘钥 utf-8
     * @param type        AES 类型
     * @param iv          向量 utf-8, ECB 模式, 传 null
     * @return 明文
     */
    public static String decryptAES(String encryptText, String aesKey, AESType type, String iv) {
        byte[] plainByte = decrypt(Base64.decodeBase64(encryptText), aesKey.getBytes(StandardCharsets.UTF_8), type, generateIV(iv));
        return new String(plainByte, StandardCharsets.UTF_8);
    }

    /**
     * 解密
     *
     * @param content 待解密内容 utf-8
     * @param aesKey  解密密钥 utf-8
     * @param type    AES 类型
     * @param iv      向量 utf-8, ECB 模式, 传 null
     * @return
     */
    public static byte[] decrypt(byte[] content, byte[] aesKey, AESType type, IvParameterSpec iv) {
        try {
            SecretKeySpec key = getSecretKeySpec(type.getKeySize(), aesKey);
            // 创建密码器
            Cipher cipher = Cipher.getInstance(type.getMode());
            // 初始化
            if (Objects.isNull(iv)) {
                cipher.init(Cipher.DECRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, iv);
            }
            // 解密
            return cipher.doFinal(content);
        } catch (Exception e) {
            log.error("failed to decrypt data.", e);
            throw new RuntimeException("failed to decrypt data.");
        }
    }

    /**
     * 生成秘钥方法1:
     * 生成指定类型的AESkey的长度
     *
     * @param type AES类型
     * @return key 秘钥 utf-8
     */
    public static String createAESKey(AESType type) {
        int length = type.getKeySize() >> 8;
        StringBuilder keySB = new StringBuilder();
        SecureRandom random = new SecureRandom();
        int sourceL = KEY_SOURCE.length();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(sourceL);
            keySB.append(KEY_SOURCE.charAt(index));
        }
        return Base64.encodeBase64String(keySB.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成秘钥方法2
     *
     * @param keySize 秘钥位数
     * @return key 秘钥 utf-8
     */
    public static String generateKey(AESType keySize) {
        SecretKey secretKey = null;
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM_NAME);
            // 初始化秘钥生成器
            keyGen.init(keySize.getKeySize());
            // 生成秘钥
            secretKey = keyGen.generateKey();
        } catch (NoSuchAlgorithmException e) {
            log.error("failed to get keyGen", e);
        }
        // 获取秘钥
        return secretKey == null ? null : Base64.encodeBase64String(secretKey.getEncoded());
    }

    /**
     * 生成 16 位向量 IV: 不够则用0填充
     * @return iv 向量 utf-8
     */
    private static IvParameterSpec generateIV(String iv) {
        if (StringUtils.isBlank(iv)) {
            return null;
        }
        StringBuilder sb = new StringBuilder(IVSize);
        sb.append(iv);
        if (sb.length() > IVSize) {
            sb.setLength(IVSize);
        }
        if (sb.length() < IVSize) {
            while (sb.length() < IVSize) {
                sb.append("0");
            }
        }
        return new IvParameterSpec(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 SecretKeySpec
     * @param keySize 秘钥位数
     * @param aesKey 秘钥 utf-8
     * @return
     * @throws NoSuchAlgorithmException
     */
    private static SecretKeySpec getSecretKeySpec(int keySize, byte[] aesKey) throws NoSuchAlgorithmException {
        KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM_NAME);
        keyGen.init(keySize, new SecureRandom(aesKey));
        SecretKey secretKey = keyGen.generateKey();
        byte[] enCodeFormat = secretKey.getEncoded();
        return new SecretKeySpec(enCodeFormat, ALGORITHM_NAME);
    }

    /**
     * AES秘钥位数
     */
    @AllArgsConstructor
    @Getter
    public enum AESType {
        AES_ECB_128(128, "AES/ECB/PKCS5Padding"),
        AES_ECB_192(192, "AES/ECB/PKCS5Padding"),
        AES_ECB_256(256, "AES/ECB/PKCS5Padding"),
        AES_CBC_128(128, "AES/CBC/PKCS5Padding"),
        AES_CBC_192(192, "AES/CBC/PKCS5Padding"),
        AES_CBC_256(256, "AES/CBC/PKCS5Padding"),
        ;
        public int keySize;
        public String mode;
    }

}
