package faang_leetcode;

import java.util.Arrays;
/*
You are given an integer array nums. You are initially positioned at the array's first index, and each element in the array represents your maximum jump length at that position.
Return true if you can reach the last index, or false otherwise.
T-O(n),O(1) Optimal Solution
Dynamic Programming-O(n^2),O(n)
* */
public class JumpGame {

        public static boolean canJump(int[] nums) {
            System.out.println("nums = " + Arrays.toString(nums));
            int finall=nums.length-1;
            for(int i=nums.length-2;i>=0;i--){
                if(i+nums[i]>=finall){
                    finall=i;
                    System.out.println(finall);
                }

            }
            if(finall==0){
                return true;
            }
            return false;
        }



    public static void main(String[] args) {
        int nums[]={2,1,1,3,5};
        int nums2[]={2,3,1,0,2,5};
        boolean result=canJump(nums);
        System.out.println("is jump possible at last index = " + result);
        boolean result2=canJump(nums2);
        System.out.println("is jump possible at last index = " + result2);
    }

}
