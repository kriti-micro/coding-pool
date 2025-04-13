package SHL;

import java.util.Arrays;

public class RemoveVowels {
    public static void main(String[] args) {
        String s="codeFAANGdecode";
        //to remove vowels
        String result = s.replaceAll("[aeiouAEIOU]","");
        System.out.println("Result after vowel removal of 'codeFAANGdecode' = " + result);
    }
}
