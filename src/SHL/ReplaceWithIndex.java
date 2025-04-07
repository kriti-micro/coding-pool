package SHL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
You are given a list of N unique positive numbers ranging from 0 to (N-1).
Write an algorithm to replace the value of each number with its corresponding index value in the list.
* */
public class ReplaceWithIndex {
    public static void main(String[] args) {
       int[] uniqueNoList= {2,0,1};
        int[] replacedList= new int[uniqueNoList.length];
        for(int i:uniqueNoList){
            replacedList[uniqueNoList[i]]=i;
        }
        System.out.println("uniqueNoList = " + Arrays.toString(uniqueNoList));
        System.out.println("replacedList = " + Arrays.toString(replacedList));
    }
}
