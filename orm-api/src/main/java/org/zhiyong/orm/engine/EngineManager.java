package org.zhiyong.orm.engine;

import org.apache.log4j.Logger;
import org.zhiyong.orm.api.Configure;
import org.zhiyong.orm.api.Engine;
import org.zhiyong.orm.api.ObjectMapper;
import org.zhiyong.orm.exceptions.EngineError;
import org.zhiyong.orm.util.ReflectionUtil;


import java.util.concurrent.CopyOnWriteArrayList;


public class EngineManager {
    private final static Logger logger = Logger.getLogger(EngineManager.class);
    private static ObjectMapper objectMapper;
    private static Engine engine;

    public static ObjectMapper builder(Configure configure, boolean autoCreateTable){
        if (objectMapper != null) {
            objectMapper.setAutoCreateTable(autoCreateTable)
                    .init(configure);
            logger.info("\nMapper Info: \n"+objectMapper.mapperInfo());
            return objectMapper;
        }
        throw new EngineError("找不到引擎");
    }

    public static void use(String engineClass){
        try {
            EngineManager.engine = (Engine) ReflectionUtil.newInstance(Class.forName(engineClass));
            objectMapper = engine.getObjectMapper();
        } catch (ClassNotFoundException e) {
            throw new EngineError("找不到数据库引擎: " + engineClass);
        }
    }

    public static ObjectMapper builder(Configure configure){
        return builder(configure, false);
    }

    public static ObjectMapper currentObjectMapper(){
        return objectMapper;
    }


}
