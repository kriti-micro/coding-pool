package dsa.arrays;

import java.util.Arrays;
import java.util.HashSet;

public class L128_LongestConsecutiveSequence {
    //Time O(n) Space O(n)
    public static void main(String[] args) {
        int[] arr=new int[]{100,4,200,1,3,2};
        int lcs=longestConsecutive(arr);
        System.out.println(" Longest consecutive subsequence : "+lcs);
    }

    private static int longestConsecutive(int[] arr) {
        System.out.println("arr = " + Arrays.toString(arr));
        if(arr.length==0){
            return 0;
        }
        HashSet<Integer> numset=new HashSet<>();
        for(int i=0;i< arr.length;i++){
            numset.add(arr[i]);
        }
        System.out.println(" numset : "+numset);
        int lcs=1;
        for(int num : numset){
            System.out.println(" num : "+num);
            if(numset.contains(num-1)){
                continue;
            }else{
                int currNum=num;
                int currSub=1;
                System.out.println(" currNum "+currNum+" currSub "+currSub);
                while(numset.contains(currNum+1)){
                    currSub++;
                    currNum++;
                    System.out.println(" currSub : "+currSub);
                }
                lcs=Math.max(lcs,currSub);

            }

        }


        return lcs;
    }
}
