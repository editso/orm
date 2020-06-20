package org.zhiyong.orm.api;

import org.zhiyong.orm.description.ConnectionDescription;

public interface Configure {
    ConnectionDescription connect();
    String[] mapperPackages();


    static Configure from(ConnectionDescription description, String[] mapperPackages){
        return new Configure() {
            @Override
            public ConnectionDescription connect() {
                return description;
            }

            @Override
            public String[] mapperPackages() {
                return mapperPackages;
            }
        };
    }

}
