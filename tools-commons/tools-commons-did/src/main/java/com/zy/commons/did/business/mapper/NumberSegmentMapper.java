package com.zy.commons.did.business.mapper;

import com.zy.commons.did.business.entity.NumberSegment;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class NumberSegmentMapper {
    private static final NumberSegmentMapper INSTANCE = new NumberSegmentMapper();

    public static NumberSegmentMapper getInstance() {
        return INSTANCE;
    }

    public void addNumberSegment(NumberSegment numberSegment, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_ADD = "INSERT INTO %s (host_name, port, type) VALUES (?, ?, ?)";
        String sql = String.format(SQL_ADD, numberSegment.getTableName());
        Object[] obj = {numberSegment.getHostName(), numberSegment.getPort(), numberSegment.getType()};
        qr.update(connection, sql, obj);
    }

    public void deleteNumberSegment(NumberSegment numberSegment, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_DELETE = "DELETE FROM %s where id = ?";
        String sql = String.format(SQL_DELETE, numberSegment.getTableName());
        qr.update(connection, sql, numberSegment.getId());
    }

    public Long queryNumberSegmentByIP(NumberSegment numberSegment, QueryRunner qr, Connection connection) throws SQLException {
        String SQL_QUERY_BY_IP = "SELECT * FROM %s WHERE host_name = ?";
        String sql = String.format(SQL_QUERY_BY_IP, numberSegment.getTableName());
        List<NumberSegment> numberSegments = qr.query(connection, sql, new BeanListHandler<>(NumberSegment.class), numberSegment.getHostName());
        if (CollectionUtils.isNotEmpty(numberSegments) && Objects.nonNull(numberSegments.get(0))) {
            return numberSegments.get(0).getId();
        }
        return null;
    }

}
