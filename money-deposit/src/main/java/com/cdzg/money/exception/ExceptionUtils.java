package com.cdzg.money.exception;

import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;

public class ExceptionUtils {

    /**
     * 违反唯一约束错误码
     */
    private static final String DULICATE_SQL_STATE = "23000";

    /**
     * 判断是否是违反唯一约束异常
     * @param dive
     * @return
     */
    public static boolean isDuplicate(DataAccessException dive) {
        if (dive.getCause() != null) {
            if (dive.getCause() instanceof SQLException) {
            	SQLException sqle = (SQLException) dive.getCause();
                return StringUtils.equals(sqle.getSQLState(),DULICATE_SQL_STATE);
            }
        }
        return false;
    }

}
