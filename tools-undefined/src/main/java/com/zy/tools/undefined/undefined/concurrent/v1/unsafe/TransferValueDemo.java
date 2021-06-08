package com.zy.tools.undefined.undefined.concurrent.v1.unsafe;

import lombok.AllArgsConstructor;
import lombok.Data;

public class TransferValueDemo {

    public static void main(String[] args) {
        TransferValueDemo demo = new TransferValueDemo();
        int num = 1;
        demo.changeValue01(num);
        System.out.println(num);  // 1

        Stu stu = new Stu("jerry");
        demo.changeValue02(stu);
        System.out.println(stu.getName());   // tom

        String str = "xxx";
        demo.changeValue03(str);
        System.out.println(str);  // xxx
    }

    private void changeValue01(int num) {
        num = 3;
    }

    private void changeValue02(Stu stu) {
        stu.setName("tom");
    }

    private void changeValue03(String str) {
        str = "aaa";
    }

    @Data
    @AllArgsConstructor
    private static class Stu {
        private String name;
    }
}
