package com.zy.tools.undefined.clone;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeepCloneObject implements Serializable {
//    private static final long serialVersionUID = 5164963008737540853L;
    private Integer id;
    private String name;
    public DeepCloneObject deepClone() throws Exception {
        Field[] fields = this.getClass().getDeclaredFields();
        java.security.AccessController.doPrivileged(new java.security.PrivilegedAction<Void>() {
            @Override
            public Void run() {
                Arrays.stream(fields).forEach(field -> field.setAccessible(true));
                return null;
            }
        });
        // 检测所有属性是否都实现了 Serializable 接口
        Arrays.stream(fields).forEach(field -> {
            if (field.getClass().isPrimitive() || Modifier.isStatic(field.getModifiers())) {
                return;
            }
            if (Serializable.class.isAssignableFrom(field.getClass())) {
                throw new RuntimeException("the field:[" + field.getName() + "] must be implements Serializable interface exclude primitive class");
            }
        });
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        // 将当前这个对象写到一个输出流当中，，因为这个对象的类实现了 Serializable 这个接口，所以在这个类中
        // 有一个引用，这个引用如果实现了序列化，那么这个也会写到这个输出流当中
        ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (DeepCloneObject) ois.readObject();
    }

    public static void main(String[] args) throws Exception {
        DeepCloneObject object = new DeepCloneObject(1, "深度克隆对象");
        System.out.println(Objects.equals(object, object.deepClone()));
        System.out.println(Objects.deepEquals(object, object.deepClone()));
        System.out.println("----------------------");
        DeepCloneObject clone = object.deepClone();
        System.out.println(Objects.equals(object.getName(), clone.getName()));
        System.out.println(Objects.deepEquals(object.getName(), clone.getName()));
        System.out.println("----------------------");
    }
}
