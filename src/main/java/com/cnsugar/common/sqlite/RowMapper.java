package com.cnsugar.common.sqlite;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @Author Sugar
 * @Version 2019/4/23 17:46
 * @Copyright 上海云辰信息科技有限公司
 */
public interface RowMapper<T> {

    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    T mapRow(ResultSet rs) throws SQLException;

}
