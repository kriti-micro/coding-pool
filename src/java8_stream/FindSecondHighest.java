package java8_stream;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    }
}
