package com.zy.commons.lang.security;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Base64;
@Slf4j
/**
 * https://docs.oracle.com/javase/8/docs/technotes/guides/security/StandardNames.html#Cipher
 */
public class Base64Tools {

    //////////////////////////////////////////////=======jdk8版本======///////////////////////////////////////////////////////////////

    /**
     * 在Java 8中，Base64编码已经成为Java类库的标准。
     * Java 8 内置了 Base64 编码的编码器和解码器。
     *
     * Base64工具类提供了一套静态方法获取下面三种BASE64编解码器：
     *  --Basic：输出被映射到一组字符A-Za-z0-9+/，编码不添加任何行标，输出的解码仅支持A-Za-z0-9+/。
     *  --URL：输出映射到一组字符A-Za-z0-9+_，输出URL和文件名安全。
     *  --MIME：输出隐射到MIME友好格式。输出每行不超过76字符，并且使用'\r'并跟随'\n'作为分割。编码输出最后没有行分割。
     *
     * jdk8的编码结果不包含换行
     * jdk8无法解码包含换行的编码结果
     *
     */

    /**
     * 将文件转为base64字符串
     * @param path 文件路径
     * @return
     */
    public static String fileToBase64(String path) {
        File file = new File(path);
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[(int) file.length()];
            bis.read(bytes);
            return Base64.getMimeEncoder().encodeToString(bytes);
        } catch (IOException e) {
            log.error("failed to encode file", e);
        }
        return null;
    }

    /**
     * 将base64字符串转为文件
     * @param base64 待转换的base64字符串
     * @param path 需要生成的文件路径
     * @return
     */
    public static boolean base64ToFile(String base64, String path) {
        File file = new File(path);
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytes = Base64.getMimeDecoder().decode(base64);
            bos.write(bytes);
            bos.flush();
            return true;
        } catch (IOException e) {
            log.error("failed to decode file", e);
        }
        return false;
    }

}
