package SHL;

import java.util.*;
/*You are given a list of numbers.
Write an algorithm to remove all the duplicate numbers of the list se
 that the list contains only distinct numbers in the same order as they appear in the input list
* */
public class removeDuplicates {
    public static void main(String[] args) {
        List<Integer> list= Arrays.asList(4, 5, 4, 2, 2, 3, 5, 1);
        List<Integer> output=removeDuplicatesM(list);
        System.out.println("List after removing duplicates: " + output);
    }

    private static List<Integer> removeDuplicatesM(List<Integer> list) {
        List<Integer> output=new ArrayList<>(new LinkedHashSet<>(list));
        return output;
    }
}
