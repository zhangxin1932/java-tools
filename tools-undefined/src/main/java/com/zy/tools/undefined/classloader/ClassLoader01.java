package com.zy.tools.undefined.classloader;

import java.io.IOException;
import java.io.InputStream;

/**
 * 类加载器虽然只用于实现类的加载动作， 但它在Java程序中起到的作用却远超类加载阶段。
 * 对于任意一个类， 都必须由加载它的类加载器和这个类本身一起共同确立其在Java虚拟机中的唯一性， 每一个类加载器， 都拥有一个独立的类名称空间。
 *
 * 这句话可以表达得更通俗一些： 比较两个类是否“相等”， 只有在这两个类是由同一个类加载器加载的前提下才有意义，
 * 否则， 即使这两个类来源于同一个Class文件， 被同一个Java虚拟机加载， 只要加载它们的类加载器不同， 那这两个类就必定不相等。
 *
 * 这里所指的“相等”， 包括代表类的Class对象的equals()方法、 isAssignableFrom()方法、 isInstance() 方法的返回结果，
 * 也包括了使用instanceof关键字做对象所属关系判定等各种情况。
 *
 * 如果没有注意到类加载器的影响， 在某些情况下可能会产生具有迷惑性的结果。
 */
public class ClassLoader01 {

    public static void main(String[] args) throws Exception {
        ClassLoader customClassLoader = new CustomClassLoader();
        Object person = customClassLoader.loadClass("com.zy.tools.undefined.classloader.PersonEntity").newInstance();
        System.out.println(person.getClass());
        System.out.println(person instanceof com.zy.tools.undefined.classloader.PersonEntity);
    }

    /**
     * 注意 PersonEntity 跟 ClassLoader01 在同一个包中
     */
    static class CustomClassLoader extends ClassLoader {
        @Override
        public Class<?> loadClass(String name) throws ClassNotFoundException {
            try {
                String fileName = name.substring(name.lastIndexOf(".") + 1) + ".class";
                InputStream is = getClass().getResourceAsStream(fileName);
                if (is == null) {
                    return super.loadClass(name);
                }
                byte[] bytes = new byte[is.available()];
                is.read(bytes);
                return defineClass(name, bytes, 0, bytes.length);
            } catch (IOException e) {
                throw new ClassNotFoundException(name);
            }
        }
    }

}
