package com.zy.commons.did.business.mapper;

import com.zy.commons.did.business.entity.WorkerNode;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class WorkerNodeMapper {
    private static final WorkerNodeMapper INSTANCE = new WorkerNodeMapper();

    public static WorkerNodeMapper getInstance() {
        return INSTANCE;
    }

    public void addWorkerNode(WorkerNode workerNode, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_ADD = "INSERT INTO %s (host_name, port, type, launch_date) VALUES (?, ?, ?, ?)";
        String sql = String.format(SQL_ADD, workerNode.getTableName());
        Object[] obj = {workerNode.getHostName(), workerNode.getPort(), workerNode.getType(), workerNode.getLaunchDate()};
        qr.update(connection, sql, obj);
    }

    public void deleteWorkerNode(WorkerNode workerNode, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_DELETE = "DELETE FROM %s where id = ?";
        String sql = String.format(SQL_DELETE, workerNode.getTableName());
        qr.update(connection, sql, workerNode.getId());
    }

    public Long queryWorkerIdByIP(WorkerNode workerNode, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_QUERY_BY_IP = "SELECT * FROM %s WHERE host_name = ?";
        String sql = String.format(SQL_QUERY_BY_IP, workerNode.getTableName());
        List<WorkerNode> workerIds = qr.query(connection, sql, new BeanListHandler<>(WorkerNode.class), workerNode.getHostName());
        if (CollectionUtils.isNotEmpty(workerIds) && Objects.nonNull(workerIds.get(0))) {
            return workerIds.get(0).getId();
        }
        return null;
    }

}
