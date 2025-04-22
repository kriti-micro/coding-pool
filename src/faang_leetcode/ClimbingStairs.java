package faang_leetcode;

import java.util.Arrays;

public class ClimbingStairs {
    //https://leetcode.com/problems/climbing-stairs/description/
    //Dynamic programming
    public static int climbStairs(int n) {
        if(n==1){
            return 1;
        }
        int one=1;
        int two=2;
        for(int i=3;i<=n;i++){
            int total=one +two;
            System.out.println("one = " + one +" two ="+ two);
            one=two;
            two=total;
        }
        return two;
    }

    public static void main(String[] args) {
        int n=5;
        int noOfWays = climbStairs(n);
        System.out.println("n = "+n+", noOfWays to climb stairs = " + noOfWays);
    }
}
