package com.zy.commons.did.business.service.impl;

import com.alibaba.druid.pool.DruidDataSource;
import com.zy.commons.did.business.entity.NumberSegment;
import com.zy.commons.did.business.mapper.NumberSegmentMapper;
import com.zy.commons.did.business.service.NumberSegmentAllocate;
import com.zy.commons.did.enums.WorkerNodeType;
import com.zy.commons.did.exception.DidException;
import com.zy.commons.did.utils.DataSourceInject;
import com.zy.commons.lang.inject.InjectedBeans;
import com.zy.commons.lang.utils.IPTools;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.lang3.RandomUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

public class DefaultNumberSegmentAllocate implements NumberSegmentAllocate {
    private final DruidDataSource dataSource;
    private final QueryRunner qr;

    /**
     * 在构造器中完成数据源的初始化工作
     */
    public DefaultNumberSegmentAllocate() {
        DataSourceInject dataSourceInject = InjectedBeans.getSingletonBean(DataSourceInject.class);
        if (Objects.isNull(dataSourceInject) || Objects.isNull(dataSourceInject.getDataSource())) {
            throw new DidException("cannot get dataSource for DefaultNumberSegmentAllocate");
        }
        this.dataSource = dataSourceInject.getDataSource();
        this.qr = new QueryRunner(this.dataSource);
    }

    @Override
    public long numberSegmentAllocate(String tableName, String dbType) {
        NumberSegment numberSegment = buildNumberSegment();
        numberSegment.setDbType(dbType);
        numberSegment.setTableName(tableName);
        Long segmentId;
        Connection connection = null;
        try {
            connection = this.dataSource.getConnection();
            // 1.关闭事务自动提交功能
            connection.setAutoCommit(false);

            // 2.新增一个 workerNode
            NumberSegmentMapper.getInstance().addNumberSegment(numberSegment, this.qr, connection);

            // 3.查询出新增的 workerNode 对应的 id, 作为 workerId.
            segmentId = NumberSegmentMapper.getInstance().queryNumberSegmentByIP(numberSegment, this.qr, connection);
            numberSegment.setId(segmentId);
            if (Objects.isNull(segmentId)) {
                throw new DidException("failed to queryNumberSegmentByIP, params is: " + numberSegment);
            }

            // 4.删除新增的 workerNode 对应的记录, 以防下次机器重启时, 读取到旧的 segmentId.
            NumberSegmentMapper.getInstance().deleteNumberSegment(numberSegment, this.qr, connection);

            // 5.提交事务
            DbUtils.commitAndCloseQuietly(connection);
        } catch (SQLException e) {
            // 回滚事务
            DbUtils.rollbackAndCloseQuietly(connection);
            throw new DidException(e);
        }
        return segmentId;
    }

    private NumberSegment buildNumberSegment() {
        NumberSegment numberSegment = new NumberSegment();
        numberSegment.setType(WorkerNodeType.ACTUAL.getCode());
        String ip = IPTools.findLocalIP();
        if (Objects.equals(IPTools.LOCALHOST_V4, ip)) {
            throw new DidException("cannot find local app's hostname.");
        }
        numberSegment.setHostName(ip);
        // todo 注意下这里的 取值是否 ok
        numberSegment.setPort(System.currentTimeMillis() + "-" + RandomUtils.nextInt());
        return numberSegment;
    }

}
