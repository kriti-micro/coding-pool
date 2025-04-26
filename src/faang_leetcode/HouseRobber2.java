package faang_leetcode;

import java.util.Arrays;

//https://leetcode.com/problems/house-robber-ii/submissions/1621350766/
//Dynamic Programming ->TimeComplexity O(n) & space O(1)
public class HouseRobber2 {
    public static int rob(int[] nums) {
        System.out.println("nums = " + Arrays.toString(nums));
        int rob1=0;
        int rob2=0;
        int max1=nums[0];
        for(int i =0;i<nums.length-1;i++){
            max1=Math.max(rob1+nums[i],rob2);
            rob1=rob2;
            rob2=max1;
            System.out.println("max1 = "+max1 + ", rob1 = "+rob1+", rob2 = "+rob2);
        }
        System.out.println(" 2nd loop...");
        rob1=0;
        rob2=0;
        int max2=0;
        for(int i =1;i<nums.length;i++){
            max2=Math.max(rob1+nums[i],rob2);
            rob1=rob2;
            rob2=max2;
            System.out.println("max2 = "+max2 + ", rob1 = "+rob1+", rob2 = "+rob2);
        }

        return Math.max(max1,max2);

    }

    public static void main(String[] args) {
        int nums[]=new int[]{1,2,15,11,3,6,10};
        int max=rob(nums);
        System.out.println("The max amount he can rob in circle = " + max);
    }
}
