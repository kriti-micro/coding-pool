package dsa.sliding_window;

import java.util.HashSet;

public class L3_LongestSubstringWithoutRepeatingChar {

    //Time -O(n) and Space - O(1) with brute force it is n^3
    /*We use a sliding window, Use 2 pointers left and right */
    public static void main(String[] args) {
        String str="abatman";

        int len=lengthOfLongestSubstring(str);
        System.out.println(" The Longest non repeating character  :"+len);
    }

    private static int lengthOfLongestSubstring(String str) {
        System.out.println("str = " + str);
        if(str==null || str.isEmpty()){
            return 0;
        }
        if(str.length()==1){
            return 1;
        }
        HashSet<Character> vistedChar=new HashSet<>();
        int left=0;
        int right=0;
        int max=0;
        while(right<str.length()){
            char c=str.charAt(right);
            System.out.println("left : right " + left+":"+right+" char :"+c);
            System.out.println("vistedChar = " + vistedChar);
            if(vistedChar.contains(c)){
                vistedChar.remove(str.charAt(left));
                left++;
            }
            vistedChar.add(c);
            max=Math.max(max,right-left+1);
            right++;
        }
        System.out.println("left : right " + left+":"+right);
        System.out.println("vistedChar = " + vistedChar);
        return max;
    }
}
