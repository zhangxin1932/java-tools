package com.zy.tools.retry.v2;

import com.zy.tools.retry.RpcService;

public class TeacherServiceImplRetryV2 implements ITeacherService {
    @Override
    public void teach(String subjectName) {
        RpcService.getInstance().teach(subjectName);
    }
}
