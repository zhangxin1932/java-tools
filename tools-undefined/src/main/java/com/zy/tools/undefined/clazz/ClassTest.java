package com.zy.tools.undefined.clazz;

public class ClassTest {

    public static void main(String[] args) {
        // 数组
        System.out.println("数组等类型 ---------------------");
        System.out.println(String[].class.getName()); // [Ljava.lang.String;
        System.out.println(String[].class.getCanonicalName()); // java.lang.String[]
        System.out.println(String[].class.getSimpleName()); // String[]
        System.out.println(String[].class.getTypeName()); // java.lang.String[]

        // 成员内部类
        System.out.println("成员内部类型 ---------------------");
        System.out.println(InnerClass.class.getName()); // com.zy.tools.clazz.ClassTest$InnerClass
        System.out.println(InnerClass.class.getCanonicalName()); // com.zy.tools.clazz.ClassTest$InnerClass
        System.out.println(InnerClass.class.getSimpleName()); // InnerClass
        System.out.println(InnerClass.class.getTypeName()); // com.zy.tools.clazz.ClassTest$InnerClass

        // 匿名内部类
        System.out.println("匿名内部类型 ---------------------");
        System.out.println(new Object(){}.getClass().getName()); // com.zy.tools.clazz.ClassTest$1
        System.out.println(new Object(){}.getClass().getCanonicalName()); // null
        System.out.println(new Object(){}.getClass().getSimpleName()); // ""
        System.out.println(new Object(){}.getClass().getTypeName()); // com.zy.tools.clazz.ClassTest$4

        // 普通类
        System.out.println("普通类型 ---------------------");
        System.out.println(ClassTest.class.getName()); // com.zy.tools.clazz.ClassTest
        System.out.println(ClassTest.class.getCanonicalName()); // com.zy.tools.clazz.ClassTest
        System.out.println(ClassTest.class.getSimpleName()); // ClassTest
        System.out.println(ClassTest.class.getTypeName()); // com.zy.tools.clazz.ClassTest

        // 基本数据类型
        System.out.println("基本数据类型 ---------------------");
        System.out.println(int.class.getName()); // int
        System.out.println(int.class.getCanonicalName()); // int
        System.out.println(int.class.getSimpleName()); // int
        System.out.println(int.class.getTypeName()); // int
    }

    private class InnerClass {}
}
