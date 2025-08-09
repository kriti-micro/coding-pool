package Spring;

class Engine{
    public void start(){
        System.out.println("Engine started");
    }
}

public class CarWithoutIOC {

    Engine engine;

    CarWithoutIOC(){
        engine=new Engine();
    }

    public void drive(){
        engine.start();
        System.out.println("The car is running ...");
    }
    public static void main(String[] args) {
        System.out.println("Description without IOC where we create object using new and\n it is tightly coupled [cant inject other engines.]. ");
        CarWithoutIOC car=new CarWithoutIOC();
        car.drive();
    }
}
