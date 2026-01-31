package dsa.arrays.sliding_window;

import java.util.Arrays;

public class L567_PermutationInString {
    public static void main(String[] args) {
        String s1="ba";
        String s2="eidbaooo";
        boolean result = checkInclusion(s1,s2);
        System.out.println(" The s1 permutations present in s2 : "+result);
    }

    private static boolean checkInclusion(String s1, String s2) {
        System.out.println("s1 = " + s1 + ", s2 = " + s2);
        if(s1.length()>s2.length()){
            return false;
        }

        int[] s1Map=new int[26];
        int[] s2Map=new int[26];

        // Initialize frequency maps for s1 and the first window of s2
        for (int i = 0; i < s1.length(); i++) {
            s1Map[s1.charAt(i)-'a']++;
            s2Map[s2.charAt(i)-'a']++;
        }
        System.out.println("s1Map : "+Arrays.toString(s1Map));
        System.out.println("s2Map : "+Arrays.toString(s2Map));
        // Slide the window through s2 and compare the maps
        for (int i = 0; i < s2.length()-s1.length(); i++) {
            System.out.println("Chars in window "+i+" : "+s2.charAt(i)+" "+s2.charAt(i+1));
            if(match(s1Map,s2Map)){
                return true;
            }
            System.out.println("Removing char : "+s2.charAt(i)+" adding char : "+s2.charAt(i+s1.length()));
            s2Map[s2.charAt(i+s1.length())-'a']++;// Add new character to the window
            s2Map[s2.charAt(i)-'a']--; // Remove old character from the window
            System.out.println("s2Map : "+Arrays.toString(s2Map));
        }
        // Check the last window
        return match(s1Map,s2Map);
    }

    // Helper function to compare two frequency maps
    private static boolean match(int[] s1Map, int[] s2Map) {
        for (int i = 0; i < 26; i++) {
            if(s1Map[i]!=s2Map[i]){
                return false;
            }
        }
        return true;
    }
}
