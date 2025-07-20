package java_programs;

public class WaitNotifyDemo {

    private static final Object lock = new Object();

    public static void main(String[] args) {
        Thread waitingThread = new Thread(() -> {
            synchronized (lock) {
                System.out.println("Waiting thread is waiting...");
                try {
                    lock.wait();// releases lock and waits
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                System.out.println("Waiting thread resumed.");
            }
        });

        Thread notifyingThread=new Thread(()->{
            synchronized (lock) {
                System.out.println("Notifier thread is notifying...");
                lock.notify();// wakes up waiting thread
            }
        });

        waitingThread.start();
        try {
            Thread.sleep(5000);// Ensure waitingThread gets the lock first
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        notifyingThread.start();
    }

}
