package com.zy.tools.undefined.undefined.clone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 *
 * https://www.jianshu.com/p/196685389bee
 *
 * 克隆是将对象的值拿过来，方便快速copy一个对象,java中有两种克隆方式:
 * 一个是浅度克隆,另外一个则是深度克隆(deep copy)。
 *
 *
 *
 * 他们之间有什么区别?
 *
 * 浅度克隆
 * 是克隆对象的副本,克隆对象的引用，
 * 在JAVA中的实现方式是只需要实现标记接口Cloneable即可(标记该对象可以调用Object.clone()方法)
 *
 * 深度克隆
 * 是指克隆对象的值，相当于值传递,
 * 实现方式是对象字节流进行序列化与反序列化转换式传输,
 * 深度克隆出来的对象里面包含的属性指向的是新的堆空间
 * (注意的是如果用到对象字节流的话所有的对象属性包括自己都必须要实现序列化,基本类型除外)
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShallowCloneObject implements Cloneable {
    private Integer id;
    private String name;
    private Object object;

    public static void main(String[] args) throws CloneNotSupportedException {
        ShallowCloneObject object = new ShallowCloneObject(1, "浅克隆对象", new Object());
        System.out.println(Objects.equals(object, object.clone()));
        System.out.println(Objects.deepEquals(object, object.clone()));
        System.out.println("----------------------");
        ShallowCloneObject clone = (ShallowCloneObject) object.clone();
        System.out.println(Objects.equals(object.getName(), clone.getName()));
        System.out.println(Objects.deepEquals(object.getName(), clone.getName()));
        System.out.println("----------------------");
        System.out.println(Objects.equals(object.getObject(), clone.getObject()));
        System.out.println(Objects.deepEquals(object.getObject(), clone.getObject()));
    }
}
