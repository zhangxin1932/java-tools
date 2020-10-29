package com.zy.tools.undefined.concurrent.v1.producerAndConsumer.v1;

/**
 * 生产者与消费者案例:
 * 等待唤醒机制
 */
public class TestProducerAndConsumerWaitAndNotify {

    public static void main(String[] args) {
        Clerk clerk1 = new Clerk();
        Producer producer = new Producer(clerk1);
        Consumer consumer = new Consumer(clerk1);
        new Thread(producer, "生产者A").start();
        new Thread(consumer, "消费者A").start();
        // 若是进货与卖货方法,不再循环中,即没使用while循环,而是用if进行啊判断,
        // 则当再增加一个消费者和一个生产者时,可能会出现问题了
        new Thread(producer, "生产者B").start();
        new Thread(consumer, "消费者B").start();

    }

}

/**
 * 店员
 * 同时有卖货和进货的方法
 */

class Clerk{

    // 初始货物数量统计
    private int product = 0;

    // 店员卖货的方法
    public synchronized void sale(){
        while (product <= 0) { //为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
            System.out.println("目前缺货中,暂时无法提供服务,请下次再来");
            try {
                this.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 消费者可以买货物
        --product;
        System.out.println(Thread.currentThread().getName() + " : " + product);
        // 通知
        this.notifyAll();
    }

    // 店员进货的方法
    public synchronized void purchase(){
        while (product >= 1) { //为了避免虚假唤醒问题，应该总是使用在while循环中,此处不能用if
            System.out.println("货品已经满仓,停止进货!");
            try{
                this.wait(); // 店员停止进货
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // 生产者可以生产货物
        ++product;
        System.out.println(Thread.currentThread().getName() + " : " + product);
        // 通知卖货
        this.notifyAll();
    }

}

/**
 * 生产者
 */
class Producer implements Runnable {

    private Clerk clerk;

    public Producer(Clerk clerk){
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            /*try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
            clerk.purchase();
        }
    }
}

/**
 * 消费者
 */
class Consumer implements Runnable {

    private Clerk clerk;

    public Consumer(Clerk clerk){
        this.clerk = clerk;
    }

    @Override
    public void run() {
        for (int i = 0; i < 10; i ++){
            clerk.sale();
        }
    }
}
