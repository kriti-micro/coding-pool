package java8_stream;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Map.Entry.comparingByValue;

public class FindSecondHighest {
    public static void main(String[] args) {

        Integer[] intArr=new Integer[]{4,5,5,7,3,2};
        List<Integer> list=new ArrayList<>(Arrays.asList(intArr));
        System.out.println("original list = " + list);

        //Asc order
        Integer lowest =list.stream().distinct().sorted().findFirst().get();
        System.out.println(" The lowest using stream : "+lowest);

        //Use of terminal operation
        System.out.println(" Asc List : " + list.stream().sorted((a,b)->a-b).collect(Collectors.toList()));

        //Desc order and use of intermediate operation
        Integer secondHighest=list.stream().distinct().sorted((a,b)->b-a).skip(1).findFirst().get();
        System.out.println(" The second highest using stream : "+secondHighest);

        //Use of terminal operation
        System.out.println(" Desc list : " + list.stream().sorted((a,b)->b-a).collect(Collectors.toList()));

        //use of reduce
        int sum=list.stream().reduce(0,(a,b)->a+b);
        System.out.println(" Sum of List : "+sum);

        //Using array
        List filteredList=Arrays.stream(intArr).filter(i->i>=4).toList();
        System.out.println(" filtered List using Array : "+filteredList);

        //Using Map
        List mapList=list.stream().map(i->i*2).toList();
        System.out.println(" Map List using original list : "+mapList);

        //Use of grouping By [used as Map] and Another way stream creation
        Stream.of("Kriti","Kriti","Daddy","Mom","Mom","Mom")
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()))
                .entrySet().stream()
                .filter(e->e.getValue()>1)
                .forEach(System.out::println);

        //Flattened the nested List
        List<List<String>> nestedList=Arrays.asList(Arrays.asList("A"),Arrays.asList("B","C"));
        System.out.println(" The nested List : "+nestedList);
        List<String> flattenedList=nestedList.stream().flatMap(List::stream).toList();
        System.out.println(" The flattened List from nested List : "+flattenedList);


        //To create map of having count of characters
        String name="Kriti";

        Map<Character,Long> charMap=name.toLowerCase().chars() //chars() return IntStream
                .mapToObj(c->(char)c) //converting int value to char
                .filter(Character::isLetter)  // only alphabetic characters
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println(" the frequency Map : "+charMap);


        //to sort the Map using comparater by Key i.e. ascending
        System.out.println("Sort the Map using comparater by Key i.e. ascending");
        List<Map.Entry<Character,Long>> entryLst=new ArrayList<>(charMap.entrySet());
        entryLst.stream()
                .sorted(Map.Entry.comparingByKey()) //use comparator as Entry does not implement comparable ex sorted() give CLassCastException
                .forEach((e)-> {
            System.out.println(e.getKey() + "= " + e.getValue());
        });

        //to sort the Map using comparater by Value i.e. ascending
        System.out.println("Sort the Map using comparater by Value i.e. ascending");
        entryLst.stream()
                .sorted(comparingByValue())
                .forEach(e->System.out.println(e.getKey()+"="+e.getValue()));

        //to find the highest freq character
        System.out.println("Sort the Map using comparater by Value i.e. descending");
        entryLst.stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .limit(1)
                .forEach(e->System.out.println(e.getKey()+"="+e.getValue()));

        //to find the second highest freq character - Compact Code
        String input="Hello @little Babyyy!";
        Map<Character,Long> freqMap=input.toLowerCase()
                .chars()
                .mapToObj(c->(char)c)
                .filter(Character::isLetter)
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println("To find the second highest freq character from Map : "+freqMap);
        freqMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .skip(1)
                .findFirst()
                .ifPresent(c->System.out.println(c));

        //To preserve order in Linked HashMap
        //To sort by value in desc,key in asce ->Use Custome Comparator
        LinkedHashMap<Character,Long> linkedHashMap=freqMap.entrySet().stream()
                .sorted(
                        Comparator
                                .comparing(Map.Entry<Character, Long>::getValue,Comparator.reverseOrder())
                                .thenComparing(Map.Entry::getKey)
                )
                .collect(Collectors.toMap(
                   Map.Entry::getKey, // Function to extract key from element (keyMapper)
                   Map.Entry::getValue, // Function to extract value from element (valueMapper)
                   (e1,e2)->e1,// What to do if duplicate keys exist (mergeFunction)
                        LinkedHashMap::new //Type of Map to create e.g. LinkedHashMap (mapSupplier)
                ));
        System.out.println("To preserve order in Linked HashMap \nSort the values in desc then sort by key asce when same values : "+linkedHashMap);
    }

}
