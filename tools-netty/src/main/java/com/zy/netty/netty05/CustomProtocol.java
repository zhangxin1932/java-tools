package com.zy.netty.netty05;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义协议
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomProtocol {
    private int length;
    private byte[] content;
}
