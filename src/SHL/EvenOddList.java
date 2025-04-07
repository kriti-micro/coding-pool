package SHL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/*
You are playing an online game. In the game, a list of N numbers is given.
The player has to arrange the numbers so that all the odd numbers of the list come after the even numbers.
Write an algorithm to arrange the given list such that all the odd numbers of the list come after the even
numbers.in java
* */
public class EvenOddList {
    public static void main(String[] args) {
        List<Integer> inputList= Arrays.asList(10, 3, 4, 7, 6, 9, 2, 11);
        System.out.println("inputList = " + inputList);
        List<Integer> even =new ArrayList<>();
        List<Integer> odd =new ArrayList<>();
        for(Integer i:inputList){
            if(i%2==0){
                even.add(i);
            }else{
                odd.add(i);
            }
        }
        even.addAll(odd);
        System.out.println("final List = " + even);
    }
}
