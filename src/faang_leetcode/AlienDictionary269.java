package faang_leetcode;

import java.util.*;

public class AlienDictionary269 {
    //MostAsked
    //TimeComplexixity - O(c) cis unique char
    //SpaceComplexity - O(1)

    public static Map<Character,List<Character>> reversedList=new HashMap<>();
    public static Map<Character,Boolean> seen=new HashMap<>();
    public static StringBuilder result=new StringBuilder();

    public static String alienOrder(String[] words){
        System.out.println("words = " + Arrays.toString(words));

        //Storing unique char in reversedList
        for(String word:words){
            for(Character c:word.toCharArray()){
                reversedList.putIfAbsent(c,new ArrayList<>());
            }
        }
        System.out.println("reversedList = " + reversedList);

        //Iterating for all words
        for(int i =0;i<words.length-1;i++){
            String word1=words[i];
            String word2=words[i+1];

            System.out.println("word1 = " + word1+" && word2 = "+word2);
            //Checking for prefix ex batman bat
            if(word1.length()>word2.length() && word1.startsWith(word2)){
                return "";
            }

            //Addeded dependencies to be fulfilled in List of unique char
            for(int j =0;j<Math.min(word1.length(),word2.length());j++){
                if(word1.charAt(j)!=word2.charAt(j)){
                    reversedList.get(word2.charAt(j)).add(word1.charAt(j));
                    break;
                }
            }
        }
        System.out.println(" reversedList "+reversedList);

        //calling dfc for all key
        for(Character c: reversedList.keySet()){
            boolean res=dfs(c);
            //if false return
            if(!res) return "";
        }

        if(result.length()<reversedList.size()){
            return "";
        }

        return result.toString();
    }

    private static boolean dfs(Character c) {
        System.out.println("Inside DFS Method ,the value of char = " + c);
        //Checking in seen all key and putting true or false
        if(seen.containsKey(c)){
            return seen.get(c);
        }

        seen.put(c,false);

        //checking for all char corresponding to unique key
        for(Character next:reversedList.get(c)){
            System.out.println("Inside reversedList : "+ next);
            boolean res=dfs(next);
            if(!res) return false;
        }

        seen.put(c,true);
        System.out.println(" seen map : "+ seen);
        result.append(c);
        System.out.println(" Inside dfs result : "+ result);
        return true;
    }

    public static void main(String[] args) {
        String[] words={"wrt","wrf","er","ett","rftt"};
        String result=alienOrder(words);
        System.out.println("result = " + result);
    }
}
