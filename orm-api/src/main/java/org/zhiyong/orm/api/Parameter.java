package org.zhiyong.orm.api;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;


/**
 * 参数
 * @see PreparedStatement
 */
public interface Parameter{

    default Parameter setObject(int index, Object o){
        return this;
    }

    default Parameter fromTo(Object[] objects){
        for (int i = 0; i < objects.length; i++) {
            setObject(i + 1, objects[i]);
        }
        return this;
    }

}
