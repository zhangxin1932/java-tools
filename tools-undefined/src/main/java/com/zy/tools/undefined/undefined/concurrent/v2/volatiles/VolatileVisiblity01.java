package com.zy.tools.undefined.undefined.concurrent.v2.volatiles;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.concurrent.TimeUnit;

/**
 * 测试 volatile 可见性:
 *
 * 定义：指当多个线程访问同一个变量时，一个线程修改了这个变量的值，其他线程能够立即看得到修改的值。
 *
 * 在多线程环境下，一个线程对共享变量的操作对其他线程是不可见的。
 * Java提供了volatile来保证可见性，当一个变量被volatile修饰后，表示着线程本地内存无效，
 * 当一个线程修改共享变量后他会立即被更新到主内存中，其他线程读取共享变量时，会直接从主内存中读取。
 *
 * 当然，synchronize和Lock都可以保证可见性。
 * synchronized和Lock能保证同一时刻只有一个线程获取锁然后执行同步代码，
 * 并且在释放锁之前会将对变量的修改刷新到主存当中。因此可以保证可见性。
 *
 *
 * #不加volatile的问题
 * 很多人在中断线程时可能都会采用这种标记办法。但是也有可能会导致无法中断线程造成死循环了。
 *
 * 在前面已经解释过，每个线程在运行过程中都有自己的工作内存，
 * main线程在运行的时候，会将flag变量的值拷贝一份放在自己的工作内存当中。　　
 *
 * 当线程2更改了flag变量的值之后，但是还没来得及写入主存当中，线程2转去做其他事情了，
 * 那么main线程由于不知道线程2对flag变量的更改，因此还会一直循环下去。
 *
 * #但是用volatile修饰之后就变得不一样了：
 * >> 使用volatile关键字会强制将修改的值立即写入主存；
 * >> 使用volatile关键字的话，当线程2进行修改时，会导致main线程的工作内存中缓存变量flag的缓存行无效
 * (反映到硬件层的话，就是CPU的L1或者L2缓存中对应的缓存行无效)
 * >> 在线程2修改flag值时(当然这里包括2个操作，修改线程2工作内存中的值, 然后将修改后的值写入内存),
 * 会使得main线程的工作内存中缓存变量flag的缓存行无效，然后main线程读取时，
 * 发现自己的缓存行无效，它会等待缓存行对应的主存地址被更新之后，然后去对应的主存读取最新的值。
 */
public class VolatileVisiblity01 {

    public static void main(String[] args) {
        final Task task = new Task();
        task.start();

        while (true) {
            if (task.isFlag()) {
                System.out.println(Thread.currentThread().getName() + " 线程探测到 flag 变为 true.");
            }
            /**
             * 如果 flag 字段不加 volatile, 那么此处加锁也是可以实现的, 原因如下:
             *
             * 某一个线程(此处是主线程)进入synchronized代码块前后，执行过程入如下:
             * a.线程获得锁
             * b.清空工作内存
             * c.从主内存拷贝共享变量最新的值到工作内存成为副本
             * d.执行代码
             * e.将修改后的副本的值刷新回主内存中
             * f.线程释放锁
             */
            /*synchronized (VolatileVisiblity01.class) {
                if (task.isFlag()) {
                    System.out.println(Thread.currentThread().getName() + " 线程探测到 flag 变为 true.");
                }
            }*/
        }
    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    private static class Task extends Thread {
        // private boolean flag = false;

        /**
         * volatile 保证可见性的原理:
         *
         * 1.子线程t从主内存读取到数据放入其对应的工作内存
         * 2.将flag的值更改为true，但是这个时候flag的值还没有写会主内存
         * 3.此时main方法main方法读取到了flag的值为false
         * 4.当子线程t将flag的值写回去后，其他线程嗅探到改变, 将自己线程中对此变量副本失效
         * 5.其他线程再次对flag进行操作的时候线程会从主内存读取最新的值，放入到工作内存中
         */
        private volatile boolean flag = false;
        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(1L);
                this.flag = true;
                System.out.println(Thread.currentThread().getName() + " 线程将 flag 置为了: " + this.flag);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
