package dsa.arrays;

import java.util.Arrays;

public class L41_FirstMissingPositive {
    //O(n) & O(1)
    //We use the array indices as a hash to mark presence
    public static void main(String[] args){
        int[] nums=new int[]{3,4,-1,1};
        int missingPositiveInteger=firstMissingPositiveInteger(nums);
        System.out.println(" the first missing positive integer : "+missingPositiveInteger);
    }

    private static int firstMissingPositiveInteger(int[] nums) {
        System.out.println("nums = " + Arrays.toString(nums));
        int contains=0;
        int n = nums.length;

        //checking if 1 exists as first positive integer
        for (int i = 0; i < nums.length; i++) {
            if(nums[i]==1){
                contains++;
            }
        }
        if(contains==0){
            return 1;
        }

        //Replace negatives, zeros, and large numbers with 1. Only care about numbers 1 to n
        for (int i = 0; i < nums.length; i++) {
            if(nums[i]<=0 || nums[i]>n){
                nums[i]=1;
            }
        }
        System.out.println("nums = " + Arrays.toString(nums));

        //Main logic of putting - of index
        //If number x exists → mark nums[x] as negative .Use nums[0] to represent number n
        for (int i = 0; i < nums.length; i++) {
            int a=Math.abs(nums[i]);
            if(a==n){
                nums[0]=-Math.abs(nums[0]);
            }else{
                nums[a]=-Math.abs(nums[a]);
            }
        }
        System.out.println("nums = " + Arrays.toString(nums));

        //return index if value is positive
        for (int i = 0; i < nums.length; i++) {
            if(nums[i]>0){
                return i ;
            }
        }

        if(nums[0]>0){
            return n;
        }

        return n+1;

    }

}
