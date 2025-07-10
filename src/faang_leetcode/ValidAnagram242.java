package faang_leetcode;

import java.util.Arrays;

public class ValidAnagram242 {
    public static void main(String[] args) {
        String s="anagram";
        String t="nagaram";
        boolean isValidAnagram=isAnagramUsingCount(s,t);
        System.out.println(" Is it valid Anagram using count : "+isValidAnagram);
        boolean isValidAnagramUsingSort=isAnagramUsingSort(s,t);
        System.out.println(" Is it valid Anagram using Sort : "+isValidAnagramUsingSort);
    }

    //Time n space-O(nlogn) n O(n)
    private static boolean isAnagramUsingSort(String s, String t) {

        if(s.length() != t.length()){
            return false;
        }

        char[] sArr=s.toCharArray();
        char[] tArr=t.toCharArray();

        Arrays.sort(sArr);
        Arrays.sort(tArr);

        return Arrays.equals(sArr,tArr);
    }

    //Time n space-O(n) n O(1)
    private static boolean isAnagramUsingCount(String s, String t) {
        System.out.println("s = " + s + ", t = " + t);
        if(s.length() != t.length()){
            return false;
        }
        int[] count=new int[26];// size 26 (for 'a' to 'z') initialize with 0
        for (int i = 0; i < s.length(); i++) {
            System.out.println(" char in s : "+s.charAt(i)+" char in t : "+t.charAt(i));

            count[s.charAt(i)-'a']++;
            count[t.charAt(i)-'a']--;
        }
        for (int i = 0; i < 26; i++) {
            if(count[i]!=0){
                return false;
            }
        }

        return true;
    }
}
