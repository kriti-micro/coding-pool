package SHL;

import java.util.Arrays;

public class StringMatching {

/**
 Java algorithm to count the number of occurrences of the second string (pattern) in the first string (text),
 including overlapping occurrences.
 **/
    public static void main(String[] args) {
        String text="ababababa";
        String pattern="aba";
        int noOfOccurences = countThePattern(text,pattern);
        System.out.println("noOfOccurences = " + noOfOccurences);
    }

    private static int countThePattern(String text, String pattern) {
        int count = 0;
        if (text==null || pattern==null || pattern.isEmpty()){
            return 0;
        }
        for(int i=0;i<=text.length()-pattern.length();i++){
            if(text.substring(i,i+pattern.length()).equals(pattern))
            {
                count++;
            }
        }
        return count;
    }
}
