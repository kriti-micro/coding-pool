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
        HashMap<Character,Integer> map=new HashMap<>();

        int left=0;
        int maxLength=0;
        int windowLength=0;
        int bestStart=0;
        for(int right=0;right<word.length();right++){
            Character c=word.charAt(right);

            if(map.containsKey(c) && map.get(c)>=left){
                left=map.get(c)+1;
            }
            map.put(c,right);
            windowLength=right-left+1;
            maxLength=Math.max(maxLength,right-left+1);
            //Main logic to return String
            if(windowLength>maxLength){
                bestStart=left;
            }
            System.out.println("-----char : "+c+" --------------");
            System.out.println(" left : "+left+" right : "+right+ " bestStart : "+bestStart);
            System.out.println("map : "+map+" length : "+maxLength);
            System.out.println(" substring : "+ word.substring(bestStart,bestStart+maxLength));
        }

        return word.substring(bestStart,bestStart+maxLength);
    }

    public static void main(String[] args) {
        String word="abcabcbb";
        int longestSubstringlength=slidingWindowApproach(word);
        System.out.println("longestSubstring length : " + longestSubstringlength);
        System.out.println("longestSubstring calling 2 method : ");
        System.out.println("longestSubstring  : " + slidingWindowApproach1(word));
    }
}
