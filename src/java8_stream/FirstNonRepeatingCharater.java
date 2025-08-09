package java8_stream;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Collectors.*;

public class FirstNonRepeatingCharater {
    public static void main(String[] args) {
        String s="SASAS";
        LinkedHashMap<Character,Integer> charMap=s.chars() //return IntStream
                .mapToObj(c->(char)c) // Convert IntStream → Stream<Character>
                .collect(Collectors.groupingBy(
                                Function.identity(),
                                LinkedHashMap::new,
                                Collectors.collectingAndThen(Collectors.counting(),Long::intValue)
                ));
        System.out.println(charMap);
        Optional<Character> firstNonRepeatingChar = charMap.entrySet().stream().filter(e->e.getValue()==1).map(Map.Entry::getKey).findFirst();

        //It gives NoSuchElementException if directly printing
        //System.out.println(firstNonRepeatingChar.get());
        if(firstNonRepeatingChar.isPresent()){
             System.out.println(firstNonRepeatingChar.get());
         }else{
             System.out.println(Optional.ofNullable(null));
         }

    }
}
