package com.zy.commons.did.buffer;

import com.zy.commons.did.utils.PaddingAtomicLong;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.concurrent.atomic.AtomicLong;

@Slf4j
public class RingBuffer {
    private static final int START_POINT = -1;
    private static final long CAN_PUT_FLAG = 0L;
    private static final long CAN_TAKE_FLAG = 1L;
    private static final int DEFAULT_PADDING_PERCENT = 50;
    @Getter
    private final int bufferSize;
    private final long indexMask;
    private final long[] slots;
    private final PaddingAtomicLong[] flags;
    private final AtomicLong tail;
    private final AtomicLong cursor;
    private final long paddingThreshold;
    @Setter
    private RejectedPutBufferHandler rejectedPutBufferHandler;
    @Setter
    private RejectedTakeBufferHandler rejectedTakeBufferHandler;
    @Setter
    private BufferPaddingExecutor bufferPaddingExecutor;

    public RingBuffer(int bufferSize) {
        this(bufferSize, 50);
    }

    public RingBuffer(int bufferSize, int paddingFactor) {
        this.bufferSize = bufferSize;
        this.tail = new PaddingAtomicLong(-1L);
        this.cursor = new PaddingAtomicLong(-1L);
        this.rejectedPutBufferHandler = RingBuffer.this::discardPutBuffer;
        this.rejectedTakeBufferHandler = RingBuffer.this::exceptionRejectedTakeBuffer;
        Assert.isTrue(bufferSize > 0L, "RingBuffer size must be positive.");
        Assert.isTrue(Integer.bitCount(bufferSize) == 1L, "RingBuffer size must be a power of 2.");
        Assert.isTrue(paddingFactor > 0 && paddingFactor < 100, "RingBuffer size must be in 0-100.");
        this.indexMask = bufferSize - 1;
        this.slots = new long[bufferSize];
        this.flags = this.initFlags(bufferSize);
        this.paddingThreshold = bufferSize * paddingFactor / 100;
    }

    public synchronized boolean put(long did) {
        long currentTail = this.tail.get();
        long currentCursor = this.cursor.get();
        long distance = currentTail - (currentCursor == -1L ? 0L : currentCursor);
        if (distance == (this.bufferSize - 1)) {
            this.rejectedPutBufferHandler.rejectPutBuffer(this, did);
            return false;
        } else {
            int nextTailIndex = this.calSlotIndex(currentTail + 1L);
            if (this.flags[nextTailIndex].get() != 0L) {
                this.rejectedPutBufferHandler.rejectPutBuffer(this, did);
                return false;
            } else {
                this.slots[nextTailIndex] = did;
                this.flags[nextTailIndex].set(1L);
                this.tail.incrementAndGet();
                return true;
            }
        }
    }

    public long take() {
        long currentCursor = this.cursor.get();
        long nextCursor = currentCursor == this.tail.get() ? currentCursor : this.cursor.incrementAndGet();
        Assert.isTrue(nextCursor >= currentCursor, "Cursor cannot be back.");
        long currentTail = this.tail.get();
        if (currentTail - nextCursor < this.paddingThreshold) {
            log.info("reach the padding threshold:{}, tail:{}, cursor:{}, rest:{}.", this.paddingThreshold, currentTail, nextCursor, currentTail - nextCursor);
            this.bufferPaddingExecutor.asyncPadding();
        }
        if (currentCursor == nextCursor) {
            this.rejectedTakeBufferHandler.rejectTakeBuffer(this);
        }
        int nextCursorIndex = this.calSlotIndex(nextCursor);
        Assert.isTrue(this.flags[nextCursorIndex].get() == 1L, "Cursor not in can take status");
        long did = this.slots[nextCursorIndex];
        this.flags[nextCursorIndex].set(0L);
        return did;
    }

    protected int calSlotIndex(long sequence) {
        return (int) (sequence & this.indexMask);
    }

    protected void discardPutBuffer(RingBuffer ringBuffer, Long did) {
        log.warn("rejectedPutBuffer for conf:[{}], [{}].", did, ringBuffer);
    }

    protected void exceptionRejectedTakeBuffer(RingBuffer ringBuffer) {
        throw new RuntimeException("Rejected take buffer." + ringBuffer);
    }

    private PaddingAtomicLong[] initFlags(int bufferSize) {
        Assert.isTrue(bufferSize > 0, "bufferSize must be positive.");
        PaddingAtomicLong[] flags = new PaddingAtomicLong[bufferSize];
        for (int i = 0; i < bufferSize; i++) {
            flags[i] = new PaddingAtomicLong(0L);
        }
        return flags;
    }

    public long getTail() {
        return this.tail.get();
    }

    public long getCursor() {
        return this.cursor.get();
    }

    public int getBufferSize() {
        return this.bufferSize;
    }
}
