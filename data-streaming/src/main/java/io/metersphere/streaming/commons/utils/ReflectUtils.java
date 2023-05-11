package io.metersphere.streaming.commons.utils;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 *
 */
public class ReflectUtils {

    //获取某个类的实现类
    public static List<Class<?>> getAllAssignedClass(Class<?> cls) throws Exception {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        for (Class<?> c : getClasses(cls)) {
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                classes.add(c);
            }
        }
        return classes;
    }

    public static List<Class<?>> getClasses(Class<?> cls) throws Exception {
        String pk = cls.getPackage().getName();
        String path = pk.replace('.', '/');
        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        URL url = classloader.getResource(path);
        return getClasses(new File(url.getFile()), pk);
    }

    //根据路径获取
    public static List<Class<?>> getClasses(File dir, String pk) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!dir.exists()) {
            return classes;
        }
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) {
                classes.addAll(getClasses(f, pk + "." + f.getName()));
            }
            String name = f.getName();
            if (name.endsWith(".class")) {
                classes.add(Class.forName(pk + "." + name.substring(0, name.length() - 6)));
            }
        }
        return classes;
    }

    //动态获取，根据反射，比如获取xx.xx.xx.xx.Action 这个所有的实现类。 xx.xx.xx.xx 表示包名  Action为接口名或者类名
    public static List<Class<?>> getAllActionSubClass(String classPackageAndName) throws Exception {
        Field field = null;
        Vector v = null;
        Class<?> cls = null;
        List<Class<?>> allSubclass = new ArrayList<Class<?>>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Class<?> classOfClassLoader = classLoader.getClass();
        cls = Class.forName(classPackageAndName);
        while (classOfClassLoader != ClassLoader.class) {
            classOfClassLoader = classOfClassLoader.getSuperclass();
        }
        field = classOfClassLoader.getDeclaredField("classes");
        field.setAccessible(true);
        v = (Vector) field.get(classLoader);
        for (int i = 0; i < v.size(); ++i) {
            Class<?> c = (Class<?>) v.get(i);
            if (cls.isAssignableFrom(c) && !cls.equals(c)) {
                allSubclass.add((Class<?>) c);
            }
        }
        return allSubclass;
    }
}