package com.cnsugar.common.sqlite;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @Author Sugar
 * @Version 2019/4/23 17:16
 */
public class JdbcPoolFactory extends BasePooledObjectFactory<Connection> {
    private static final Logger logger = LoggerFactory.getLogger(JdbcPoolFactory.class);

    private static final String DRIVER = "org.sqlite.JDBC";
    public static final String DB_NAME = "seetaface.db";

    private static String URL;

    static {
        String db = System.getProperty(DB_NAME);
        if (db == null || db.isEmpty()) {
            logger.error("System.getProperty(\"{}\") is null", DB_NAME);
        } else {
            URL = "jdbc:sqlite:" + db;
            logger.info(URL);
        }
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Connection create() throws Exception {
        if (URL == null) {
            return null;
        }
        return DriverManager.getConnection(URL);
    }

    @Override
    public PooledObject<Connection> wrap(Connection conn) {
        return new DefaultPooledObject<Connection>(conn);
    }
}
