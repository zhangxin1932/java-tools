package com.zy.commons.lang.utils;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 文件的 复制, 流的转换, 参考 jdk7 的 Paths, Files 工具类即可, 不必再使用 apache 的文件工具类
 * https://segmentfault.com/a/1190000020778836?utm_source=tag-newest
 */
@Slf4j
public class FileUtils {
    private FileUtils() {
        throw new RuntimeException("FileUtils can not instantiated.");
    }

    /**
     * 压缩文件或文件夹到指定zip文件
     *
     * @param srcFile    源文件或文件夹
     * @param targetFile 目标zip文件
     * @throws IOException 抛出给调用者处理
     */
    public static void zip(String srcFile, String targetFile) throws IOException {
        try (OutputStream os = new FileOutputStream(targetFile)) {
            zip(srcFile, os);
        }
    }

    /**
     * 压缩文件夹到指定输出流中，可以是本地文件输出流，也可以是web响应下载流
     *
     * @param srcFile      源文件或文件夹
     * @param outputstream 压缩后文件的输出流
     * @throws IOException 抛出给调用者处理
     */
    public static void zip(String srcFile, OutputStream outputstream) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(outputstream);
             ArchiveOutputStream aos = new ZipArchiveOutputStream(bos);
        ) {
            Path start = Paths.get(srcFile);
            if (Files.isDirectory(start)) {
                Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                        ArchiveEntry entry = new ZipArchiveEntry(dir.toFile(), start.relativize(dir).toString());
                        aos.putArchiveEntry(entry);
                        aos.closeArchiveEntry();
                        return super.preVisitDirectory(dir, attrs);
                    }

                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        try (InputStream is = new FileInputStream(file.toFile())) {
                            ArchiveEntry entry = new ZipArchiveEntry(file.toFile(), start.relativize(file).toString());
                            aos.putArchiveEntry(entry);
                            IOUtils.copy(is, aos);
                            aos.closeArchiveEntry();
                        }
                        return super.visitFile(file, attrs);
                    }
                });
            } else {
                try (InputStream is = new FileInputStream(start.toFile())) {
                    ArchiveEntry entry = new ZipArchiveEntry(start.toFile(), start.toString());
                    aos.putArchiveEntry(entry);
                    IOUtils.copy(is, aos);
                    aos.closeArchiveEntry();
                }
            }
        }
    }

    /**
     * 解压zip文件到指定文件夹
     *
     * @param srcZipFileName 源zip文件路径
     * @param destDir        解压后输出路径
     * @throws IOException 抛出给调用者处理
     */
    public static void unzip(String srcZipFileName, String destDir) throws IOException {
        try (InputStream is = new FileInputStream(srcZipFileName)) {
            unzip(is, destDir);
        }
    }

    /**
     * 从输入流中获取zip文件，并解压到指定文件夹
     *
     * @param is      zip文件输入流，可以是本地文件输入流，也可以是web请求上传流
     * @param destDir 解压后输出路径
     * @throws IOException 抛出给调用者处理
     */
    public static void unzip(InputStream is, String destDir) throws IOException {
        try (BufferedInputStream bis = new BufferedInputStream(is);
             ArchiveInputStream ais = new ZipArchiveInputStream(bis);
        ) {
            // 前置条件, 如果文件夹存在, 则删除文件夹, 然后重新创建新的文件夹路径
            Path path = Paths.get(destDir);
            if (path.toFile().exists()) {
                Files.walk(path).sorted(Comparator.reverseOrder()).map(Path::toFile).peek(file -> {
                    log.info("beginning to delete file [{}]." + file.getName());
                }).forEach(File::delete);
            }
            path.toFile().mkdirs();

            // 开始进行解压
            ArchiveEntry entry;
            while (Objects.nonNull(entry = ais.getNextEntry())) {
                if (ais.canReadEntryData(entry)) {
                    File file = Paths.get(destDir, entry.getName()).toFile();
                    if (entry.isDirectory()) {
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                    } else {
                        try (OutputStream os = new FileOutputStream(file)) {
                            IOUtils.copy(ais, os);
                        }
                    }
                } else {
                    log.warn("file [{}] cannot be read.", entry.getName());
                }
            }
        }
    }

    public static void main(String[] args) {
        write("E:\\config.properties", "properties");
    }

    public static void write(String absPath, String fileSuffix) {
        List<String> files;
        try {
            files = Files.walk(Paths.get(absPath))
                    .map(Path::toString)
                    .filter(Objects::nonNull)
                    .filter(absFile -> StringUtils.endsWith(absFile, fileSuffix))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(String.format("failed to get files, absPath: {%s}, fileSuffix: {%s}.", absPath, fileSuffix), e);
        }

        if (CollectionUtil.isEmpty(files)) {
            return;
        }

        files.forEach(file -> {
            try {
                List<String> allLines = Files.readAllLines(Paths.get(file), StandardCharsets.UTF_8);
                if (CollectionUtil.isEmpty(allLines)) {
                    return;
                }
                List<String> newLines = new ArrayList<>();
                allLines.forEach(line -> {
                    if (StringUtils.isBlank(line)) {
                        newLines.add(line);
                        return;
                    }

                    // 注意转义符
                    if (line.contains("\\")) {
                        line = line.replace("\\", "");
                    }

                    // 注意 Base64 加密时, 尾部可能有 = 号, 所以不用 String 的 split 方法
                    int i = line.indexOf("=");
                    // FIXME 这里再确认下当 = 为最后一位时, 这样处理是否合适
                    if (i == -1 || i == line.length() - 1) {
                        newLines.add(line);
                        return;
                    }
                    String k = StringUtils.substring(line, 0, i);
                    String v = StringUtils.substring(line, i + 1, line.length());
                    if (StringUtils.startsWith(v, "prefixStr")) {
                        line = k + "=" + StringUtils.replaceOnce(v, "prefixStr", "newPrefixStr");
                    }
                    newLines.add(line);
                });
                Files.write(Paths.get(file + ".bak"), newLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(String.format("failed to write from file: {%s} to newFile: {%s}.", file, file), e);
            }
        });
    }
}
