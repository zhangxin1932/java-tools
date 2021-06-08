package com.zy.tools.undefined.undefined.flowcontrol;

import com.google.common.util.concurrent.RateLimiter;

import java.util.concurrent.Callable;

/**
 * 流控处理方案: google简易版
 */
public class RateLimter implements Callable<Void> {

    private static final RateLimiter RATE_LIMITER = RateLimiter.create(10);

    @Override
    public Void call() throws Exception {
        // 流控
        RATE_LIMITER.acquire();
        // 业务处理逻辑
        return null;
    }
}
