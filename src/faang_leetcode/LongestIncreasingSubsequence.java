package faang_leetcode;

import java.util.Arrays;

//https://leetcode.com/problems/longest-increasing-subsequence/
//Dynamic Programming O(n*n)
//Brute force has more time complexity O(2^n)
//Also possible with O(nlogn)
public class LongestIncreasingSubsequence {

    public static  int lengthOfLIS(int[] nums) {
        int Lis[]=new int[nums.length];
        System.out.println("nums = " + Arrays.toString(nums));
        Arrays.fill(Lis,1);
        int max=1;
        for(int i =1;i<Lis.length;i++){
            for(int j=0;j<i;j++){
                if(nums[i]>nums[j]){
                    Lis[i]=Math.max(Lis[i],1+Lis[j]);
                    max=Math.max(max,Lis[i]);
                    System.out.println("LIS["+i+"] = " + Lis[i]+", nums["+j+"] = "+nums[j]+" , Max = "+max);

                }

            }
        }
        return max;
    }
    public static void main(String[] args) {
        int inpArr[]=new int[]{10,7,8,5,16,27,9};
        int ans=lengthOfLIS(inpArr);
        System.out.println("The Longest Increasing subsequence = "+ans);
    }
}
