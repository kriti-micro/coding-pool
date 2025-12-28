package dsa.arrays;

import java.util.*;

public class L49_GroupAnagram {
    public static void main(String[] args) {

        String[] strArr=new String[]{"eat","tea","tan","ate","nat","bat"};
        //Time complexity : O(nxk) space : O(nxk) k=avg length of the string , optimal solution
        System.out.println("Group Anagram : "  + " : "+groupAnagram(strArr));
        System.out.println("Group Anagram using Optimized code : "  + " : "+groupAnagramOptimizedCode(strArr));


    }

    private static List<List<String>> groupAnagramOptimizedCode(String[] strArr) {
        if(strArr.length==0){
            return new ArrayList<>();
        }
        HashMap<String,List<String>> map=new HashMap<>();
        int[] count =new int[26];
        //O(n)
        for(String str: strArr){
            Arrays.fill(count,0);
            //O(k)
            for(char c : str.toCharArray())    {
                count[c-'a']++;
            }
            // O(1) → 26 fixed
            StringBuilder sb = new StringBuilder("");
            for (int i=0;i<26;i++) {
                sb.append("#").append(count[i]);
            }

            String key=sb.toString();
            // Single lookup + insert
            map.computeIfAbsent(key,k->new ArrayList<String>()).add(str);
        }

        return new ArrayList<>(map.values());
    }

    private static List<List<String>> groupAnagram(String[] strArr) {
        System.out.println("strArr = " + Arrays.toString(strArr));
        List<List<String>> result=new ArrayList<>();
        HashMap<String,List<String>> map=new HashMap<>();
        for(String str: strArr){
            System.out.println(" The str is : "+str);
            int[] arr=new int[26];
          for(char c : str.toCharArray())    {
            arr[c-'a']++;
          }
            StringBuilder sb = new StringBuilder();
          for (int i=0;i<26;i++) {
              sb.append("#").append(arr[i]);
          }
          String key=sb.toString();
            System.out.println(" the sb : "+sb);
            if(!map.containsKey(key)){
                map.put(key,new ArrayList<>());
            }
            map.get(key).add(str);

        }
        System.out.println(" the map : "+map);

        for(List<String> l : map.values()){
            result.add(l);
        }
        return result;
    }
}
