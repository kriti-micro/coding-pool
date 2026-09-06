package practise;

import java.util.Arrays;
import java.util.HashMap;

public class L2_LongestSubstringWithoutRepeatingChar {

    //Return Length of longest Substring
    public static int slidingWindowApproach(String word){
        HashMap<Character,Integer> map=new HashMap<>();

        int left=0;
        int maxLength=0;
        for(int right=0;right<word.length();right++){
            Character c=word.charAt(right);

            //If I've seen this character before AND
            // its previous occurrence is still inside my current window,
            // move left past that previous occurrence.
            if(map.containsKey(c) && map.get(c)>=left){
                left=map.get(c)+1;
            }
            map.put(c,right);
            maxLength=Math.max(maxLength,right-left+1);
            System.out.println("-----char : "+c+" --------------");
            System.out.println(" left : "+left+" right : "+right);
            System.out.println("map : "+map+" length : "+maxLength);
            System.out.println(" substring : "+ word.substring(left,right+1));
        }

        return maxLength;
    }

    //Return longest Substring
    public static String slidingWindowApproach1(String word){
        HashMap<Character,Integer> lastIndex=new HashMap<>();

        int left=0;
        int maxLength=0;
        int windowLength=0;
        int bestStart=0;
        for(int right=0;right<word.length();right++){
            Character c=word.charAt(right);

            if(lastIndex.containsKey(c) && lastIndex.get(c)>=left){
                left=lastIndex.get(c)+1;
            }
            lastIndex.put(c,right);
            windowLength=right-left+1;
            //Main logic to return String
            //I just found a bigger substring, so remember its starting position.
            if(windowLength>maxLength){
                bestStart=left;
            }
            maxLength=Math.max(maxLength,right-left+1);

            System.out.println("-----char : "+c+" --------------");
            System.out.println(" left : "+left+" right : "+right+ " bestStart : "+bestStart);
            System.out.println("map : "+lastIndex+" length : "+maxLength);
            System.out.println(" substring : "+ word.substring(bestStart,bestStart+maxLength));
        }

        return word.substring(bestStart,bestStart+maxLength);
    }

    public static void main(String[] args) {
        String word="abcabcbb";
        String word1="abcabcdabcde";
        int longestSubstringlength=slidingWindowApproach(word);
        System.out.println("longestSubstring length : " + longestSubstringlength);
        System.out.println("longestSubstring calling 2 method : ");
        System.out.println("longestSubstring  : " + slidingWindowApproach1(word1));
        slidingWindowApproach1("")  ;     // returns ""   — max stays 0, substring(0,0) = ""
        slidingWindowApproach1("aaa")   ; // returns "a"  — every window is length 1
        slidingWindowApproach1("abcdef") ;// returns "abcdef" — no duplicates, entire string
        slidingWindowApproach1("pwwkew") ;// returns "wke" — not "pwwke"!
    }
}
