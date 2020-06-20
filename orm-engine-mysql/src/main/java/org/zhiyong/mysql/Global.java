package org.zhiyong.mysql;

import org.zhiyong.orm.util.ReflectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


/**
 * 全局数据
 */
public final class Global {
    private static final Map<Class<?>, ClassDescription> clazzs;

    static {
        clazzs = new HashMap<>();
    }

    public static <T> ClassDescription<T> find(Class<?> interfaceClazz){
        return clazzs.get(interfaceClazz);
    }

    public static <T>T findToInstance(Class<?> interfaceClazz, Objects objects){
        return (T) clazzs.get(interfaceClazz).get(objects);
    }

    public static <T>T findToInstance(Class<?> interfaceClazz){
        ClassDescription<T> description = clazzs.get(interfaceClazz);
        if (description == null)return null;
        return (T) description.get();
    }

    public static synchronized <T> void add(Class<?> interfaceClazz, ClassDescription<T> description){
        clazzs.put(interfaceClazz, description);
    }

    public static synchronized <T> void add(Class<?> key, T value){
        clazzs.put(key, new ClassDescription<T>(key, value));
    }

    public static synchronized <T> void addAll(T o){
        for (Class<?> anInterface : ReflectionUtil.getInterfaces(o.getClass())) {
            add(anInterface, new ClassDescription<T>(anInterface, o));
        }
    }

    public static final class ClassDescription<T>{
        private final Class<?> clazz;
        private T instance;

        public ClassDescription(Class<?> clazz, T instance) {
            this.clazz = clazz;
            this.instance = instance;
        }

        public ClassDescription(Class<T> clazz) {
            this.clazz = clazz;
        }

        public synchronized T get(Object ...objects){
            return instance;
        }

        public synchronized <E>E get(Class<E> clazz, Object ...objects) {
            return (E) instance;
        }
    }

}
