package com.zy.commons.did.business.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class NumberSegment implements Serializable {
    private static final long serialVersionUID = -8076566201085362366L;
    /**
     * numberSegment: 数据库字段类型, 请设计为 bigint
     */
    private Long id;
    /**
     * 主机名, 即 ip
     */
    private String hostName;
    /**
     * 随机端口, 暂时无用
     */
    private String port;
    /**
     * 是实际软件或 docker 容器中
     */
    private Integer type;
    /**
     * 表名
     */
    private String tableName = "number_segment";
    /**
     * 生成 号段 id 的数据库类型
     */
    private String dbType = "mysql";
}
