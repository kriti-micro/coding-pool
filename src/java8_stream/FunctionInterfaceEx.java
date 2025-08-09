package java8_stream;

public class FunctionInterfaceEx {
    public static void main(String[] args) {
        HelloWorld helloWorld=()->System.out.println("Say hello from Lambda expression.");
        helloWorld.sayHello();
    }
}
