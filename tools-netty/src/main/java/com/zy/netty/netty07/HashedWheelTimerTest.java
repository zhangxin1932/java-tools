package com.zy.netty.netty07;

import io.netty.util.HashedWheelTimer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class HashedWheelTimerTest {

    private static final HashedWheelTimer timer = new HashedWheelTimer(2L, TimeUnit.SECONDS, 2);

    public static void main(String[] args) {
        RetryTask retryTask = new RetryTask(3, 10);
        timer.newTimeout(retryTask, 5L, TimeUnit.SECONDS);
    }

    private static class RetryTask implements TimerTask {
        private final int retries;
        private final long tick;
        private int retryTimes = 1;

        private RetryTask(int retries, long tick) {
            this.retries = retries;
            this.tick = tick;
        }

        @Override
        public void run(Timeout timeout) throws Exception {
            try {
                System.out.println("第" + retryTimes + "次重试");
                throw new RuntimeException("...");
                // do task
            } catch (Throwable e) {
                retryTimes++;
                if (retryTimes > retries) {
                    // log.error
                } else {
                    rePutTimeout(timeout);
                }
            }
        }

        private void rePutTimeout(Timeout timeout) {
            if (Objects.isNull(timeout)) {
                return;
            }
            Timer timer = timeout.timer();
            timer.newTimeout(timeout.task(), tick, TimeUnit.SECONDS);
        }
    }
}
