package org.zhiyong.mysql;

import org.zhiyong.orm.api.ObjectMapper;
import org.zhiyong.orm.engine.EngineManager;

public class Engine implements org.zhiyong.orm.api.Engine {
    static {
        EngineManager.registerEngine(new Engine());
        System.out.println("init engine");
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return new MysqlObjectMapper();
    }
}
