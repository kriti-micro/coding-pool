package faang_leetcode;

import java.util.Arrays;

public class MinInRotatSortArr {
    //Min in Rotated sorted array using Binary Search O(logn)
    public static int findMin(int[] nums) {
        System.out.println("Rotated Sorted Arr nums = " + Arrays.toString(nums));

        int left=0;
        int right=nums.length-1;
        int ans=nums[0];
        if(nums.length==1){
            return ans;
        }
        while(left<=right){ //imp point to put equals sign
            System.out.println("ans = " + ans);
            if(nums[left]<nums[right]){
                ans=Math.min(ans,nums[left]);
            }
            int mid=(left+right)/2;
            System.out.println("index left = " +left+" mid = "+mid+" right = "+right);
            System.out.println("value left = " +nums[left]+" mid = "+nums[mid]+" right = "+nums[right]);
            ans=Math.min(ans,nums[mid]);
            if(nums[left]<=nums[mid]){ //imp point to put equals sign
                left=mid+1;
            }else{
                right=mid-1;
            }
        }
        return ans;
    }

    public static void main(String[] args) {
        //int ans=findMin(new int[]{2,3,4,5,6,7,8,0,1});
        int ans=findMin(new int[]{2,1});

        System.out.println("ans for min in rotated sorted arr = " + ans);
    }
}
