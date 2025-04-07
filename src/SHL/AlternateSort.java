package SHL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AlternateSort {
    /*An alternate sort of a list consists of alternate elements (starting from the first position) of the
    given list after sorting it in an ascending order. You are given a list of unsorted elements.
    Write an algorithm to find the alternate sort of the given list
    * */

    public static void main(String[] args) {
        List<Integer> input = Arrays.asList(7, 3, 5, 2, 9, 1);
        List<Integer> output = alternateSort(input);
        System.out.println("Alternate sort of the list: " + output);
    }

    private static List<Integer> alternateSort(List<Integer> input) {
        Collections.sort(input);
        System.out.println("After sort of the list: " + input);
        List<Integer> alternateList=new ArrayList<>();
        for(int i=1;i<=input.size();i++){
            if(!(i%2==0)){
                alternateList.add(input.get(i-1));
            }
        }
        return alternateList;
    }
}
