package org.zhiyong.format.builder;

import org.zhiyong.format.impl.PreparedParameter;
import org.zhiyong.format.impl.StringParameter;
import org.zhiyong.format.interfaces.Parameter;

import java.sql.Connection;
import java.sql.PreparedStatement;


public class ParameterFactory {
    public static Parameter<PreparedStatement> prepared(String s,
                                                        Connection connection){
        return new PreparedParameter(s, connection);
    }

    public static Parameter<String> stringParameter(String s){
        return new StringParameter(s);
    }


    public static FunctionalPrepared functional(String s){
        return new FunctionalPrepared(stringParameter(s));
    }

}
