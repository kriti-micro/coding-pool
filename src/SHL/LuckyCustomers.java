package SHL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

public class LuckyCustomers {
    /*
    * The manager of a supermarket wishes to hold an event at which he will distribute gift baskets to
    *  lucky customers. Each gift basket contains a pair of products.
    * Each basket contains different product pairs, but the overall value of t baskets may be the same.
    *  There are N types of products and each product has a price.
    * The gift baskets will awarded to the customers that pick a product pair that has a difference in price
    * equal to the given integer value Write an algorithm to help the Manager
    * find the total numbers of lucky customers who will win a gift basket in java
    * Input
The first line of the input consists of an integer - list_ input_size, representing the types of products (N).
The second line consists of N space-separated integers - list_input[0], list_input[1],  list_input[ N−1], representing the price of the products.
The last line consists of an integer - K input, representing the given value K .
* I/P List of Product prices = [5 20 3 2 50 80]
    * */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter the  number of products " );
        // First line: number of products
        int n = Integer.parseInt(scanner.nextLine());
        System.out.println("Enter the  space-separated prices " );
        // Second line: space-separated prices
        // IP -- 5 20 3 2 50 80
        int[] prices = new int[n];
        String[] priceStr = scanner.nextLine().split(" ");
        for (int i = 0; i < n; i++) {
            prices[i] = Integer.parseInt(priceStr[i]);
        }

        // Third line: value of K
        System.out.println("Enter the  target difference to be compared " );
        int k = Integer.parseInt(scanner.nextLine());

        int result = countLuckyCustomers(prices, k);
        System.out.println("the Lucky customers are : "+result);
    }

    private static int countLuckyCustomers(int[] prices, int k) {
        HashSet<Integer> seen=new HashSet<>();
        HashSet<String> uniquePairs=new HashSet<>();
        for(int price:prices){
            if(seen.contains(price-k)){
                int a=price;
                int b=price-k;
                uniquePairs.add(Math.min(a,b)+":"+Math.max(a,b));

            }
            if(seen.contains(price+k)){
                int a=price;
                int b=price+k;
                uniquePairs.add(Math.min(a,b)+":"+Math.max(a,b));

            }
            seen.add(price);
        }
        System.out.println("seen = " + seen);
        System.out.println("uniquePairs = " + uniquePairs);
        return uniquePairs.size();
    }

}
