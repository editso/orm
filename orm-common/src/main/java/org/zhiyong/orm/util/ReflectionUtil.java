package org.zhiyong.orm.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Function;

/**
 * 反射相关工具包
 */
public class ReflectionUtil {

    /**
     *  实例化一个对象
     * @param clazz 被实例化的类
     * @param args 被实例化类的构造参数
     * @param <T> 被实例化的类
     * @return 返回一个实例化对象
     */
    public static <T>T  newInstance(Class<T> clazz, Object ...args){
        try {
            Class<?>[] types;
            for (Constructor<?> constructor : clazz.getDeclaredConstructors()) {
                types = constructor.getParameterTypes();
                if (types.length != args.length) continue;
                for (int i = 0; i <= args.length; i++) {
                    if (i == args.length){
                        return (T)constructor.newInstance(args);
                    }
                    if (!types[i].isInstance(args[i])){
                        break;
                    }
                }
            }
        }catch (Exception e){
            throw new Error(e);
        }
        throw new NoSuchMethodError(Arrays.toString(Arrays.stream(args)
                .map(Object::getClass)
                .toArray(Object[]::new)) + "/" + clazz.getName());
    }


    public static Class<?>[] getInterfaces(Class<?> clazz){
        Set<Class<?>> classes = new HashSet<>();
        if (clazz.isInterface()){
            classes.add(clazz);
        }
        classes.addAll(Arrays.asList(clazz.getInterfaces()));
        return classes.toArray(Class[]::new);
    }

    public static <T>T proxy(Class<T> mInterface, InvocationHandler handler){
        return (T)Proxy.newProxyInstance(
                ReflectionUtil.class.getClassLoader(),
                new Class[]{mInterface},
                handler);
    }


    public interface Function<R, E>{
        R apply(E e);
    }
}
