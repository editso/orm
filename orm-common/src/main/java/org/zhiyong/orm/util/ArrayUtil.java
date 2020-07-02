package org.zhiyong.orm.util;



import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

public class ArrayUtil {

    public static String toString(Object[] array){
        return toString(array, ",");
    }

    public static String toString(Object[] array, String separation){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            builder.append(array[i]);
            if (i != array.length - 1){
                builder.append(separation);
            }
        }
        return builder.toString();
    }

    public static <T> String toString(Collection<T> collection, String separation){
        return toString(collection.toArray(), separation);
    }

    public static <T> String toString(Collection<T> collection){
        return toString(collection, ",");
    }

    public static <T> void each(T[] array, Each<T> each){
        for (T o : array) {
            each.apply(o);
        }
    }

    public static <E> void each(Collection<E> collection, Each<E> each){
        collection.forEach(each::apply);
    }

    public static <K, V> void each(Map<K, V> map, MapEach<K, V> each){
        map.forEach(each::apply);
    }

    public static <K, V> void each(Map<K, V> map, Each<Value<K, V>>  each){
        map.forEach((k, v) -> each.apply(new Value<>(k, v)));
    }

    /**
     * 填充指定的字符串
     * @param s 被填充的字符
     * @param count 数量
     * @return 填充后的
     */
    public static String fillToString(String s, int count){
        String[] array = new String[count];
        Arrays.fill(array, s);
        return toString(array);
    }

    public static String fillSequence(String s, int count){
        String[] array = new String[count];
        for (int i = 0; i < array.length; i++)
            array[i] = s + "_" + i;
        return toString(array);
    }

    public interface Each<E>{
        void apply(E target);
    }

    public interface MapEach<K,V>{
        void apply(K key, V value);
    }

    public static class Value<K, V>{
        public final K key;
        public final V value;

        public Value(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
