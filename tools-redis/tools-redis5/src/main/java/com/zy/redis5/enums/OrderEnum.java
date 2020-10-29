package com.zy.redis5.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderEnum {
    /**
     * 待支付
     */
    one(1),
    /**
     * 支付完成
     */
    two(2),
    /**
     * 已取消
     */
    three(3),
    /**
     * 待收货
     */
    four(4),
    /**
     * 已收货
     */
    five(5),
    ;
    private int status;
}
