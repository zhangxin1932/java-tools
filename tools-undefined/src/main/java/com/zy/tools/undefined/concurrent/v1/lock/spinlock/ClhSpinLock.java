package com.zy.tools.undefined.concurrent.v1.lock.spinlock;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
/**
 * CLH锁是自旋锁的一种，AQS源代码中使用了CLH锁的一个变种
 * CLH Lock是独占式锁的一种，并且是不可重入的锁
 *
 * CLH的算法定义
 *   CLH lock is Craig, Landin, and Hagersten (CLH) locks,
 *   CLH lock is a spin lock, can ensure no hunger, provide fairness first come first service.
 *   The CLH lock is a scalable, high performance, fairness and spin lock based on the list,
 *   the application thread spin only on a local variable,
 *   it constantly polling the precursor state, if it is found that the pre release lock end spin.
 *
 */
public class ClhSpinLock implements Lock {
    private final ThreadLocal<QNode> prev;
    private final ThreadLocal<QNode> node;
    // step1.初始状态 tail指向一个node(head)节点
    private final AtomicReference<QNode> tail = new AtomicReference<>(new QNode());

    public ClhSpinLock() {
        this.node = ThreadLocal.withInitial(QNode::new);
        this.prev = ThreadLocal.withInitial(() -> null);
    }

    /**
     * 1.初始状态 tail 指向一个node(head)节点
     * +------+
     * | head | <---- tail
     * +------+
     *
     * 2.lock-thread 加入等待队列: tail指向新的Node，同时Prev指向tail之前指向的节点
     * +----------+
     * | Thread-A |
     * | := Node  | <---- tail
     * | := Prev  | -----> +------+
     * +----------+        | head |
     *                     +------+
     *
     *             +----------+            +----------+
     *             | Thread-B |            | Thread-A |
     * tail ---->  | := Node  |     -->    | := Node  |
     *             | := Prev  | ----|      | := Prev  | ----->  +------+
     *             +----------+            +----------+         | head |
     *                                                          +------+
     * 3.寻找当前node的prev-node然后开始自旋
     *
     */
    @Override
    public void lock() {
        final QNode qNode = this.node.get();
        qNode.locked = true;
        // step2.thread 加入等待队列: tail指向新的Node，同时 prev 指向 tail 之前指向的节点
        QNode pred = this.tail.getAndSet(qNode);
        this.prev.set(pred);
        // step3.自旋: 寻找当前线程对应的node的前驱node然后开始自旋前驱node的status判断是否可以获取lock
        while (pred.locked);
    }

    @Override
    public void unlock() {
        // 获取当前线程的node，设置lock status，将当前node指向前驱node(这样操作tail指向的就是前驱node等同于出队操作).
        QNode qNode = this.node.get();
        qNode.locked = false;
        this.node.set(this.prev.get());
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) {
        return false;
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public Condition newCondition() {
        return null;
    }

    private static class QNode {
        volatile boolean locked;
    }
}
