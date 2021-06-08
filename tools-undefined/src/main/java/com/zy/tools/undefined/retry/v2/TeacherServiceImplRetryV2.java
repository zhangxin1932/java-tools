package com.zy.tools.undefined.retry.v2;

import com.zy.tools.undefined.retry.RpcService;

public class TeacherServiceImplRetryV2 implements ITeacherService {
    @Override
    public void teach(String subjectName) {
        RpcService.getInstance().teach(subjectName);
    }
}
