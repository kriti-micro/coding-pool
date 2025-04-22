package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ThreeSum_2Pointer {
    public static void main(String[] args) {
        int[] inpArr=new int[]{-1,0,1,2,-1,-4};
        List<List<Integer>> ans=threeSum(inpArr);
        System.out.println("Triplets having sum as 0 = " + ans);
        int[] inpArr1=new int[]{0,0,0};
        List<List<Integer>> ans1=threeSum(inpArr1);
        System.out.println("Triplets having sum as 0 = " + ans1);
    }

    private static List<List<Integer>> threeSum(int[] nums) {
        List<List<Integer>> result=new ArrayList<>();
        Arrays.sort(nums);
        //Create for loop for each value to check having sum as 0
        for(int i =0;i<nums.length-1 && nums[i]<=0;i++){
            if(i==0 || nums[i]!=nums[i-1]){
                twoSum(nums,i,result);
            }
        }
        return result;
    }

    private static void twoSum(int[] nums, int i, List<List<Integer>> result) {

        System.out.println("nums = " + Arrays.toString(nums) + ", i = " + i + ", result = " + result);
        //using left and right pointer
        int left=i+1;
        int right=nums.length-1;

        while(left<right){

            int sum=nums[i]+nums[left]+nums[right];
            System.out.println("nums[i] = "+nums[i]+", left = " + nums[left] + ", right = " + nums[right] + ", sum = " + sum);
            if(sum<0){
                ++left;
            }else if(sum>0){
                --right;
            }else{
                result.add(Arrays.asList(nums[i],nums[left++],nums[right--]));
                while(left<right && nums[left]==nums[left-1]){
                    ++left;
                }
            }
        }

    }
}
