package com.zy.tools.retry.v1;

import com.zy.tools.retry.RpcService;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImplRetryV1 implements IEmployeeService {
    private static final int RETRY_TIMES = 3;
    @Override
    public String getName(Long id) {
        int times = 0;
        while (times < RETRY_TIMES) {
            try {
                return RpcService.getInstance().getName(id);
            } catch (Exception e) {
                times++;
                System.out.println("times ------------> " + times);
                if (times >= RETRY_TIMES) {
                    throw new RuntimeException(e);
                }
            }
        }
        return null;
    }
}
