package dsa.arrays;

import java.util.Arrays;
import java.util.HashSet;

public class L238_ProductExceptSelf {
    //complexity=O(2n) ,space=O(1) no extra var required excluding i/p n o/p arr
    public static int[] productExceptSelf(int[] arr){
        System.out.println("arr = " + Arrays.toString(arr));
        int pre=1,post=1;
        int size= arr.length;
        int[] result =new int[arr.length];
        Arrays.fill(result,1);
        for(int i=0;i<size;i++){
            result[i]=pre;
            pre=pre*arr[i];
            System.out.println("Value of pre : "+pre);
        }
        System.out.println(Arrays.toString(result));

        for(int i=size-1;i>=0;i--){
            result[i]=result[i]*post;
            post = post * arr[i];
            System.out.println("Value of post : "+post);
        }
        System.out.println(Arrays.toString(result));

        return result;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,2,3,4};
        System.out.println("The product except self : "+Arrays.toString(productExceptSelf(arr)));
    }
}
