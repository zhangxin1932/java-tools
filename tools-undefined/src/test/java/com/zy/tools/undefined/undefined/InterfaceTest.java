package com.zy.tools.undefined.undefined;

import org.junit.Test;

public class InterfaceTest {

    @Test
    public void fn01() {
        Fa f01 = new Son01();
        Fa f02 = new Son02();
        f01.run();
        f02.run();
    }


    private class Son02 implements Fa {

        @Override
        public void run() {
            System.out.println(">>>>>>> Son02 ------");
        }
    }

    private class Son01 implements Fa {

        @Override
        public void run() {
            System.out.println(">>>>>>> Son01 ------");
        }
    }

    private interface Fa {
        void run();
    }
}

