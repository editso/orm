package org.zhiyong.format.impl;

import org.zhiyong.format.base.BaseParameter;
import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.orm.util.ArrayUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;


public class PreparedParameter extends BaseParameter<PreparedStatement> {
    protected Connection connection;


    public PreparedParameter(String origin, Connection connection) {
        super(origin);
        this.connection = connection;
    }

    @Override
    public PreparedStatement transform() {
        ArrayList<KeyDescription> descriptions = new ArrayList<>();
        descriptions.addAll(options.values());
        descriptions.addAll(keys.values());
        for (KeyDescription description : descriptions) {
            if (byValues.contains(description)){
                blocks.set(description.index, "?");
                continue;
            };
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
        String sql = ArrayUtil
                .toString(blocks, "")
                .replaceAll("[ ]+", " ");
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ArrayList<KeyDescription> list = new ArrayList<>(byValues);
            list.sort((keyDescription, t1) -> {
                if (keyDescription.index > t1.index) return 1;
                else if (keyDescription.index < t1.index) return -1;
                return 0;
            });
            KeyDescription key = null;
            for (int i = 0; i < byValues.size(); i++) {
                key = list.get(i);
                preparedStatement.setObject(i + 1, key.value);
            }
            return preparedStatement;
        } catch (SQLException e) {
            return null;
        }
    }


}
