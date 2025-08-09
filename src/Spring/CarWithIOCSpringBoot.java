//package Spring;
//
//@Component
//class Engine {
//    public void start() {
//        System.out.println("Engine started");
//    }
//}
//
//@Component
//class Car {
//    @Autowired
//    Engine engine;
//
//    public void drive() {
//        engine.start();
//        System.out.println("Car is moving...");
//    }
//}
//
//@SpringBootApplication
//public class Main {
//    public static void main(String[] args) {
//        ApplicationContext context = SpringApplication.run(Main.class, args);
//        Car car = context.getBean(Car.class);
//        car.drive(); // Spring injects Engine automatically
//    }
//}

/*
Spring is the IoC container.
It creates and injects dependencies using @Autowired.
Objects (Car, Engine) are managed by the container, not by new.

One Liner :
Spring scans for @Autowired annotations using AutowiredAnnotationBeanPostProcessor, resolves dependencies from the container by type (and optionally by qualifier), and injects them using reflection — even if the field is private.

* */

