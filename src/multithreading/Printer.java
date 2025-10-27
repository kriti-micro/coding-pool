package multithreading;

public class Printer {
    private int number=1;
    private final int MAX=10;
    public synchronized void printOdd(){
        while(number<10){
            if(number%2==0){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(" Odd Number : "+number++);
            notify();
        }

    }
    public synchronized void printEven(){
        while(number<10){
            if(number%2==1){
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(" Even Number : "+number++);
            notify();
        }
    }
}

class OddEvenDemo{
    public static void main(String[] args) {
        Printer printer=new Printer();
        Thread t1=new Thread(printer::printEven);
        Thread t2=new Thread(printer::printOdd);
        t1.start();
        t2.start();

    }
}
