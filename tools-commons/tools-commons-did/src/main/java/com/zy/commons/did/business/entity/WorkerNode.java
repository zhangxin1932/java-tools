package com.zy.commons.did.business.entity;

import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
public class WorkerNode implements Serializable {
    private static final long serialVersionUID = -755311455494938145L;
    /**
     * workerId: 数据库字段类型, 请设计为 bigint
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
    private String tableName = "worker_node";
    /**
     * 生成 workerId 的数据库类型
     */
    private String dbType = "mysql";
    /**
     * 创建时间: 数据库字段类型, 请设计为 timestamp
     */
    private Date launchDate;
}
