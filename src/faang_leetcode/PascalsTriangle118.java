package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//Time n Space = O(n^2)-no of elements,O(n^2)-storing the elements,dynamic programming
public class PascalsTriangle118 {

    public static void main(String[] args) {

        int noOfRows=6;
        List<List<Integer>> pascalTriangle=generate(noOfRows);
        System.out.println("pascal triangle = " + pascalTriangle);
    }

    private static List<List<Integer>> generate(int noOfRows) {
        List<List<Integer>> ans=new ArrayList<>();
        ans.add(new ArrayList<>());
        ans.get(0).add(1);
        for (int i = 1; i < noOfRows; i++) {
            List<Integer> prevList=ans.get(i-1);
            System.out.println("prevList = " + prevList);
            List<Integer> r=new ArrayList<>();
            r.add(1);
            for (int j = 1; j <i ; j++) {
                r.add(prevList.get(j)+prevList.get(j-1));
            }
            r.add(1);
            ans.add(r);
        }
        return ans;
    }
}
