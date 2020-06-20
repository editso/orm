package org.zhiyong.orm.base;

import org.zhiyong.orm.api.GenerateFactory;
import org.zhiyong.orm.description.FieldDescription;

import java.util.UUID;

public class UUIDGenerateFactory implements GenerateFactory {
    @Override
    public void generate(Object o, FieldDescription description) {
        description.setValue(o, UUID.randomUUID().toString());
    }
}
