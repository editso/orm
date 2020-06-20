package org.zhiyong.orm.util;

import java.util.Arrays;

public class ArrayUtil {

    public static String toString(Object[] array){
        return Arrays.toString(array).replaceAll("[\\[|\\]]", "");
    }

    public static String fillToString(String s, int count){
        String[] array = new String[count];
        Arrays.fill(array, s);
        return toString(array);
    }

}
