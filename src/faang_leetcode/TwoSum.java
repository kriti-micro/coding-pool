package faang_leetcode;

import java.util.*;

/*
*
Q-> https://leetcode.com/problems/two-sum/description/
* Given an array of integers nums and an integer target, return indices of the two numbers such that they add up to target.
You may assume that each input would have exactly one solution, and you may not use the same element twice.
* I/P nums : 2,7,11,15 Target:9
* O/P : [0,1]
* */
public class TwoSum {

    public static int[] twoSum(int[] nums, int target) {
        Map<Integer,Integer> hmap=new HashMap<>();
        int result[]=new int[2];
        Arrays.fill(result,-1);
        for(int i =0;i<nums.length;i++){
            if(hmap.containsKey(target-nums[i])){
                result[1]=i;
                result[0]=hmap.get(target-nums[i]);

            }else{
                hmap.put(nums[i],i);
            }
        }
        return result;
    }

    public static void main(String[] args) {

        int nums[] =new int[]{2,7,11,15};
        int target=9;
        int result[]=twoSum(nums,target);
        System.out.println("Indices of two sum  = [" + result[0]+","+result[1]+"]");
    }
}
