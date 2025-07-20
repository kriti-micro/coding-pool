package java_programs;

import java.util.Arrays;

public class ThreadInOrder {
    public static void main(String[] args) throws InterruptedException {
        Thread t1=new Thread(()->{
            System.out.println("T1 running = " + Thread.currentThread().getName());
        });//NEW STATE
        System.out.println(" State of thread : "+t1.getState());
        Thread t2=new Thread(()->{
            System.out.println("T2 running = " + Thread.currentThread().getName());
        });


        t2.start();//RUNNABLE STATE
        System.out.println(" State of thread : "+t2.getState());
        t2.join(); //t2 will finish first
        System.out.println(" State of thread : "+t2.getState());

        t1.start();
        t1.join();//then t1

        System.out.println(" Main Thread running");//Main thread run at last
    }
}
