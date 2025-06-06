package java_programs;

import java.util.Arrays;

public class InstanceExtendProgram {
    public static void main(String[] args) {
        B b=new B();
        System.out.println("args = " + ((b instanceof B) && !(b instanceof C)));

        //MCQ test
        boolean a=false;
        if(a=true){
            System.out.println("Hello ");
        }else{
            System.out.println("Goodbye ");
        }

        //MCQ
        try {
            Float f1 = new Float("3.0");
            int x = f1.intValue();
            byte c = f1.byteValue();
            double d = f1.doubleValue();
            System.out.println(x + c + d);
        } catch (NumberFormatException e) {
            System.out.println("bad number");
        }
    }
}
class A {

}
class B extends A {

}
class C extends B {

}