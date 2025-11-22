package practise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SummingList {

    public static List<Integer> sumList(List<Integer> list){
        List<Integer> newList=new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            int sum=0;
            for (int j = 0; j <= i; j++) {
                sum=sum+list.get(j);
            }
            newList.add(sum);
        }
        return newList;
    }

    public static List<Integer> prefixSumApproach(List<Integer> list){
        int runningSum=0;
        List<Integer> resultList=new ArrayList<>();
        for(int i=0;i<list.size();i++){
            runningSum += list.get(i);
            resultList.add(runningSum);
        }
        System.out.println("Using prefixSumApproach = " + resultList);
        return resultList;
    }

    public static List<Integer> java8Approach(List<Integer> list){
        AtomicInteger atomicInteger=new AtomicInteger();
        List<Integer> resultList = list.stream().map(atomicInteger::addAndGet).toList();
        System.out.println("Using java8Approach = " + resultList);
        return resultList;
    }

    public static void main(String[] args) {
        List<Integer> oldList=new ArrayList<>(Arrays.asList(1,2,3,4));
        List<Integer> newList = sumList(oldList);
        System.out.println("oldList = " + oldList);
        System.out.println("Using Brute force newList = " + newList);
        prefixSumApproach(oldList);
        java8Approach(oldList);
    }
}
