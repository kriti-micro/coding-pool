package java8_stream;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ThirdDuplicateCharacter {
    public static void main(String[] args) {
        String str=" Hello puppy ok!";
        LinkedHashMap<Character,Long> charCount=str.toLowerCase().chars() //intStream i.e. unicode
                .mapToObj(c->(char)c) //convert intstream to object
                .filter(Character::isLetter)
                .collect(Collectors.groupingBy(
                        c->c, //or Function.identity()
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
        System.out.println("charCount = " + charCount);
        System.out.println("Third duplicate character ");
        charCount.entrySet().stream()
                .filter(e->e.getValue()>1)
                .skip(2)
                .findFirst()
                .ifPresent(System.out::println);

        System.out.println("Sorting on the basis of value in ascending : ");
        charCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()) //Comparator.naturalOrder()
                .forEach(System.out::println);

        //Original list did not get sorted,working on streams
        //Reverse Order
        System.out.println("Sorting on the basis of value in descending : ");
        charCount.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) // or .comparingByValue().reversed()
                .forEach(System.out::println);

        //Prsctise :
        System.out.println(" NAme char count : ");
        String name="Kriti Singh";
        LinkedHashMap<Character,Long> charCountMap=name.toLowerCase()
                .chars()
                .mapToObj(c->(char)c)
                .filter(Character::isLetter)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                ));
System.out.println(" Reverse order i.e. desc ");
charCountMap.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
        .limit(1)
        .forEach(e->System.out.println(e.getKey()+" : "+e.getValue())); //or forEach(System.out::println)



    }
}
