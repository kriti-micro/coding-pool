package faang_leetcode;

import java.util.Arrays;

public class SearchInRotatedSortArr {
    public static int findTarget(int[] nums,int target) {
        System.out.println("Rotated Sorted Arr nums = " + Arrays.toString(nums));

        int left=0;
        int right=nums.length-1;

        while(left<=right){ //imp point to put equals sign
            int mid=(left+right)/2;
            if(nums[mid]==target){
                return mid;
            }
            System.out.println("index left = " +left+" mid = "+mid+" right = "+right);
            System.out.println("value left = " +nums[left]+" mid = "+nums[mid]+" right = "+nums[right]);
            //To check from left to mid if it is sorted or not
            if(nums[left]<=nums[mid]) {
                if (target < nums[left] || target > nums[mid]) { //imp point : no equals sign
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }else{
                //Right side i.e. from mid to right is sorted
                if(target<nums[mid] || target>nums[right]){
                    right=mid-1;
                }else{
                    left=mid+1;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) {
        int ans=findTarget(new int[]{2,3,4,5,6,7,8,0,1},5);
        //int ans=findTarget(new int[]{2,1},1);

        System.out.println("Target found in index of rotated sorted arr = " + ans);
    }
}
