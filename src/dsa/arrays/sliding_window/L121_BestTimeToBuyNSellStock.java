package dsa.arrays.sliding_window;

import java.util.Arrays;

public class L121_BestTimeToBuyNSellStock {

    //Time Complexity -O(n) n O(1) Dynamic window approach
    public static void main(String[] args) {
        int[] prices=new int[]{7,1,5,3,6,4};
        int profit=maxProfit(prices);
        System.out.println(" The best time to buy and sell stock : "+profit);
    }

    private static int maxProfit(int[] prices) {
        System.out.println("prices = " + Arrays.toString(prices));
        int profit=0;
        int buy=prices[0];
        for (int i = 0; i < prices.length; i++) {
            System.out.println("  indices current : "+prices[i]);
            if(prices[i]<buy){
                buy=prices[i];//move left pointer
            }
            profit=Math.max(profit,prices[i]-buy);//window evalution
            System.out.println("Profit : "+profit+" Buy : "+buy);
        }
        return profit;
    }
}
