package dsa.arrays.sliding_window;

import java.util.Arrays;

public class L424_LongestRepeatingCharacterReplacement {
    //Time -O(n) and Space - O(1)
    /*We use a sliding window, track the most frequent character, and ensure that replacements needed don’t exceed k.
    This allows us to find the longest valid substring in linear time.*/
    public static void main(String[] args) {
        String str="AABABBA";
        int k=1;
        int len=characterReplacement(str,k);
        System.out.println(" The Longest repeating character replacement :"+len);
    }

    private static int characterReplacement(String str, int k) {
        System.out.println("str = " + str + ", k = " + k);
        int ans=0;
        int left=0;
        int maxOcur=0;
        int[] occurances =new int[26];
        for (int right = 0; right < str.length(); right++) {
            System.out.println(" substring : "+ str.substring(left,right+1));
            maxOcur=Math.max(maxOcur,++occurances[str.charAt(right)-'A']);
            System.out.println(" Left : "+left+" Right : "+right+" maxOccurances : "+maxOcur);
            System.out.println(" occurances : "+ Arrays.toString(occurances));
            if(right-left+1-maxOcur>k){
                occurances[str.charAt(left)-'A']--;
                left++;
            }
            ans=Math.max(ans,right-left+1);
            System.out.println(" Ans : "+ans);
        }
        return ans;
    }
}
