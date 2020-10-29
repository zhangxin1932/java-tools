package com.zy.netty.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.util.ResourceLeakDetector;
import org.junit.Test;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * netty 框架参考
 * <p>github 上搜索 'netty自定义协议'</p>
 * https://github.com/commonrpc/commonrpc
 * https://github.com/all4you/bitchat
 * https://github.com/twjitm/twjitm-core
 */
public class ByteBufTest {

    private static final String HELLO = "hello";
    private static final String WORLD = "world";

    /**
     * 1.初始化一个池化的 byteBuf >>>>>>>>>>>>>>>>>>>>
     * readerIndex: {0}, writerIndex: {0}, isWritable: {true}, isReadable: {false}, capacity: {4}, maxCapacity: {1024}.
     * 2.调用 writeBytes 方法后 >>>>>>>>>>>>>>>>>>>>
     * readerIndex: {0}, writerIndex: {5}, isWritable: {true}, isReadable: {true}, capacity: {16}, maxCapacity: {1024}.
     * 3.调用 readBytes(int i) 方法后 >>>>>>>>>>>>>>>>>>>>
     * readerIndex: {2}, writerIndex: {5}, isWritable: {true}, isReadable: {true}, capacity: {16}, maxCapacity: {1024}.
     * 4.调用 discardReadBytes() 方法后 >>>>>>>>>>>>>>>>>>>>
     * readerIndex: {0}, writerIndex: {3}, isWritable: {true}, isReadable: {true}, capacity: {16}, maxCapacity: {1024}.
     * 5.调用 clear 方法后 >>>>>>>>>>>>>>>>>>>>
     * readerIndex: {0}, writerIndex: {0}, isWritable: {true}, isReadable: {false}, capacity: {16}, maxCapacity: {1024}.
     */
    @Test
    public void fn01() {
        // FIXME 初始化一个 directBuffer, 初始容量为4, 最大容量设置为 1024, 当超过4不超过1024时, 可扩容
        ByteBuf directBuffer = PooledByteBufAllocator.DEFAULT.directBuffer(4, 1024);
        System.out.println("1.初始化一个池化的 byteBuf >>>>>>>>>>>>>>>>>>>>");
        System.out.println(String.format("readerIndex: {%s}, writerIndex: {%s}, isWritable: {%s}, isReadable: {%s}, capacity: {%s}, maxCapacity: {%s}.", directBuffer.readerIndex(), directBuffer.writerIndex(), directBuffer.isWritable(), directBuffer.isReadable(), directBuffer.capacity(), directBuffer.maxCapacity()));

        System.out.println("2.调用 writeBytes 方法后 >>>>>>>>>>>>>>>>>>>>");
        directBuffer.writeBytes(HELLO.getBytes(StandardCharsets.UTF_8));
        System.out.println(String.format("readerIndex: {%s}, writerIndex: {%s}, isWritable: {%s}, isReadable: {%s}, capacity: {%s}, maxCapacity: {%s}.", directBuffer.readerIndex(), directBuffer.writerIndex(), directBuffer.isWritable(), directBuffer.isReadable(), directBuffer.capacity(), directBuffer.maxCapacity()));

        System.out.println("3.调用 readBytes(int i) 方法后 >>>>>>>>>>>>>>>>>>>>");
        directBuffer.readBytes(2);
        System.out.println(String.format("readerIndex: {%s}, writerIndex: {%s}, isWritable: {%s}, isReadable: {%s}, capacity: {%s}, maxCapacity: {%s}.", directBuffer.readerIndex(), directBuffer.writerIndex(), directBuffer.isWritable(), directBuffer.isReadable(), directBuffer.capacity(), directBuffer.maxCapacity()));

        System.out.println("4.调用 discardReadBytes() 方法后 >>>>>>>>>>>>>>>>>>>>");
        directBuffer.discardReadBytes();
        System.out.println(String.format("readerIndex: {%s}, writerIndex: {%s}, isWritable: {%s}, isReadable: {%s}, capacity: {%s}, maxCapacity: {%s}.", directBuffer.readerIndex(), directBuffer.writerIndex(), directBuffer.isWritable(), directBuffer.isReadable(), directBuffer.capacity(), directBuffer.maxCapacity()));

        System.out.println("5.调用 clear 方法后 >>>>>>>>>>>>>>>>>>>>");
        directBuffer.clear();
        System.out.println(String.format("readerIndex: {%s}, writerIndex: {%s}, isWritable: {%s}, isReadable: {%s}, capacity: {%s}, maxCapacity: {%s}.", directBuffer.readerIndex(), directBuffer.writerIndex(), directBuffer.isWritable(), directBuffer.isReadable(), directBuffer.capacity(), directBuffer.maxCapacity()));
    }

