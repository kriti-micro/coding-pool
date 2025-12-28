package dsa.arrays;

import java.util.Arrays;
import java.util.HashSet;

public class L219_ContainsDuplicateII {
    public static boolean containsDuplicate(int[] arr,int k){
        HashSet<Integer> set=new HashSet<>();

        for(int i=0;i<arr.length;i++){
            System.out.println("Hashset = " + set);
            if(set.contains(arr[i])){
                return true;
            }
            set.add(arr[i]);
            if(set.size() > k){
                set.remove(arr[i-k]);
            }
        }

        return false;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,2,3,1,2,3};
        boolean isDuplicatePresent=containsDuplicate(arr,2);
        System.out.println("Is duplicate present in array " + Arrays.toString(arr) + " : "+isDuplicatePresent);
    }
}
