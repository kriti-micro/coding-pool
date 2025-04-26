package faang_leetcode;

import java.util.*;
//https://leetcode.com/problems/word-break/
//Dynamic programming [bottom up approach ] O(n^3) better than O(2^n) for brute force
public class WordBreak {
    public static boolean wordBreak(String s, List<String> wordDict) {
        System.out.println("s = " + s + ", wordDict = " + wordDict);
        boolean[] dp=new boolean[s.length()+1];
        dp[0]=true;
        Set<String> wordSet=new HashSet<>(wordDict);
        for(int i=1;i<=s.length();i++){
            //for(int j=0;j<i;j++){ Time was increasing because at bottom if we get true the we dont need to check for other substring but i=0 we have to check full
            for(int j=i-1;j>=0;j--){
                System.out.println(" substring("+j+","+i+") = "+s.substring(j,i));
                if(dp[j] && wordSet.contains(s.substring(j,i))){
                    System.out.println(" dp[" + j+"] = "+dp[j]);
                    dp[i]=true;
                    break;
                }
            }
            System.out.println(" dp[" + i + "] " + dp[i]);
        }
        return dp[s.length()];
    }

    public static void main(String[] args) {
        String s="ESPN";
        List<String> list= Arrays.asList(new String[]{"E","SP","N"});
        boolean flag=wordBreak(s,list);
        System.out.println("The word is made from dictionaryList = " + flag);
    }
}
