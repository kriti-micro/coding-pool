package practise;

import java.util.HashMap;

public class LongestSubstringWithDistinctKChar {
    public static int longestSubstring(String s,Integer k){
        if(s==null || s.isEmpty() || k==0){
            return 0;
        }
        HashMap<Character,Integer> freq=new HashMap<>();
        int left=0;
        int max=0;
        for(int  right=0;right<s.length();right++){
            Character c=s.charAt(right);
            // Add/update character frequency
            freq.put(c,freq.getOrDefault(c,0)+1);
            // Shrink window if distinct characters > k
            while(freq.size()>k){
                char ch=s.charAt(left);
                freq.put(ch,freq.get(ch)-1);
                if(freq.get(ch)==0){
                    freq.remove(ch);
                }
                left++;
            }
            // Current window has <= k distinct characters
            max=Math.max(max,right-left+1);
        }
        return max;
    }
    public static void main(String arg[]){
        String s="eaebce";
        int k =2;
        int length=longestSubstring(s,k);
        System.out.println(length);
    }
}
