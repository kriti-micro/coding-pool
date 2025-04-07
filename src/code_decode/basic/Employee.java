package code_decode.basic;

import java.util.*;

public class Employee implements Comparable<Employee>{
    private int id;
    private int age;
    private String name;
    private double salary;
    private Address address;

    public Employee(int id, int age, String name, double salary,Address address) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.salary=salary;
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", age=" + age +
                ", name='" + name + '\'' +
                ", salary=" + salary +
                ", address=" + address +
                '}';
    }

    @Override
    public int compareTo(Employee o) {
        return this.id-o.id;
    }

    public static void main(String[] args) {
        Employee e1=new Employee(1,21,"Tom",10000.0,new Address("str1",452016));
        Employee e2=new Employee(3,56,"Kate",89000.0,new Address("str2",452022));
        Employee e3=new Employee(4,28,"Jerry",50000.0,new Address("str1",452011));
        Employee e4=new Employee(2,32,"Shanaya",23000.0,new Address("str2",452055));

        List<Employee> list =new ArrayList<>();
        list.add(e1);
        list.add(e2);
        list.add(e3);
        list.add(e4);
        System.out.println("Unsorted Employees : ");
        System.out.println(list);
        System.out.println("*****************************************" );

        Collections.sort(list);
        System.out.println("Sort Employees By id naturally (Asc): ");
        System.out.println(list);
        System.out.println("*****************************************" );

        //Using Lambda expression
        list.sort(Comparator.comparingInt(Employee::getAge));
        //Lamda expression for desc order
        //list.sort(Comparator.comparingInt(Employee::getAge).reversed());
        //Collections.sort(list,new SortByAge());
        System.out.println("Sort Employees By age(Ascending) : ");
        System.out.println(list);
        System.out.println("*****************************************" );

        Collections.sort(list,new SortByName());
        System.out.println("Sort Employees By name(Alphabateically) : ");
        System.out.println(list);
        System.out.println("*****************************************" );

        Collections.sort(list,new SortBySalary());
        System.out.println("Sort Employees By Salary(Descending) : " );
        System.out.println(list);
        System.out.println("*****************************************" );

        Collections.sort(list,new SortByAddress());
        System.out.println("Sort Employees By Address by pincode(Asc) : " );
        System.out.println(list);
        System.out.println("*****************************************" );

    }



    static class SortByAge implements Comparator<Employee>{

        @Override
        public int compare(Employee o1, Employee o2) {
            return o1.age-o2.age;
        }
    }

    static class SortByName implements Comparator<Employee>{

        @Override
        public int compare(Employee o1, Employee o2) {
            return o1.name.compareTo(o2.name);
        }
    }

    static class SortBySalary implements Comparator<Employee>{

        @Override
        public int compare(Employee o1, Employee o2) {
            return (int)(o2.salary-o1.salary);
        }
    }

    static class SortByAddress implements Comparator<Employee>{

        @Override
        public int compare(Employee o1, Employee o2) {
            return o1.address.compareTo(o2.address);
        }
    }

}
