package com.zy.commons.lang;

import com.zy.commons.lang.utils.FileUtils;
import org.junit.Test;

import java.io.IOException;

public class FileUtilsTest {
    @Test
    public void fn01() throws IOException {
        FileUtils.zip("/证书", "/b.zip");
        // FileUtils.zip("/B+树.png", "/b.zip");
        FileUtils.unzip("/b.zip", "/zzzz");
    }
}
