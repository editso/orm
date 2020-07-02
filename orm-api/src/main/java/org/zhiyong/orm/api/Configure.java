package org.zhiyong.orm.api;

import org.zhiyong.orm.description.ConnectionDescription;

/**
 * 配置接口
 */
public interface Configure {
    /**
     * 连接信息
     * @see ConnectionDescription
     */
    ConnectionDescription connectionDescription();

    /**
     * 被映射到数据库的包
     */
    String[] mapperPackages();


    static Configure from(ConnectionDescription description, String[] mapperPackages){
        return new Configure() {
            @Override
            public ConnectionDescription connectionDescription() {
                return description;
            }

            @Override
            public String[] mapperPackages() {
                return mapperPackages;
            }
        };
    }

}
