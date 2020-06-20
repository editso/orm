package org.zhiyong.format.interfaces;


public interface ParameterFormatter {
    ParameterFormatter set(String key, Object value);
    ParameterFormatter set(int index, Object value);
    ParameterFormatter setOption(String key, boolean use);
    ParameterFormatter setOption(String key, String value);
    ParameterFormatter setOptionSeparator(String separator);
    Object get(String key);
    Object get(int index);
    int count();
    ParameterFormatter clear();
    String getOrigin();
    String transform();
    String[] keys();
    String[] options();
}
