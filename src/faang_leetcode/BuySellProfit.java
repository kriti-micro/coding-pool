package faang_leetcode;

/*
 You are given an array prices where prices[i] is the price of a given stock on the ith day.
You want to maximize your profit by choosing a single day to buy one stock and choosing a different day in the future to sell that stock.
Return the maximum profit you can achieve from this transaction. If you cannot achieve any profit, return 0.
 https://leetcode.com/problems/best-time-to-buy-and-sell-stock/description/
 */

import java.util.Arrays;

public class BuySellProfit {
    public static int maxProfit(int[] prices) {
        int buy = prices[0];
        int profit =0 ;
        for (int i=0;i<prices.length-1;i++) {
            if (prices[i] < buy) {
                buy = prices[i];
            }
            profit=Math.max(profit,prices[i+1]-buy);
        }
        return profit;
    }

    public static void main(String[] args) {
        int profit=maxProfit(new int[]{7,1,5,3,6,4});
        System.out.println("profit  for arr {7,1,5,3,6,4}= " + profit);
    }
}
