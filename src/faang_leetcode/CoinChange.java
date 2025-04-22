package faang_leetcode;

import java.util.Arrays;

public class CoinChange {
    //https://leetcode.com/problems/coin-change/
    //Dynamic Programming O(n*m)
    //greedy approach more complexity O(m^n)m=amount and n=no of coins
    public static int coinChange(int[] coins, int amount) {
            int amountArr[]=new int[amount+1];
            Arrays.fill(amountArr,amount+1);
            amountArr[0]=0;
            for(int i=1;i<=amount;i++){
                for(int j=0;j<coins.length;j++){
                    //Main logic to check i with every coin given
                    if(i>=coins[j]){
                        amountArr[i]=Math.min(amountArr[i],1+amountArr[i-coins[j]]);
                        System.out.println(" amountArr["+i+"] = "+amountArr[i]+" , coin = " + coins[j]);
                    }
                }
            }
            if(amountArr[amount]<(amount+1)){
                return amountArr[amount];
            }
            return -1;
    }

    public static void main(String[] args) {
        int inpArr[]=new int[]{1,3,5,6};
        int amount=8;
        int ans=coinChange(inpArr,amount);
        System.out.println("inpArr = " + Arrays.toString(inpArr) +" ,amount = "+amount);
        System.out.println("The no of coins which gives the amount = "+ans);
    }
}
