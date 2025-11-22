package practise;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MostRepeatedNLongestString {
    public static void main(String[] args) {
        List<String> list=new ArrayList<>(Arrays.asList("Pen", "Pen","Pen","Pen","Pen", "Pencil","Pencil",  "Note Book","Note Book", "Note Book", "Eraser", "Eraser"));
        String mostRepeatedElement = list.stream()
                .collect(Collectors.groupingBy(
                    Function.identity(),
                    Collectors.counting()
                )).entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .findFirst().get().getKey();

        String longestElement=list.stream().distinct().max(Comparator.comparingInt(s->s.length())).get();
        System.out.println("mostRepeatedElement = longestElement ? " + mostRepeatedElement.equals(longestElement));
        System.out.println("mostRepeatedElement  " + mostRepeatedElement);
        System.out.println("longestElement  " + longestElement);

        //Alternarive Ways using Collector.toMap
        Map<String,Integer> lengthMap=list.stream().distinct().collect(Collectors.toMap(
                s->s,
                s->s.length()
        ));
        System.out.println("lengthMap = " + lengthMap);
        String longestElementAlternative =lengthMap.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).findFirst().get().getKey();
        System.out.println("longestElement  " + longestElementAlternative);

        System.out.println("mostRepeatedElement = longestElementAlternative ? " + mostRepeatedElement.equals(longestElementAlternative));

    }
}
