package org.zhiyong.orm.engine;

import org.zhiyong.orm.api.Configure;
import org.zhiyong.orm.api.Engine;
import org.zhiyong.orm.api.ObjectMapper;


import java.util.concurrent.CopyOnWriteArrayList;

public class EngineManager {
    private final static CopyOnWriteArrayList<Engine> engines = new CopyOnWriteArrayList<>();


    public static void registerEngine(Engine engine){
        engines.add(engine);
    }

    public static ObjectMapper builder(Configure configure){
        for (Engine engine : engines) {
            ObjectMapper mapper = engine.getObjectMapper();
            if (mapper != null) {
                mapper.init(configure);
                return mapper;
            }
        }
        throw new Error("找不到引擎");
    }


}
