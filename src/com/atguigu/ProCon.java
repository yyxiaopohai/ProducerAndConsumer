package com.atguigu;

import javax.swing.text.StyledEditorKit;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者消费者模式
 */
public class ProCon
{
    public static void main(String[] args) throws InterruptedException {


        MyResource myResource = new MyResource(new ArrayBlockingQueue<>(10));

        new Thread(() -> {
            try {
                myResource.myPro();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"pro").start();
        new Thread(() -> {
            try {
                myResource.myCon();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        },"con").start();

        TimeUnit.SECONDS.sleep(5);
        System.out.println(Thread.currentThread().getName() + "\t 停止生产蛋糕");
        myResource.stop();
    }
}


class MyResource{

    private volatile boolean flag = true;

    private AtomicInteger atomicInteger = new AtomicInteger();

    BlockingQueue<String> blockingQueue = null;

    public MyResource(BlockingQueue<String> blockingQueue){
        this.blockingQueue = blockingQueue;
        System.out.println("当前阻塞队列的名字：" + blockingQueue.getClass().getName());
    }

    public void myPro() throws InterruptedException {
        String data = null;
        boolean retValue;
        while (flag) {
            data = atomicInteger.incrementAndGet() + "";
            retValue = blockingQueue.offer(data);
            if (retValue){
                System.out.println(Thread.currentThread().getName() + "\t 生产蛋糕成功");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 生产蛋糕失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + "\t 停止生产");
    }

    public void myCon() throws InterruptedException {
        String result = null;
        while (flag){
            result = blockingQueue.poll(2, TimeUnit.SECONDS);

            if (null == result || result.equals("")){
                flag = false;
                System.out.println(Thread.currentThread().getName() + "\t 超过两秒没有消费到蛋糕");
                return;
            }
            System.out.println(Thread.currentThread().getName() + "\t 消费蛋糕成功");
        }
    }

    public void stop(){
        this.flag = false;
    }
}