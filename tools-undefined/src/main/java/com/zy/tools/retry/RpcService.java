package com.zy.tools.retry;

import java.util.Objects;

public class RpcService {
    private static final RpcService RPC_SERVICE = new RpcService();

    public static RpcService getInstance() {
        return RPC_SERVICE;
    }

    /**
     * 配合测试 v1 版本的重试: 原始的代码侵入性高的
     *
     * @param id
     * @return
     */
    public String getName(Long id) {
        throw new RuntimeException(String.format("failed to get employee's %s name...", id));
        // return String.format("%s->%s", id, UUID.randomUUID().toString());
    }

    /**
     * 配合测试 v2 版本的重试: 基于 jdk 动态代理
     *
     * @param subjectName
     */
    public void teach(String subjectName) {
        throw new RuntimeException(String.format("failed to teach subject: %s ...", subjectName));
    }

    /**
     * 配合测试 v3版本的重试: 基于 cglib 动态代理
     *
     * @param language
     */
    public void program(String language) {
        throw new RuntimeException(String.format("%s programmer failed to code ...", language));
    }

    /**
     * 配合测试 v4 版本的重试: 基于 AOP 实现, 简化开发
     *
     * @param foodName
     */
    public void comment(String foodName) {
        throw new RuntimeException(String.format("failed to comment %s ...", foodName));
    }

    /**
     * 配合测试 v5 版本的重试: 基于 spring-retry 框架实现
     *
     * @param address
     * @return
     */
    public boolean nearby(String address) {
        if (Objects.isNull(address)) {
            throw new NullPointerException(String.format("failed to judge empty address is nearby, ...", address));
        }
        throw new RuntimeException(String.format("failed to judge address %s is nearby ...", address));
    }
}

