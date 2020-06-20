package org.zhiyong.orm.api;

public interface ObjectMapper {

    void init(Configure configure);

    Session openSession();

}
