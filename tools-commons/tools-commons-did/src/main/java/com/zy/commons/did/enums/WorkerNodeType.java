package com.zy.commons.did.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WorkerNodeType {
    /**
     * 实际软件
     */
    ACTUAL(1),
    /**
     * DOCKER 容器部署的软件
     */
    DOCKER(2),
    ;
    private Integer code;
}
