package com.zy.commons.did.buffer;

import com.zy.commons.did.utils.PaddingAtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class BufferPaddingExecutor {
    private final AtomicBoolean running;
    private final PaddingAtomicLong lastSecond;
    private final RingBuffer ringBuffer;
    private final BufferedDidProvider didProvider;
    private final ExecutorService bufferedPaddingExecutor;
    private final ScheduledExecutorService bufferedPaddingScheduleExecutor;
    private long scheduledInterval;

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedDidProvider didProvider, boolean usingSchedule) {
        this.ringBuffer = ringBuffer;
        this.didProvider = didProvider;
        this.scheduledInterval = 300L;
        this.running = new AtomicBoolean(false);
        this.lastSecond = new PaddingAtomicLong(TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis()));
        // todo 这里评估下, 在这里的构造器中创建线程池是否合适
        int cpuCore = Runtime.getRuntime().availableProcessors();
        this.bufferedPaddingExecutor = Executors.newFixedThreadPool(cpuCore * 2, new BasicThreadFactory.Builder().namingPattern("RingBufferPaddingExecutor").daemon(true).build());
        if (usingSchedule) {
            this.bufferedPaddingScheduleExecutor = Executors.newSingleThreadScheduledExecutor(new BasicThreadFactory.Builder().namingPattern("RingBufferPaddingScheduleExecutor").daemon(true).build());
        } else {
            this.bufferedPaddingScheduleExecutor = null;
        }
    }

    public BufferPaddingExecutor(RingBuffer ringBuffer, BufferedDidProvider didProvider) {
        this(ringBuffer, didProvider, true);
    }

    public void start() {
        if (Objects.nonNull(this.bufferedPaddingScheduleExecutor)) {
            this.bufferedPaddingScheduleExecutor.scheduleWithFixedDelay(BufferPaddingExecutor.this::paddingBuffer, this.scheduledInterval, this.scheduledInterval, TimeUnit.SECONDS);
        }
    }

    public void shutdown() {
        if (!this.bufferedPaddingExecutor.isShutdown()) {
            this.bufferedPaddingExecutor.shutdown();
        }
        if (this.bufferedPaddingScheduleExecutor != null && !this.bufferedPaddingScheduleExecutor.isShutdown()) {
            this.bufferedPaddingScheduleExecutor.shutdown();
        }
    }

    public boolean isRunning() {
        return this.running.get();
    }

    public void asyncPadding() {
        this.bufferedPaddingExecutor.submit(BufferPaddingExecutor.this::paddingBuffer);
    }

    public void paddingBuffer() {
        log.info("ready to padding buffer lastSecond:{}.", this.lastSecond.get(), this.ringBuffer);
        if (!this.running.compareAndSet(false, true)) {
            log.info("Padding buffer is still running, {}.", this.ringBuffer);
        } else {
            boolean isFullRingBuffer = false;
            while (!isFullRingBuffer) {
                List<Long> didList = this.didProvider.provide(this.lastSecond.incrementAndGet());
                for (Long did : didList) {
                    isFullRingBuffer = !this.ringBuffer.put(did);
                    if (isFullRingBuffer) {
                        break;
                    }
                }
            }
            this.running.compareAndSet(true, false);
            log.info("End to padding buffer lastSecond: {}, {}.", this.lastSecond.get(), this.ringBuffer);
        }
    }

    public void setScheduledInterval(long scheduledInterval) {
        Assert.isTrue(scheduledInterval > 0L, "scheduledInterval must positive.");
        this.scheduledInterval = scheduledInterval;
    }
}
