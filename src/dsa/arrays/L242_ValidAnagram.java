package dsa.arrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class L242_ValidAnagram {



    public static void main(String[] args) {

        //Time complexity : O(n) space : O(1) Fastest
        System.out.println("Is string anagram : "  + " : "+verifyAnagramUsingArray("cart","trac"));
        //Time complexity : O(n) space : O(n) Flexible [For any char]
        System.out.println("Is string anagram : "  + " : "+verifyAnagramUsingHM("listen","silent"));
        //Time complexity : O(n) space : O(n+) Heavy
        System.out.println("Is string anagram : "  + " : "+verifyAnagramUsingStreams("listen","silent"));
        //Time complexity : O(nlogn) space : O(1) Not Optimal
        System.out.println("Is string anagram : "  + " : "+verifyAnagramUsingSorting("listen","silent"));

    }

    private static boolean verifyAnagramUsingSorting(String s, String t) {
        System.out.println("verifyAnagramUsingSorting : s = " + s + ", t = " + t);
        char[] a1=s.toCharArray();
        char[] a2=t.toCharArray();
        Arrays.sort(a1);
        Arrays.sort(a2);
        System.out.println("a1 : "+Arrays.toString(a1)+" , a2 : "+Arrays.toString(a2));
        return Arrays.equals(a1, a2);
    }

    private static boolean verifyAnagramUsingStreams(String s, String t) {
        System.out.println("verifyAnagramUsingStreams : s = " + s + ", t = " + t);
        //converting Intstream to Character
        //Map<Character,Long> map1=s.chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(c->c, Collectors.counting()));
        //converting Intstream to Integer
        Map<Integer,Long> map1=s.chars().boxed().collect(Collectors.groupingBy(c->c, Collectors.counting()));
        Map<Integer,Long> map2=s.chars().boxed().collect(Collectors.groupingBy(c->c, Collectors.counting()));
        System.out.println(map1+" : map 2 : "+ map2);

        return map1.equals(map2);
    }

    private static boolean verifyAnagramUsingArray(String s, String t) {
        if(s.length()!=t.length()){
            return false;
        }
        System.out.println("Best way to do using verifyAnagramUsingArray : s = " + s + ", t = " + t);
        int[] charArr =new int[26];
        for(int i=0;i<s.length();i++){
            System.out.println((int)s.charAt(i)+"-"+(int)'a' +" : "+ (s.charAt(i)-'a'));
            charArr[s.charAt(i)-'a']++;
            charArr[t.charAt(i)-'a']--;
        }
        System.out.println(" charArr : "+Arrays.toString(charArr));
        for(int count : charArr){
            if(count!=0){
                return false;
            }
        }
        return true;
    }

    private static boolean verifyAnagramUsingHM(String s, String t) {
        if(s.length()!=t.length()){
            return false;
        }
        System.out.println("verifyAnagramUsingHM : s = " + s + ", t = " + t);
        HashMap<Character,Integer> map=new HashMap<>();
        for(char c : s.toCharArray()){
            map.put(c,map.getOrDefault(c,0)+1);
        }

        for(char c : t.toCharArray()){
            if(!map.containsKey(c)){
                return false;
            }
            map.put(c,map.get(c)-1);
            if(map.get(c)==0){
                map.remove(c);
            }
        }


        return map.isEmpty();
    }
}
