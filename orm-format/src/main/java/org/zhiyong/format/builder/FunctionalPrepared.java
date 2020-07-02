package org.zhiyong.format.builder;

import org.zhiyong.format.interfaces.Parameter;
import org.zhiyong.orm.util.ArrayUtil;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class FunctionalPrepared {
    private Parameter<String> formatter;
    private Connection connection;

    public FunctionalPrepared(Parameter<String> formatter){
        this.formatter = formatter;
    }

    public FunctionalPrepared append(Object o){
        return this;
    }

    public <E> FunctionalPrepared by(String key,
                                     String base,
                                     E[] elements,
                                     VoidFunction<Parameter<String>, E> function){
        by(key, base, ",", elements, function);
        return this;
    }

    public <E> FunctionalPrepared by(String key,
                                     String base,
                                     String separation,
                                     E[] elements,
                                     VoidFunction<Parameter<String>, E> function){
        List<String> arrays = new ArrayList<>();
        Parameter<String> mat;
        for (E element : elements){
            mat = ParameterFactory.stringParameter(base);
            by(element, mat, function);
            if (mat.isAvailable()) arrays.add(mat.transform());
        }
        formatter.set(key, ArrayUtil.toString(arrays));
        return this;
    }


    public String prepared(){
        return formatter.transform();
    }

    public <R, T>R  to(T t, Function<Object, T, String> function){
        return (R) function.apply(t, prepared());
    }


    public <E> FunctionalPrepared by(E element, VoidFunction<Parameter<String>, E> function){
        by(element, formatter, function);
        return this;
    }

    private <E, A> FunctionalPrepared by(E element, A a, VoidFunction<A,E> function){
        try {
            function.apply(a, element);
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
        return this;
    }

    public  <T>T apply(Arg1Function<T, String> function){
        return function.apply(formatter.transform());
    }

    public<T, E> FunctionalPrepared byEach(T[] elements, E e ,
                                           VoidFunction<Parameter<String>, T> to,
                                           VoidFunction<E, String> from){
        for (T element : elements) {
            by(element, to);
            by(prepared(), e, from);
            formatter.clear();
        }
        return this;
    }


    public interface Function<R, T, E>{
        R apply(T t, E e);
    }

    public interface Arg1Function<R, E>{
        R apply(E e);
    }

    public interface VoidFunction<T, E>{
        void apply(T t, E e)throws Exception;
    }

}
