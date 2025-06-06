package faang_leetcode;

import java.util.Arrays;
import java.util.LinkedList;

//O(n)
public class InsertInterval57 {
    public static void main(String[] args) {
        int[] newInterval= {3,8};
        int[][] intervals={
                {2,4},
                {8,10},
                {13,15}
        };
        int[][] output=insert(intervals,newInterval);
        System.out.println(" the output is "+Arrays.deepToString(output));
    }

    private static int[][] insert(int[][] intervals, int[] newInterval) {
        System.out.println("intervals = " + Arrays.deepToString(intervals) + ", newInterval = " + Arrays.toString(newInterval));
        int newStart=newInterval[0];
        int newEnd=newInterval[1];
        int left=0;
        int right=intervals.length;

        //Creating list to add interval
        LinkedList<int[]> output=new LinkedList<>();

        System.out.println("=== Phase 1: Adding intervals before newInterval ===");
        while(left<right && intervals[left][0]<newStart){
            System.out.println("Adding interval before newInterval: [" + intervals[left][0] + "," + intervals[left][1] + "]");
            output.add(intervals[left]);
            left++;
        }

        System.out.println("=== Phase 2: Insert/Merge newInterval ===");
        int[] interval=new int[2];
        if(output.isEmpty() || output.getLast()[1]<newStart){
            System.out.println("No overlap with previous, adding newInterval: [" + newStart + "," + newEnd + "]");
            output.add(newInterval);
        }else{
            interval=output.removeLast();
            System.out.println("Merging last interval [" + interval[0] + "," + interval[1] + "] with newInterval [" + newStart + "," + newEnd + "]");
            interval[1] = Math.max(interval[1], newEnd);
            System.out.println("Merged interval: [" + interval[0] + "," + interval[1] + "]");
            output.add(interval);
        }

        System.out.println("=== Phase 3: Processing remaining intervals ===");
        while(left<right){
            interval=intervals[left];
            left++;
            int start=interval[0];
            int end=interval[1];
            System.out.println("Processing interval: [" + start + "," + end + "]");

            if(output.getLast()[1]<start){
                System.out.println("No overlap, adding directly.");
                output.add(interval);
            }else{
                int[] last=output.removeLast();
                System.out.println("Merging with last interval [" + last[0] + "," + last[1] + "]");
                last[1] = Math.max(last[1], end);
                System.out.println("Merged interval: [" + last[0] + "," + last[1] + "]");
                output.add(last);
            }
        }
        return output.toArray(new int[output.size()][2]);
    }
}
