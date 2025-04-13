package faang_leetcode;

import java.util.Arrays;

/**
 * Given an integer array nums, return an array answer such that answer[i] is equal to the product of all the elements of nums except nums[i].
 *
 * The product of any prefix or suffix of nums is guaranteed to fit in a 32-bit integer.
 *
 * You must write an algorithm that runs in O(n) time and without using the division operation.
 */

public class ProductOfArrExceptSelf {
    public static int[] productExceptSelf(int[] nums) {
        int[] result=new int[nums.length];
        Arrays.fill(result,1);
        int prefix=1;
        int post=1;
        result[0]=prefix;
        for(int i =1;i<nums.length;i++){
            result[i]=prefix*nums[i-1];
            prefix=result[i];
        }
        System.out.println("prefix value in result : "+ Arrays.toString(result));

        result[nums.length-1]=post*result[nums.length-1];
        for(int j =nums.length-2;j>=0;j--){
            post=post*nums[j+1];
            result[j]=post*result[j];
        }
        System.out.println("post multiply to result  : "+Arrays.toString(result));
        return result;
    }

    public static void main(String[] args) {
        int[] result=productExceptSelf(new int[]{1,2,3,4});
        System.out.println("Array I/P : [1,2,3,4] = " + Arrays.toString(result));
    }
}