    /**
     *
     * 1. slice 的 zero-copy 操作
     *                            CompositeByteBuf
     *      +-------------------+------------------+------------------+
     *      |               header                 |        body      |
     *      +-------------------+------------------+------------------+
     *                                (composite)
     *      +-------------------+------------------+------------------+
     *      |                          all data                       |
     *      +-------------------+------------------+------------------+
     *
     * 2. slice 的 zero-copy 操作
     *                                  ByteBuf
     *      +-------------------+------------------+------------------+
     *      |                          all data                       |
     *      +-------------------+------------------+------------------+
     *                                (slice)
     *      +-------------------+------------------+------------------+
     *      |               header                 |        body      |
     *      +-------------------+------------------+------------------+
     */
    @Test
    public void fn02() {
        System.out.println("1.copy 数组 >>>>>>>>>>>>>>>>>>>>>> ");
        // 方式1: 希望将一个 byte 数组转换为一个 ByteBuf 对象, 以便于后续的操作, 那么传统的做法是将此 byte 数组拷贝到 ByteBuf 中: array.clone()
        ByteBuf header = Unpooled.copiedBuffer("header".getBytes(StandardCharsets.UTF_8));

        System.out.println("2.zero-copy 数组 combine >>>>>>>>>>>>>>>>>>>>>> ");
        // 虽然看起来 CompositeByteBuf 是由两个 ByteBuf 组合而成的, 不过在 CompositeByteBuf 内部, 这两个 ByteBuf 都是单独存在的, CompositeByteBuf 只是逻辑上是一个整体.
        // addComponents(boolean increaseWriterIndex, ByteBuf... buffers) 第一个参数是 true, 表示当添加新的 ByteBuf 时, 自动递增 CompositeByteBuf 的 writeIndex.
        // compositeByteBuf.addComponents(header, body) 方法的 writeIndex 仍然是0, 因此此时我们就不可能从 compositeByteBuf 中读取到数据,
        CompositeByteBuf compositeByteBuf = Unpooled.compositeBuffer().addComponents(true, Unpooled.copiedBuffer("a".getBytes(StandardCharsets.UTF_8)), Unpooled.copiedBuffer("b".getBytes(StandardCharsets.UTF_8)));

        System.out.println("3.zero-copy 数组 combine >>>>>>>>>>>>>>>>>>>>>> ");
        // 方式2: 也可以使用 Unpooled 的相关方法, 包装这个 byte 数组, 生成一个新的 ByteBuf 实例, 而不需要进行拷贝操作
        // Unpooled.wrappedBuffer 方法来将 bytes 包装成为一个 UnpooledHeapByteBuf 对象, 而在包装的过程中, 是不会有拷贝操作的.
        // 即最后我们生成的生成的 ByteBuf 对象是和 bytes 数组共用了同一个存储空间, 对 bytes 的修改也会反映到 ByteBuf 对象中.
        ByteBuf body = Unpooled.wrappedBuffer("body".getBytes(StandardCharsets.UTF_8));

        // 方式3: 合并两个 ByteBuf, zero-copy
        ByteBuf combinedByteBuf = Unpooled.wrappedBuffer(header, body);

        System.out.println("4.zero-copy 数组 slice >>>>>>>>>>>>>>>>>>>>>> ");
        // 方式1: 不带参数的 slice 方法等同于 buf.slice(buf.readerIndex(), buf.readableBytes()) 调用, 即返回 buf 中可读部分的切片.
        // ByteBuf slice = combinedByteBuf.slice();
        ByteBuf headerBuf = combinedByteBuf.slice(0, 6);
        // 方式2: slice(int index, int length) 方法相对就比较灵活了, 我们可以设置不同的参数来获取到 buf 的不同区域的切片.
        ByteBuf bodyBuf = combinedByteBuf.slice(6, 10);

        // Netty 中使用 FileRegion 实现文件传输的零拷贝, 不过在底层 FileRegion 是依赖于 Java NIO FileChannel.transfer 的零拷贝功能.
        System.out.println("5.zero-copy file --> FileServerHandler 借助 DefaultFileRegion 实现 >>>>>>>>>>>>>>>>>>>>>> ");
    }


    /**
     * 代码中不断中源文件中读取定长数据到 temp 数组中, 然后再将 temp 中的内容写入目的文件,
     * 这样的拷贝操作对于小文件倒是没有太大的影响, 但是如果我们需要拷贝大文件时, 频繁的内存拷贝操作就消耗大量的系统资源了.
     *
     * @param srcFile
     * @param destFile
     */
    private void bioCopyFile(String srcFile, String destFile) {
        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(srcFile));
             BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile))) {
            byte[] bytes = new byte[1024];
            int length;
            while ((length = bis.read(bytes)) != -1) {
                bos.write(bytes, 0, length);
            }
        } catch (Exception e) {
            System.out.println("bioCopyFile error.");
            e.printStackTrace();
        }
    }

    /**
     * FileChannel 可以直接将源文件的内容直接拷贝(transferTo) 到目的文件中,
     * 而不需要额外借助一个临时 buffer, 避免了不必要的内存操作.
     * @param srcFile
     * @param destFile
     */
    private void nioCopyFile(File srcFile, File destFile) {
        try (RandomAccessFile srcRaf = new RandomAccessFile(srcFile, "r");
             RandomAccessFile destRaf = new RandomAccessFile(destFile, "rw")) {
            FileChannel srcFileChannel = srcRaf.getChannel();
            FileChannel destFileChannel = destRaf.getChannel();
            long position = 0;
            long count = srcFileChannel.size();
            srcFileChannel.transferTo(position, count, destFileChannel);
        } catch (Exception e) {
            System.out.println("nioCopyFile error.");
            e.printStackTrace();
        }
    }

    private void loopFile(File srcFile, File destFile) throws IOException {
        Path path = Paths.get(srcFile.toURI());
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {

                return super.visitFile(file, attrs);
            }
        });
    }

    @Test
    public void fn03() {
        // ResourceLeakDetector.setLevel(ResourceLeakDetector.Level.PARANOID);
        for (int i = 0; i < 10000000; i++) {
            ByteBuf hello = PooledByteBufAllocator.DEFAULT.heapBuffer();
            hello.writeBytes("hello".getBytes(StandardCharsets.UTF_8));
            
            ByteBuf world = PooledByteBufAllocator.DEFAULT.heapBuffer();
            world.writeBytes("world".getBytes(StandardCharsets.UTF_8));


            // FIXME 注释掉这里, 观察是否出现
            // buffer.release();
        }
    }

}
