package dsa.arrays;

import java.util.Arrays;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;

public class L347_TopKFrequentElement {
    //Time ->O(nlogk) Space -O(n)
    public static int[] topKFrequent(int[] arr,int k){
        System.out.println("arr = " + Arrays.toString(arr) + ", k = " + k);
        if(arr.length==k){
            return arr;
        }
        HashMap<Integer,Integer> map=new HashMap<>();
        for (int key : arr) {
            map.put(key,map.getOrDefault(key,0)+1);
        }
        System.out.println(" the map is : "+map);

        //function used for queue insetion, it is called min heap : smallest freq on top
        Queue<Integer> queue=new PriorityQueue<>((a,b)->map.get(a)- map.get(b));

        for(int key:map.keySet()){
            queue.add(key);
            if(queue.size()>k){
                System.out.println(" the queue is : "+queue);
                queue.poll();
            }
        }


        int[] result=new int[k];
        for(int i =0;i<k;i++){
            result[i]=queue.poll();
        }

        return result;
    }
    public static void main(String[] args) {
        int[] arr=new int[]{1,1,1,2,2,3};
        int[] result=topKFrequent(arr,2);
        System.out.println("Top k frequent element = " + Arrays.toString(result));

        int[] arr1=new int[]{1,3,4,3,4,2,3,4,2,5,4,5,5};
        int[] result1=topKFrequent(arr1,3);
        System.out.println("Top k frequent element = " + Arrays.toString(result1));
    }
}
