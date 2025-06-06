package java_programs;

import java.util.Arrays;

public class ThreadProgram {

    public static void main(String[] args) throws InterruptedException {

        Counter counter=new Counter();
        //Anonyamus class
        Runnable task=()->{
            for(int i =0;i<1000;i++){
                counter.increment();
            }
        };
        Thread t1=new Thread(task);
        Thread t2=new Thread(task);
        Thread t3=new Thread(task);
        Thread t4=new Thread(task);
        Thread t5=new Thread(task);
            t1.start();t2.start();t3.start();t4.start();t5.start();
            //main thread waits for t1 t2 t3 t4 t5 to complete the task so O/p =5000
        t1.join(); t2.join(); t3.join(); t4.join(); t5.join();
        System.out.println("count = " + counter.getValue());// O/P->615
    }
}

class Counter{
    private int count=0;

    //If no synchronized keyword then it can return anything
    public synchronized void increment(){
        count=count+1;
    }
    public int getValue(){
        return count;
    }
}
