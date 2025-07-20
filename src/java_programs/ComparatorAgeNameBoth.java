package java_programs;

import java.util.*;

public class ComparatorAgeNameBoth {
    public static void main(String[] args) {
        Employee e1=new Employee(1,30,"Ishika");
        Employee e2=new Employee(2,30,"Jyotilal");
        Employee e3=new Employee(3,26,"Kriti");
        Employee e6=new Employee(6,20,"Kriti");
        Employee e4=new Employee(4,78,"Raju");
        Employee e5=new Employee(5,21,"Raju");

        List<Employee> empList= Arrays.asList(e1,e2,e3,e4,e5,e6);
        //Using java 7 Custom Comparator
        /**
        Collections.sort(empList,(em1,em2)->{
            if(em1.age!= em2.age){
                return em1.age-em2.age;
            }
            return em1.name.compareTo(em2.name);
        });
         **/
        System.out.println("Before = " );
        empList.forEach(System.out::println);

        //Using Comparator chaining
        empList.sort(
                Comparator.comparingInt((Employee emp1)->emp1.age)
                .thenComparing((Employee emp2)->emp2.name)
        );

        System.out.println("After = " );
        empList.forEach(System.out::println);
    }
}

class Employee{
    int id;
    int age;
    String name;

    Employee(int i,int a,String n){
        this.id=i;
        this.age=a;
        this.name=n;
    }

    @Override
    public String toString() {
        return "Id : "+id+" Age : "+age+" Name : "+name;
    }
}
