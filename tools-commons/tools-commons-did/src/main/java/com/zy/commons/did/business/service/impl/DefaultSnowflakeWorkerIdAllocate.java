package com.zy.commons.did.business.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.zy.commons.did.enums.WorkerNodeType;
import com.zy.commons.did.exception.DidException;
import com.zy.commons.did.utils.DataSourceInject;
import com.zy.commons.lang.inject.InjectedBeans;
import com.zy.commons.did.business.entity.WorkerNode;
import com.zy.commons.did.business.mapper.WorkerNodeMapper;
import com.zy.commons.did.business.service.WorkerIdAllocate;
import com.zy.commons.lang.utils.IPTools;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class DefaultSnowflakeWorkerIdAllocate implements WorkerIdAllocate {
    private final DruidDataSource dataSource;
    private final QueryRunner qr;

    /**
     * 在构造器中完成数据源的初始化工作
     */
    public DefaultSnowflakeWorkerIdAllocate() {
        DataSourceInject dataSourceInject = InjectedBeans.getSingletonBean(DataSourceInject.class);
        if (Objects.isNull(dataSourceInject) || Objects.isNull(dataSourceInject.getDataSource())) {
            throw new DidException("cannot get dataSource for DefaultSnowflakeWorkerIdAllocate");
        }
        this.dataSource = dataSourceInject.getDataSource();
        this.qr = new QueryRunner(this.dataSource);
    }

    /**
     * 切记, 这三个 SQL 操作, 要放到同一个 事务中处理
     *
     * @param workerIdBits
     * @param tableName
     * @param dbType
     * @return
     */
    @Override
    public long allocateWorkerId(int workerIdBits, String tableName, String dbType) {
        WorkerNode workerNode = buildWorkerNode();
        workerNode.setDbType(dbType);
        workerNode.setTableName(tableName);
        workerNode.setLaunchDate(new Date());
        Long workerId;
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            // 1.关闭事务自动提交功能
            connection.setAutoCommit(false);

            // 2.新增一个 workerNode
            WorkerNodeMapper.getInstance().addWorkerNode(workerNode, this.qr, connection);

            // 3.查询出新增的 workerNode 对应的 id, 作为 workerId.
            workerId = WorkerNodeMapper.getInstance().queryWorkerIdByIP(workerNode, this.qr, connection);
            workerNode.setId(workerId);
            if (Objects.isNull(workerId)) {
                throw new DidException("failed to queryWorkerIdByIP, params is: " + workerNode);
            }

            // 4.删除新增的 workerNode 对应的记录, 以防下次机器重启时, 读取到旧的 workerId.
            WorkerNodeMapper.getInstance().deleteWorkerNode(workerNode, this.qr, connection);

            // 5.提交事务
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException e) {
            // 回滚事务
            DbUtils.rollbackAndCloseQuietly(connection);
            throw new DidException(e);
        } finally {
            // fixme 6.关闭 Druid 连接池, 此处只在初始化时需要, 用完即可关闭
            // this.dataSource.close();
        }
        return workerId;
    }

    private WorkerNode buildWorkerNode() {
        WorkerNode workerNode = new WorkerNode();
        workerNode.setType(WorkerNodeType.ACTUAL.getCode());
        String ip = IPTools.findLocalIP();
        if (Objects.equals(IPTools.LOCALHOST_V4, ip)) {
            throw new DidException("cannot find local app's hostname.");
        }
        workerNode.setHostName(ip);
        // todo 注意下这里的 取值是否 ok
        workerNode.setPort(System.currentTimeMillis() + "-" + RandomUtils.nextInt());
        return workerNode;
    }

}
