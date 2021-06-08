package com.zy.tools.undefined.retry.v3;

import com.zy.tools.undefined.retry.RpcService;

public class ProgrammerServiceImpl {
    public void program(String language) {
        RpcService.getInstance().program(language);
    }
}

