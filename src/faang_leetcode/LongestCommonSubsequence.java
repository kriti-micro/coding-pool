package faang_leetcode;

import java.util.Arrays;

//https://leetcode.com/problems/longest-common-subsequence/
//Dynamic programming O(m*n) Space - O(m*n)

public class LongestCommonSubsequence {
    public static int longestCommonSubsequence(String text1, String text2) {
        int matrix[][]=new int[text1.length()+1][text2.length()+1];
        //We are taking -1 of length because the last row for null element is set to 0 by default so no need to fill it.
        for(int i=text1.length()-1;i>=0;i--){
            for(int j=text2.length()-1;j>=0;j--){
                if(text1.charAt(i)== text2.charAt(j)){ //equals don't work for character
                    matrix[i][j]=1+matrix[i+1][j+1];
                }else{
                    matrix[i][j]= Math.max(matrix[i+1][j],matrix[i][j+1]);
                }
                System.out.println("matrix[" + i + "]["+ j +"]  = "+matrix[i][j]);
            }
        }
        return matrix[0][0];
    }

    public static void main(String[] args) {
        String text1="john";
        String text2="ron";
        int lcs=longestCommonSubsequence(text1,text2);
        System.out.println("longest common subsequence where text1 = "+text1+", text2 = " +text2 +", LCS = "+ lcs);
    }
}
