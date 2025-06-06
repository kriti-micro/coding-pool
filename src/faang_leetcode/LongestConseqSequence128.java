package faang_leetcode;

import java.util.*;

//O(n) O(n^3)-BruteForce (nlogn)-using sorting
public class LongestConseqSequence128 {
    public static int longestConsecutive(int[] nums) {
        System.out.println("nums = " + Arrays.toString(nums));

        if(nums.length == 0){
            return 0;
        }

        HashSet<Integer> numSet=new HashSet<>();
        for(int i =0;i<nums.length;i++){
            numSet.add(nums[i]);
        }
        System.out.println("numSet = " + numSet);
        int longestSeq=1;

        for(int num : numSet){
            if(numSet.contains(num-1)){
                continue;
            }else{
                int currentNum=num;
                int currentSeq=1;
                System.out.println("currentNum = " + currentNum);
                System.out.println("currentSeq = " + currentSeq);

                while(numSet.contains(currentNum+1)){
                    currentNum++;
                    currentSeq++;
                }

                longestSeq=Math.max(longestSeq,currentSeq);
            }
        }
        return longestSeq;
    }

    public static void main(String[] args) {
        int nums[]={100,4,200,1,2,3};
        int result=longestConsecutive(nums);
        System.out.println("longest consequtive Subsequence = " + result);
    }
}
