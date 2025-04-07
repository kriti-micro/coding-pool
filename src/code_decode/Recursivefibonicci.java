package code_decode;

import java.util.Arrays;
import java.util.Scanner;

public class Recursivefibonicci {
    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int input=sc.nextInt();
        System.out.println("Fib Series = " + fib(input));
    }
    public static int fib(int n){
        if(n<=1)
                return n;
        else
            return fib(n-1)+fib(n-2);
    }
    //0 1 1 2 3 5 8 13 21 34 55
}
