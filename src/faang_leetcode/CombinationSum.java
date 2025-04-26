package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* Complex */
//https://leetcode.com/problems/combination-sum/description/
//Time Complexsity O(n^t) n=nof candidates t=time Recursion and backtracking
public class CombinationSum {

    public static List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> result=new ArrayList<>();
        List<Integer> combinations=new ArrayList<>();
        backtrack(target,result,combinations,0,candidates);
        return result;
    }

    public static void backtrack(int target, List<List<Integer>> result, List<Integer> combinations, int start, int[] candidates) {
        System.out.println("Start of backtrack method , target = " + target + ", result = " + result + ", combinations = " + combinations + ", start = " + start + ", candidates = " + Arrays.toString(candidates));
        if(target==0){
            result.add(new ArrayList<>(combinations));
        }else if(target<0){
            System.out.println(" Backtrack return condition when target is less than 0 = "+target);
            return;
        }

        for(int i=start;i<candidates.length;i++){
            System.out.println(" For loop entering then target value = "+target);
            combinations.add(candidates[i]);
            System.out.println(" target - candidates["+i+"] = "+target +"-"+candidates[i]+" = "+(target-candidates[i]));
            backtrack(target-candidates[i],result,combinations,i,candidates);
            combinations.remove(combinations.size()-1);
            System.out.println("Increment of i as return was called (target<0): "+ i + ",target = "+target+" ,combos = "+combinations);
        }

    }

    public static void main(String[] args) {
        int[] candidates=new int[]{2,5,6};
        int target=8;
        List<List<Integer>> result=combinationSum(candidates,target);
        System.out.println("Unique combination to make target = " + result);
    }
}
