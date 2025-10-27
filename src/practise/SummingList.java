package practise;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static void main(String[] args) {
        List<Integer> oldList=new ArrayList<>(Arrays.asList(1,2,3,4));
        List<Integer> newList = sumList(oldList);
        System.out.println("oldList = " + oldList);
        System.out.println("newList = " + newList);
    }
}
