package faang_leetcode;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
/*
https://leetcode.com/problems/contains-duplicate/description/
* Given an integer array nums, return true if any value appears at least twice in the array, and return false if every element is distinct.
* */
public class ContainsDuplicate {
       public static boolean containsDuplicate(int[] nums) {
            Set<Integer> uniqueSet = new HashSet<>();
            for (int num : nums) {
                if (uniqueSet.contains(num)) {
                    return true;
                } else {
                    uniqueSet.add(num);
                }

            }
            return false;
        }


    public static void main(String[] args) {
      boolean containDups=containsDuplicate(new int[]{1,2,3,1})  ;
        System.out.println("containsDuplicate = " + containDups);
    }
}
