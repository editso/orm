package org.zhiyong.orm.util;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * 包相关工具工具方法
 */
public class PackageUtil {

    private static void scannerClass(String pkg,
                                     List<Class<?>> classes,
                                     File file,
                                     int curDepth,
                                     int depth){
        if (file.isDirectory() && curDepth != depth){
            File[] files = file.listFiles();
            if (files == null) return;
            for (File f : files) {
                scannerClass(pkg, classes, f, curDepth + 1, depth);
            }
        }
        if(!file.isFile() || ! file.getName().endsWith(".class")) return;
        pkg = file.getAbsolutePath()
                .replaceAll(".*("+pkg+".*)\\.class", "$1")
                .replace(File.separator, ".");
        try {
            classes.add(Class.forName(pkg));
        } catch (ClassNotFoundException ignore) {
        }

    }

    /**
     * 扫描一个表下指定深度的 class
     * @param pkg 被扫描的包
     * @param depth 扫描深度
     * @return 扫描到的 class
     */
    public static Class<?>[] scannerClass(String pkg, int depth){
        if (pkg.startsWith(".")){
            String packagePath = System.getProperty("sun.java.command");
            pkg = packagePath.replaceAll("(.*)\\.[A-z]*", "$1" + pkg);
        }
        pkg = pkg.replace(".", File.separator);
        List<Class<?>> classes = new ArrayList<>();
        try {
            Enumeration<URL> enumeration =  PackageUtil.class.getClassLoader().getResources(pkg);
            while (enumeration.hasMoreElements()){
                URL url = enumeration.nextElement();
                if (!"file".equals(url.getProtocol())) continue;
                scannerClass(pkg, classes, new File(url.getPath()),0, depth);
            }
        }catch (Exception e){
            return classes.toArray(Class[]::new);
        }
        return classes.toArray(Class[]::new);
    }

    /**
     * 扫描指定包下的所有类,只会扫描当前包下所有class
     * @param pkg 包
     */
    public static Class<?>[] scannerClass(String pkg){
        return scannerClass(pkg, 1);
    }

}
