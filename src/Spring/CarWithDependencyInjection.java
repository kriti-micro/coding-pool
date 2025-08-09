package Spring;

public class CarWithDependencyInjection {

    Engine engine;
    // Constructor Injection: IoC here
    CarWithDependencyInjection(Engine e){
        this.engine=e;
    }

    public void drive(){
        engine.start();
        System.out.println("The car is running ...");
    }

    public static void main(String[] args) {
        //Control is inverted and we inject dependency
        CarWithDependencyInjection c=new CarWithDependencyInjection(new Engine());
        c.drive();
    }
}
