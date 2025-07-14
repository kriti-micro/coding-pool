package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class GroupAnagram49 {
    public static void main(String[] args) {
        String[] strs=new String[]{"eat","tea","tan","ate","nat","bat"};
        List<List<String>> anagramGroupList=groupAnagrams(strs);
        System.out.println(" the anagramGroupList : "+anagramGroupList);
    }

    private static List<List<String>> groupAnagrams(String[] strs) {
        if (strs.length == 0) {
            return new ArrayList<>();
        }

        HashMap<String,List<String>> hm=new HashMap<>();
        int count[]=new int[26];
        for(String str:strs){

            Arrays.fill(count,0);
            for(char c : str.toCharArray()){
                count[c-'a']++;
            }

            StringBuilder sb=new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append('#');
                sb.append(count[i]);
            }
            System.out.println(" String s = "+str+" sb "+sb);
            String key = sb.toString();
            if(!hm.containsKey(key)){
                hm.put(key,new ArrayList<>());
            }
            hm.get(key).add(str);
        }
        System.out.println(" HM values : "+hm.values());
        return new ArrayList<>(hm.values());
    }
}
