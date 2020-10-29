package com.zy.tools.retry.v3;

import com.zy.tools.retry.RpcService;

public class ProgrammerServiceImpl {
    public void program(String language) {
        RpcService.getInstance().program(language);
    }
}

