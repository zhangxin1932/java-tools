package com.zy.tools.undefined.retry.v4;

import com.zy.tools.undefined.retry.RpcService;
import org.springframework.stereotype.Service;

@Service
public class CommentServiceImpl {
    @RetryAnno(times = 3, internal = 2000)
    public void comment(String foodName) {
        RpcService.getInstance().comment(foodName);
    }
}

