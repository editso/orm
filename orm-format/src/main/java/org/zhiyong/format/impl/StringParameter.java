package org.zhiyong.format.impl;

import org.zhiyong.format.base.BaseParameter;
import org.zhiyong.orm.util.ArrayUtil;

import java.util.ArrayList;

public class StringParameter extends BaseParameter<String> {

    public StringParameter(String origin) {
        super(origin);
    }

    @Override
    public String transform() {
        ArrayList<KeyDescription> descriptions = new ArrayList<>();
        descriptions.addAll(options.values());
        descriptions.addAll(keys.values());
        for (KeyDescription description : descriptions) {
            if (description.use){
                if (description.value != null){
                    blocks.set(description.index, description.value.toString());
                }else {
                    blocks.set(description.index, null);
                }
            }else {
                blocks.set(description.index, "");
            }
        }
        return ArrayUtil.toString(blocks, "")
                .replaceAll("[ ]+", " ");
    }
}
