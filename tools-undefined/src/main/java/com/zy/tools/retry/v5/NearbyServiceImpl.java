package com.zy.tools.retry.v5;

import com.zy.tools.retry.RpcService;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class NearbyServiceImpl {

    /**
     * 重试
     * @param address
     * @return
     */
    @Retryable(maxAttempts = 3, value = NullPointerException.class, backoff = @Backoff(delay = 2000L, multiplier = 2))
    public boolean nearby(String address) {
        System.out.println("根据这里调了几次, 可以看到重试了多少次...||||||||||||| >>>>>>>>>>> ||||||||||||||");
        return RpcService.getInstance().nearby(address);
    }

    /**
     * 降级机制: 如果最终仍然失败, 将会调用这里的方法
     * @param e
     * @return
     */
    @Recover
    public boolean degreeNearby(NullPointerException e) {
        return false;
    }
}

