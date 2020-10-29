package com.zy.tools.undefined;

import com.zy.tools.retry.v1.IEmployeeService;
import com.zy.tools.retry.v2.ITeacherService;
import com.zy.tools.retry.v2.JdkProxy;
import com.zy.tools.retry.v2.TeacherServiceImplRetryV2;
import com.zy.tools.retry.v3.CglibProxy;
import com.zy.tools.retry.v3.ProgrammerServiceImpl;
import com.zy.tools.retry.v4.CommentServiceImpl;
import com.zy.tools.retry.v5.NearbyServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableRetry
public class RetryServiceImplTest {
    @Autowired
    private IEmployeeService employeeService;
    @Autowired
    private CommentServiceImpl commentService;
    @Autowired
    private NearbyServiceImpl nearbyService;
    /**
     * 测试 v1 版本的重试: 原始的代码侵入性高的
     * @return
     */
    @Test
    public void fn01() {
        System.out.println(employeeService.getName(1L));
    }
    /**
     * 测试 v2 版本的重试: 基于 jdk 动态代理
     */
    @Test
    public void fn02() {
        ITeacherService proxyTeacherService = (ITeacherService) JdkProxy.getProxy(new TeacherServiceImplRetryV2());
        proxyTeacherService.teach("english");
    }
    /**
     * 测试 v3版本的重试: 基于 cglib 动态代理
     */
    @Test
    public void fn03() {
        ProgrammerServiceImpl programmerService = new CglibProxy().getProxy(ProgrammerServiceImpl.class);
        programmerService.program("php");
    }
    /**
     * 测试 v4 版本的重试: 基于 AOP 实现, 简化开发
     */
    @Test
    public void fn04() {
        commentService.comment("banana");
    }
    /**
     * 测试 v5 版本的重试: 基于 spring-retry 框架实现
     */
    @Test
    public void fn05() {
        System.out.println("-----------------------------");
        System.out.println(nearbyService.nearby(null));
        System.out.println("-----------------------------");
    }
}