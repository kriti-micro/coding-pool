package java_programs;

import java.util.*;

public class ComparableComparatorSorting {
    public static void main(String[] args) {
        Student s1=new Student(13," Kriti");
        Student s2=new Student(12," Rita");
        Student s3=new Student(11," Anjali");
        List<Student> list= Arrays.asList(s1,s2,s3);

        System.out.println(" Sorting using comparable for names of Student.");
        Collections.sort(list);
        list.stream().forEach(e->System.out.println(e.getId() +" "+e.getName()));

        System.out.println(" Sorting using comparator[lambda] for ids of Student in desc order.");
        Collections.sort(list,(a,b)->{
            return b.getId()-a.getId();
        });

        list.stream().forEach(e->System.out.println(e.getId() +" "+e.getName()));
    }
}

class Student implements Comparable{
    private int id;
    private String name;

    public Student(int i, String s) {
        this.id=i;
        this.name=s;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int compareTo(Object o) {
        Student s=(Student)o;
        //return this.id-s.getId(); //If we want sorting acc to id
        return this.name.compareTo(s.getName()); //If we want sorting acc to name
    }



}
