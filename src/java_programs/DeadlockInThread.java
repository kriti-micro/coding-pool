package java_programs;

public class DeadlockInThread {

        static final Object lock1 = new Object();
        static final Object lock2 = new Object();

        public static void main(String[] args) {
            Thread t1 = new Thread(() -> {
                synchronized (lock1) {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    synchronized (lock2) {
                        System.out.println("Thread 1: Acquired both locks");
                    }
                }
            });

            Thread t2 = new Thread(() -> {
                synchronized (lock2) {
                    try { Thread.sleep(100); } catch (InterruptedException ignored) {}
                    synchronized (lock1) {
                        System.out.println("Thread 2: Acquired both locks");
                    }
                }
            });

            t1.start();
            t2.start();
        }


}
