package practise;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class FirstNonRepeatingCharacter {

    public static Character firstNonRepeatingCharacter(String word){
        LinkedHashMap<Character,Long> map=word.chars()
                .mapToObj(c->(char)c)
                .collect(Collectors.groupingBy(
                        Function.identity(),
                        LinkedHashMap::new,
                        Collectors.counting()
                        ));
        System.out.println(map);
        Map.Entry<Character,Long> entry=map.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue()) //Need to take care of sorting
                .limit(1)
                .findFirst().get();
        return entry.getKey();
    }

    public static void main(String[] args) {
        String str="swiss";
        Character result = firstNonRepeatingCharacter(str);
        System.out.println("First non repeating character of "+str+" and result = " + result);
    }
}
