package java8_stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MethodReferenceExercices {

    public static void main(String[] args) {
        List<Integer> list=Arrays.asList(new Integer[]{25,43,52,31,81});

        //Using Anonymous class
        Function<Integer,Double> f3=new Function<Integer, Double>() {
            @Override
            public Double apply(Integer integer) {
                return Math.sqrt(integer);
            }
        };
        List<Double> listSqrt1 = list.stream().map(f3).collect(Collectors.toList());
        System.out.println("Using Anonymous class "+listSqrt1);

        //Using Lambda Expression
        Function<Integer,Double> f1=(num)->Math.sqrt(num);
        List<Double> listSqrt = list.stream().map(f1).collect(Collectors.toList());
        System.out.println("Using LAMBDA Expression "+listSqrt);

        //Using Method Reference
        Function<Integer,Double> f2=Math::sqrt;
        List<Double> listSqrt2 = list.stream().map(f2).collect(Collectors.toList());
        System.out.println("Using Method Reference "+listSqrt2);


        //Exercice 2: convert list of string to uppercase
        List<String> listStr=Arrays.asList(new String[]{"asd","fertg","kriti"});

        System.out.println("\nEx 2 using Lambda ");
        Function<String,String> fun1=(str)->str.toUpperCase();
        listStr.stream().map(fun1).forEach(s->System.out.println(s));
        System.out.println("\nUsing Method Ref : ");
        Function<String,String> fun2=String::toUpperCase;
        listStr.stream().map(fun2).forEach(System.out::println);

        //Ex-3 : Sort the Color Object
        List<Color> colorList=new ArrayList<>();
        colorList.add(new Color("Red",""));
        colorList.add(new Color("Black",""));
        colorList.add(new Color("Indigo",""));

        System.out.println("\nEx 3 using Lambda ");
        List<String> listOfName=colorList.stream()
                .map(c->c.getName())
                .sorted()
                .collect(Collectors.toList());
        listOfName.forEach(System.out::println);

        System.out.println("\nUsing Method Ref : ");
        List<String> listOfNames=colorList.stream()
                .map(Color::getName)
                .sorted()
                .collect(Collectors.toList());
        listOfNames.forEach(System.out::println);

    }

}
