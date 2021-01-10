package com.zy.tools.retry.v4;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Component
@Aspect
@Order(Ordered.LOWEST_PRECEDENCE - 3)
public class RetryAspect {

    @Around(value = "@annotation(retryAnno)")
    public Object process(ProceedingJoinPoint point, RetryAnno retryAnno) throws Throwable {
        RetryAnno retry = Objects.nonNull(retryAnno) ? retryAnno : ((MethodSignature) point.getSignature()).getMethod().getAnnotation(RetryAnno.class);
        if (Objects.isNull(retry)) {
            return point.proceed();
        }
        int times = retry.times();
        if (times <= 1) {
            return point.proceed();
        }
        int i = 0;
        while (i < times) {
            try {
                return point.proceed();
            } catch (Throwable e) {
                i++;
                System.out.println(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now()) + " retry times is ======>>> " + i);
                if (i >= times) {
                    throw new RuntimeException(e);
                }
                TimeUnit.MILLISECONDS.sleep(retry.internal());
            }
        }
        return null;
    }
}

