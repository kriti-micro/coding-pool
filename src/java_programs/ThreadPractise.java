package java_programs;

import java.util.Arrays;

public class ThreadPractise {
    public static void main(String[] args) throws InterruptedException {
        MyThread t1=new MyThread();
        t1.setName("Thread1");
        t1.start();

        Thread t2=new Thread(new MyRunnable());
        t2.setName("Thread2");
        t2.start();

        Thread t3=new Thread(()->{
            System.out.println("Thread running using lamba creation = " + Thread.currentThread().getName());
        });
        t3.setName("Thread3");
        t3.start();

        // Use sleep(): pause main thread
        System.out.println("Main thread sleeping for 1 second...");
        Thread.sleep(2000);

        // join(): wait for t1 to finish
        System.out.println("Main thread waiting for Thread-1 to finish using join()");
        t1.join();

        System.out.println("Main thread exiting.");
    }

}
class MyThread extends Thread{
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" running using extended thread ");
    }
}

class MyRunnable implements Runnable{

    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName()+" running using runnable ");
    }
}

/**Output : Due to no synchronization unless we use join or sleep to make current thread wait
 * Thread1 running using extended thread
 * Thread2 running using runnable
 * Main thread sleeping for 1 second...
 * Thread running using lamba creation = Thread3
 * Main thread waiting for Thread-1 to finish using join()
 * Main thread exiting.
 */
