package faang_leetcode;

import java.util.Arrays;

public class HouseRobber {

    //Dynamic Programming ->TimeComplexity O(n) & space O(1)
    public static int rob(int[] nums) {
        System.out.println("nums = " + Arrays.toString(nums));
        int rob1=0;
        int rob2=0;
        int max=0;
        System.out.println(" Initial values of max = "+max + ", rob1 = "+rob1+", rob2 = "+rob2);
        for(int i =0;i<nums.length;i++){
            System.out.println(" Max comparing  ("+nums[i]+"+"+rob1+"), " +rob2);
            max=Math.max(rob1+nums[i],rob2);
            rob1=rob2;
            rob2=max;
            System.out.println("max = "+max + ", rob1 = "+rob1+", rob2 = "+rob2);
        }
        return max;
    }

    public static void main(String[] args) {
        int nums[]=new int[]{2,4,3,9,11,2};
        int max=rob(nums);
        System.out.println("The max amount he can rob  = " + max);
    }
}
