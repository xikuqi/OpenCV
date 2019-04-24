package com.cnsugar.common.sqlite;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author Sugar
 * @Version 2019/4/23 16:38
 */
public class SqliteUtils {
    private static final Logger logger = LoggerFactory.getLogger(SqliteUtils.class);

    /**
     * 执行sql查询
     *
     * @param sql select 语句
     * @return 查询结果
     * @throws SQLException
     */
    public static String queryForString(String sql) throws SQLException {
        Connection conn = JdbcPool.getInstance().getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
            if (rs.next()) {
                return rs.getString(1);
            }
            return null;
        } finally {
            JdbcPool.returnConnection(conn);
        }
    }

    /**
     * 执行select查询，返回对象
     *
     * @param sql    select 语句
     * @param mapper 结果集的行数据处理类对象
     * @return
     * @throws SQLException
     */
    public static <T> T queryForObject(String sql, RowMapper<T> mapper) throws SQLException {
        Connection conn = JdbcPool.getInstance().getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
            if (rs.next()) {
                return mapper.mapRow(rs);
            }
        } finally {
            JdbcPool.returnConnection(conn);
        }
        return null;
    }

    /**
     * 执行select查询，返回结果列表
     *
     * @param sql    select 语句
     * @param mapper 结果集的行数据处理类对象
     * @return
     * @throws SQLException
     */
    public static <T> List<T> queryForList(String sql, RowMapper<T> mapper) throws SQLException {
        List<T> list = new ArrayList<T>();
        logger.debug("queryForList: sql={}", sql);
        Connection conn = JdbcPool.getInstance().getConnection();
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql);) {
            while (rs.next()) {
                list.add(mapper.mapRow(rs));
            }
        } finally {
            JdbcPool.returnConnection(conn);
        }
        return list;
    }

    /**
     * 执行数据库更新sql语句
     *
     * @param sql
     * @return 更新行数
     * @throws SQLException
     */
    public static int executeUpdate(String sql) throws SQLException {
        return executeUpdate(sql, null);
    }

    /**
     * 执行数据库更新sql语句
     *
     * @param sql
     * @return 更新行数
     * @throws SQLException
     */
    public static int executeUpdate(String sql, Object[] args) throws SQLException {
        logger.debug("executeUpdate: sql={}, parameter={}", sql, arrayToString(args));
        Connection conn = JdbcPool.getInstance().getConnection();
        if (args == null || args.length == 0) {
            try (Statement stmt = conn.createStatement()) {
                return stmt.executeUpdate(sql);
            } finally {
                JdbcPool.returnConnection(conn);
            }
        } else {
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                for (int i = 0; i < args.length; i++) {
                    stmt.setObject(i + 1, args[i]);
                }
                return stmt.executeUpdate();
            } finally {
                JdbcPool.returnConnection(conn);
            }
        }
    }

    /**
     * Object[] 转成 String
     *
     * @param objs 对象数组
     * @return
     */
    protected static String arrayToString(Object[] objs) {
        if (objs == null) {
            return "[]";
        }
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        for (int j = 0; j < objs.length; j++) {
            if (j > 0) {
                buf.append(", ");
            }
            buf.append(String.valueOf(objs[j]));
        }
        buf.append("]");
        return buf.toString();
    }
}
