package dsa.arrays;

import java.util.Arrays;
import java.util.HashMap;

public class L953_AlienDicVerification {

    //Timecomplexity O(n) n Space O(n)
    public static void main(String[] args) {
        String[] words= new String[]{"will","chris","bat","batman","batwoman"};
        String order="wcabdefhgjilknmopqrsuxtvyz";
        boolean result = isAlienSorted(words,order);
        System.out.println("The result for "+result);
        boolean result1 = isAlienSortedUsingArray(words,order);
        System.out.println("The result for "+result1);
    }

    private static boolean isAlienSortedUsingArray(String[] words, String order) {
        int[] rank=new int[26];

        for(int i = 0; i<26;i++){
            rank[order.charAt(i)-'a']=i;
        }
        System.out.println(" rank array : "+Arrays.toString(rank));
        for(int i=0;i<words.length-1;i++){
            String w1=words[i];
            String w2=words[i+1];
            int length=Math.min(w1.length(),w2.length());
            boolean different=false;
            for (int j = 0; j < length; j++) {
                if(w1.charAt(j)!=w2.charAt(j)) {
                    if (rank[w1.charAt(i) - 'a'] > rank[w2.charAt(i) - 'a']) {
                        return false;
                    }
                    different=true;
                    break;
                }
            }
            // prefix case: "batman" before "bat"
            if(!different && w1.length()>w2.length()){
                return false;
            }

        }

        return true;
    }

    private static boolean isAlienSorted(String[] words, String order) {
        System.out.println("words = " + Arrays.toString(words) + ", order = " + order);
        HashMap<Character,Integer> map=new HashMap<>();
        //store the order in map
        for (int i = 0; i < order.length(); i++) {
            map.put(order.charAt(i),i);
        }
        
        for (int i = 0; i < words.length-1; i++) {
            for (int j = 0; j < words[i].length(); j++) {
                //bat vs batman
                if(j>words[i+1].length()){
                    return false;
                }
                System.out.println(words[i]+" "+words[i+1]);
                //compare each letter and if different
                if(words[i].charAt(j)!=words[i+1].charAt(j)){
                    int currentLetter=map.get(words[i].charAt(j));
                    int nextLetter=map.get(words[i+1].charAt(j));
                    System.out.println(words[i]+" "+words[i+1]+" "+currentLetter+" "+nextLetter);
                    if(currentLetter>nextLetter){
                        return false;
                    }else{
                        break;
                    }
                }
            }
        }
        return true;
    }
}
