package org.zhiyong.format.builder;

import org.zhiyong.format.impl.ParameterFormatterImpl;
import org.zhiyong.format.interfaces.ParameterFormatter;

import java.lang.reflect.Array;
import java.util.*;


public class FormatterFactory {
    public static ParameterFormatter builder(String s){
        return new ParameterFormatterImpl(s);
    }
}
