package com.zy.commons.did.core;

import com.zy.commons.did.exception.DidException;

/**
 * 分布式 ID 生成器
 */
public interface DidGenerator {

    String getDid() throws DidException;

    String parseDid(String did);
}
