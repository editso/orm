package org.zhiyong.orm.util;

/**
 * 字符串工具包
 */
public class StringUtil {

    /**
     * 将驼峰方式命名转换为下划线方式
     * @param s 被转换的驼峰
     * @return 转换后的
     */
    public static String under(String s){
        if (s == null)return "";
        StringBuilder under = new StringBuilder();
        char chr;
        for (int i = 0; i < s.length(); i++) {
            chr = s.charAt(i);
            if (i == 0){
                under.append(Character.toLowerCase(chr));
            }else if(Character.isUpperCase(chr)){
                under.append("_").append(Character.toLowerCase(chr));
            }else{
                under.append(chr);
            }
        }
        return under.toString();
    }

    public static String under(String ...array){
        return ArrayUtil.toString(array, "_").replaceAll("[ ]*", "");
    }

    public static String hump(String ...s){
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < s.length; i++) {
            if (i == 0){
                buffer.append(s[i]);
                continue;
            }
            buffer.append(Character.toUpperCase(s[i].charAt(0)))
                    .append(s[i].substring(1));
        }
        return buffer.toString();
    }

}
