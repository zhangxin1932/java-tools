package com.zy.tools.undefined.concurrent.v1;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * CopyOnWriteArrayList/CopyOnWriteArraySet : “写入并复制”
 * 注意：添加操作多时，效率低，因为每次添加时都会进行复制，开销非常的大。并发迭代操作多时可以选择。
 */
public class TestCopyOnWriteArrayList {

    public static void main(String[] args) {
        TestThread thread = new TestThread();
        for (int i = 0; i < 10; i++){
            new Thread(thread).start();
        }
    }
}

class TestThread implements Runnable {

    // private static List<String> list = Collections.synchronizedList(new ArrayList<String>());
    // java.util.ConcurrentModificationException

    private static CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

    static {
        list.add("tom1");
        list.add("tom2");
        list.add("tom3");
    }

    @Override
    public void run() {
        Iterator<String> it = list.iterator();
        while (it.hasNext()) {
            System.out.println(it.next());
            list.add("jerry");
        }
    }
}