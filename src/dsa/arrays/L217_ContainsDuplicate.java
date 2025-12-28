package dsa.arrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

public class L217_ContainsDuplicate {
    //O(n) returning indices of target sum
    public static boolean containsDuplicate(int[] arr){
        HashSet<Integer> seenNumbers=new HashSet<>();

        for(int i=0;i<arr.length;i++){

            if(seenNumbers.contains(arr[i])){
                return true;
            }
            //System.out.println("Hashset = " + seenNumbers);
            seenNumbers.add(arr[i]);
        }

        return false;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,2,3,1};
        boolean isDuplicatePresent=containsDuplicate(arr);
        System.out.println("Is duplicate present in array " + Arrays.toString(arr) + " : "+isDuplicatePresent);
    }
}
