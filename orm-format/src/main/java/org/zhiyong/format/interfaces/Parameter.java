package org.zhiyong.format.interfaces;


public interface Parameter<T> {
    /**
     * 使用指定key设置value
     * @param key 关键字
     * @param value 设置的值
     */
    Parameter<T> set(String key, Object value);

    /**
     * 使用角标设置值
     * @param index 角标位置
     * @param value 值
     */
    Parameter<T> set(int index, Object value);

    /**
     * 使用内部自增设置值, 从角标 0 开始
     * @param value 被设置的值
     */
    Parameter<T> set(Object value);

    /**
     * 如果是 sql value 将会过滤非法字符串
     * @param key 关键字
     * @param value 值
     */
    Parameter<T> setByValue(String key, Object value);

    /**
     * 如果是 sql value 将会过滤非法字符串
     * @param index 索引位置
     * @param value 值
     */
    Parameter<T> setByValue(int index, Object value);

    /**
     * 使用内部自增设置值, 从角标 0 开始
     * @param value 被设置的值
     */
    Parameter<T> setByValue(Object value);


    /**
     * 设置可选参数, 可选参数默认不会被显示
     * 如果可选参数带有空格,那么关键字的名称为驼峰方式命名
     * 列子:
     *      ${primary key} -> key: primaryKey
     * @param key 可选参数关键字
     * @param use 是否使用,显示:true 不显示:false
     */
    Parameter<T> setOption(String key, boolean use);

    /**
     * 设置可选参数值, 可选参数默认不会被显示
     * 如果可选参数带有空格,那么关键字的名称为驼峰方式命名
     *  列子:
     *     ${primary key} -> key: primaryKey
     * @param key 可选参数关键字
     * @param value 值
     */
    Parameter<T> setOption(String key, String value);

    /**
     * 设置可选参数显示后的分隔符默认 ','
     * @param separator 分隔符
     */
    Parameter<T> setOptionSeparator(String separator);

    /**
     * 设置角标起始位置
     * @param position 位置
     */
    Parameter<T> setPosition(int position);

    /**
     * 获取角标起始位置, 如果有设置, 默认为 0
     */
    int getPosition();

    /**
     * 获取当前角标位置
     */
    int getCurPosition();

    /**
     * 重置位置, 如果调用setPosition后那么重置后的起始位置和setPosition后的相同
     * @return 返回重置后的位置
     */
    int resetPosition();

    /**
     * 获取关键字参数的值
     * @param key 关键字
     * @return 值
     */
    Object get(String key);

    /**
     * 获取指定索引的角标位置
     * @param index 索引
     * @return 值
     */
    Object get(int index);

    /**
     * 获取关键字参数的总个数
     * @return 总个数
     */
    int count();

    /**
     * 清空所有设置的值
     */
    Parameter<T> clear();

    /**
     * 获取原始信息
     * @return 原始信息
     */
    String getOrigin();

    /**
     * 转换为设置后的
     */
    T transform();

    /**
     * 返回所有关键字参数
     * @return 关键参数
     */
    String[] keys();

    /**
     * 返回所有可选参数
     * @return 可选参数
     */
    String[] options();

    /**
     * 是否可用
     * @return true可用否则false
     */
    boolean isAvailable();

    /**
     * 设置当前参数格式器是否可用
     * @param a true:可用 false:不可用
     */
    Parameter<T> setAvailable(boolean a);

    /**
     * 重新生成语句
     * @return 新的参数提供器
     */
    Parameter<T> replace();




}
