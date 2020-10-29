package com.zy.commons.did.buffer;

public interface RejectedPutBufferHandler {
    void rejectPutBuffer(RingBuffer ringBuffer, long did);
}
