package org.zhiyong.orm.api;

import org.zhiyong.orm.description.FieldDescription;


/**
 * 生成器接口
 */
public interface GenerateFactory {
    void generate(Object o, FieldDescription description);
}
