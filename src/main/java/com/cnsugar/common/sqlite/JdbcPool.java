package com.cnsugar.common.sqlite;

import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author Sugar
 * @Version 2019/4/23 17:11
 */
public class JdbcPool {
    private volatile static JdbcPool pool;
    public static final String MAX_TOTAL = "sqlite.conn.maxTotal";
    public static final String MAX_IDLE = "sqlite.conn.maxIdle";
    public static final String MIN_IDLE = "sqlite.conn.minIdle";
    public static final String MAX_WAIT_MILLIS = "sqlite.conn.maxWaitMillis";

    public static JdbcPool getInstance() {
        if (pool == null) {
            synchronized (JdbcPool.class) {
                if (pool == null) {
                    pool = new JdbcPool();
                }
            }
        }
        return pool;
    }

    private static GenericObjectPool<Connection> connPool;

    private JdbcPool() {
        connPool = new GenericObjectPool<Connection>(new JdbcPoolFactory(), getDefaultConfig());
    }

    private GenericObjectPoolConfig getDefaultConfig() {
        GenericObjectPoolConfig conf = new GenericObjectPoolConfig();
        conf.setMaxTotal(Integer.parseInt(System.getProperty(MAX_TOTAL, "100")));
        conf.setMaxIdle(Integer.parseInt(System.getProperty(MAX_IDLE, "0")));
        conf.setMinIdle(Integer.parseInt(System.getProperty(MIN_IDLE, "0")));
        conf.setMaxWaitMillis(Integer.parseInt(System.getProperty(MAX_WAIT_MILLIS, "60000")));
        return conf;
    }

    public Connection getConnection() {
        try {
            return connPool.borrowObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void close(PreparedStatement ps, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
            }
        }
        if (ps != null) {
            try {
                ps.close();
            } catch (SQLException e) {
            }
        }
    }

    public static void returnConnection(Connection conn) {
        connPool.returnObject(conn);
    }

    public static void returnConnectionAndClose(Connection conn, PreparedStatement ps, ResultSet rs) {
        close(ps, rs);
        returnConnection(conn);
    }
}
