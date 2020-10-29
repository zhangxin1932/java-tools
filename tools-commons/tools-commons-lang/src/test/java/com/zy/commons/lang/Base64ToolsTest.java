package com.zy.commons.lang;

import com.zy.commons.lang.security.Base64Tools;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static com.zy.commons.lang.security.Base64Tools.base64ToFile;
import static com.zy.commons.lang.security.Base64Tools.fileToBase64;

public class Base64ToolsTest {

    @Test
    public void fn00() {
        String encode = Base64.getUrlEncoder().encodeToString("https:// www.haha:heihei".getBytes(StandardCharsets.UTF_8));
        System.out.println(encode);
        String decode = new String(Base64.getUrlDecoder().decode(encode));
        System.out.println(decode);
    }

    /**
     * base64-----> basic
     */
    @Test
    public void fn01() {
        String basicEncode = Base64.getEncoder().encodeToString("java8 base64 basic".getBytes(Charset.forName("utf-8")));
        System.out.println("java8 basicEncode>>>>>>>>>>>>" + basicEncode);
        String basicDecode = new String(Base64.getDecoder().decode(basicEncode), Charset.forName("utf-8"));
        System.out.println("java8 basicDecode>>>>>>>>>>>>" + basicDecode);
    }

    /**
     * base64-----> url
     */
    @Test
    public void fn02() {
        String urlEncode = Base64.getUrlEncoder().encodeToString("java8 base64 http://www.baidu.com?name=tom".getBytes(Charset.forName("utf-8")));
        System.out.println("java8 urlEncode>>>>>>>>>>>>" + urlEncode);
        String urlDecode = new String(Base64.getUrlDecoder().decode(urlEncode), Charset.forName("utf-8"));
        System.out.println("java8 urlDecode>>>>>>>>>>>>" + urlDecode);
    }

    /**
     * base64-----> mime
     * MIME编码器会使用基本的字母数字产生BASE64输出，而且对MIME格式友好：
     *  每一行输出不超过76个字符，而且每行以“\r\n”符结束
     */
    @Test
    public void fn03() {
        String fileToBase64 = fileToBase64("/helloworld.txt");
        System.out.println("java8 fileEncode>>>>>>>>>>>>" + fileToBase64);
        boolean base64ToFile = base64ToFile(fileToBase64, "/hi.txt");
        System.out.println(base64ToFile+"========");
    }


    /**
     * base64读写文件
     * @throws IOException
     */
    @Test
    public void fn04() throws IOException {
        String src = "This is the content of any resource read from somewhere" +
                " into a stream. This can be text, image, video or any other stream.";

        // 编码器封装OutputStream, 文件/tmp/buff-base64.txt的内容是BASE64编码的形式
        try (OutputStream os = Base64.getEncoder().wrap(new FileOutputStream("/tmp/buff-base64.txt"))) {
            os.write(src.getBytes("utf-8"));
        }

        // 解码器封装InputStream, 以及以流的方式解码, 无需缓冲
        // is being consumed. There is no need to buffer the content of the file just for decoding it.
        try (InputStream is = Base64.getDecoder().wrap(new FileInputStream("/tmp/buff-base64.txt"))) {
            int len;
            byte[] bytes = new byte[100];
            while ((len = is.read(bytes)) != -1) {
                System.out.print(new String(bytes, 0, len, "utf-8"));
            }
        }
    }


    //////////////////////////////////////////////=======spring版本======///////////////////////////////////////////////////////////////

    /**
     * oorg.springframework.util类也可以进行编码和解码
     * 编码之后或解码之前去除换行符
     * 编码和解码使用相同的jdk版本
     */
    @Test
    public void fn05() {
        String encodeToString = Base64Utils.encodeToString("hello".getBytes(Charset.forName("utf-8")));
        String decode = new String(Base64Utils.decodeFromString(encodeToString), Charset.forName("utf-8"));
        System.out.println(encodeToString);
        System.out.println(decode);
    }

    //////////////////////////////////////////////=======apache版本======///////////////////////////////////////////////////////////////
    /**
     * org.apache.com.zy.commons.codec.binary.Base64类也可以进行编码和解码
     * 编码之后或解码之前去除换行符
     * 编码和解码使用相同的jdk版本
     */
    @Test
    public void fn06() {
        /*String encodeBase64String = org.apache.commons.codec.binary.Base64.encodeBase64String("hello".getBytes(Charset.forName("utf-8")));
        String decode = new String(org.apache.commons.codec.binary.Base64.decodeBase64(encodeBase64String), Charset.forName("utf-8"));
        System.out.println(encodeBase64String);
        System.out.println(decode);*/
    }
}
