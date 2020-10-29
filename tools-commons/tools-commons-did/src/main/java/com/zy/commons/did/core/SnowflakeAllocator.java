package com.zy.commons.did.core;

import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;
@Getter
@ToString
public final class SnowflakeAllocator {
    public static final int TOTAL_BITS = 64;
    private int headBits = 1;
    private final int timestampBits;
    private final int dataCenterIdBits;
    private final int workerIdBits;
    private final int sequenceBits;
    private final long maxDeltaSeconds;
    private final long maxDataCenterId;
    private final long maxWorkerId;
    private final long maxSequence;
    private final int timestampLeftShift;
    private final int dataCenterIdLeftShift;
    private final int workerIdLeftShift;

    public SnowflakeAllocator(final int timestampBits, final int dataCenterIdBits, final int workerIdBits, final int sequenceBits) {
        int allocatedTotalBits = this.headBits + timestampBits + dataCenterIdBits + workerIdBits + sequenceBits;
        Assert.isTrue(allocatedTotalBits == TOTAL_BITS, String.format("allocated bits is not equals %s.", TOTAL_BITS));
        this.timestampBits = timestampBits;
        this.dataCenterIdBits = dataCenterIdBits;
        this.workerIdBits = workerIdBits;
        this.sequenceBits = sequenceBits;
        // 最大支持的距离基准时间的差值, 代表当前时间距离基准时间的差值(单位s)(max: 2147483648  68年)
        this.maxDeltaSeconds = ~(-1L << timestampBits);
        // 最大支持数据中心节点数
        this.maxDataCenterId = ~(-1L << dataCenterIdBits);
        // 最大支持机器节点数, 代表当前id生成器所在机器的唯一标识(max: 1048576)
        this.maxWorkerId = ~(-1L << workerIdBits);
        // 最大支持序列号数 4096, 代表当前机器1s内可生成的序列号(max: 4096)
        this.maxSequence = ~(-1L << sequenceBits);
        // timestamp 左移 dataCenterIdBits + workerIdBits + sequenceBits 位, 默认为  0 + 20 + 12 位
        this.timestampLeftShift = dataCenterIdBits + workerIdBits + sequenceBits;
        // dataCenterId 左移 workerIdBits + sequenceBits 位, 默认为 20 + 12 位
        this.dataCenterIdLeftShift = workerIdBits + sequenceBits;
        // workerId 左移 sequenceBits 位, 默认为 12 位
        this.workerIdLeftShift = sequenceBits;
    }

    public long allocate(long deltaSeconds, long dataCenterId, long workerId, long sequence) {
        return dataCenterId >= 0L ? deltaSeconds << this.timestampLeftShift | dataCenterId << this.dataCenterIdLeftShift | workerId << this.workerIdLeftShift | sequence : deltaSeconds << this.timestampLeftShift | workerId << this.workerIdLeftShift | sequence;
    }
}
