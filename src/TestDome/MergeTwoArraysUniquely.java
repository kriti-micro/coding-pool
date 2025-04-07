package TestDome;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

public class MergeTwoArraysUniquely {
    public static void main(String[] args) {
        String[] names1={"Olivia","Emma","Sophia"};
        String[] names2={"Anupriya","Sophia","Olivia"};

        List<String> l1=new ArrayList<>(Arrays.asList(names1));
        List<String> l2=new ArrayList<>(Arrays.asList(names2));
        l1.addAll(l2);
        System.out.println("mergedArr = " + l1);

        l1=new ArrayList<>(new LinkedHashSet<>(l1));
        String[] arr=new String[l1.size()];

      for(int i=0;i<arr.length;i++){
           arr[i]=l1.get(i);
            System.out.println("mergedarray index = " + arr[i]);
        }
    }
}
