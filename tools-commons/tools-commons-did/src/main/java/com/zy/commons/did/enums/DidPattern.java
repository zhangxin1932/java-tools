package com.zy.commons.did.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DidPattern {
    /**
     * 表示基于雪花算法的分布式 ID
     */
    snowflake("1"),
    /**
     * 表示基于号段模式算法的分布式 ID
     */
    numberSegment("2"),

    ;
    private String code;
}
