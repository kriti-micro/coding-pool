package faang_leetcode;

import java.util.Arrays;

//https://leetcode.com/problems/unique-paths/description/ Q-62
//DP T=O(m*n) S=O(m*n)
public class UniquePaths {
    public static int uniquePaths(int m, int n) {
        System.out.println("m = " + m + ", n = " + n);
        int[][] dp=new int[m][n];
        for(int[] row:dp){
            Arrays.fill(row,1);
            System.out.println("row = " + Arrays.toString(row));
        }

        for (int i = 1; i<m ; i++) {
            for (int j = 1; j<n ; j++) {
                dp[i][j]=dp[i-1][j]+dp[i][j-1];
                System.out.println("dp[" + i + "][" + j+"] = "+dp[i][j]);
            }
        }

        for(int[] row:dp){
            System.out.println("final row = " + Arrays.toString(row));
        }
        return dp[m-1][n-1];
    }
    public static void main(String[] args) {
        //int result=uniquePaths(3,3);
        int result=uniquePaths(3,7);
        System.out.println(" No of unique path from which robot reaches to star : "+result);
    }
}
