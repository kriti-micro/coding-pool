package dsa.arrays;

import java.util.Arrays;
import java.util.HashMap;

public class L1_TwoSum {
    //O(n) returning indices of target sum
    public static int[] twoSum(int[] arr,int target){
        HashMap<Integer,Integer> map=new HashMap<>();

                for(int i=0;i<arr.length;i++){
                    int diff=target-arr[i];
                    System.out.println("Map present in : "+map);
                    if(map.containsKey(diff)){
                        return new int[]{map.get(diff),i};
                    }
                    map.put(arr[i],i);
                }

        return new int[]{-1,-1};
    }
    public static void main(String[] args) {
        int[] arr=new int[]{6,7,2,15};
        int[] indices=twoSum(arr,9);
        System.out.println("indices for target 9 = " + Arrays.toString(indices));
    }
}
