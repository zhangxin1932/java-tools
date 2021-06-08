package com.zy.tools.undefined.undefined.concurrent.v1;

/**
 * 模拟CAS算法
 * 实际的CAS是通过操作硬件来实现
 */
public class TestCompareAndSwap {

    public static void main(String[] args) {
        CompareAndSwap cas = new CompareAndSwap();
        for(int i = 0; i < 10; i ++){
            new Thread(() -> {
                int expectedValue = cas.getValue();
                boolean b = cas.compareAndSet(expectedValue, (int) (Math.random() * 100));
                System.out.println(b);
            }).start();
        }
    }

    static class CompareAndSwap {

        private int value;

        // 获取内存值
        public synchronized int getValue(){
            return value;
        }

        // 比较
        public synchronized int compareAndSwap(int expectedValue, int newValue){
            int oldValue = value;
            if (oldValue == expectedValue){
                this.value = newValue;
            }
            return oldValue;
        }

        // 设置
        public synchronized boolean compareAndSet(int expectedValue, int newValue){
            return expectedValue == compareAndSwap(expectedValue, newValue);
        }
    }

}
