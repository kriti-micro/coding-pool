package practise.karat;

import java.util.*;

public class MergeInterval {

    //Complexity O(nlogn) for sorting and O(n) for merging n space complexity O(n) for storing the merged intervals
    public static int[][] mergeInterval(int[][] intervals){
        List<int[]> merged=new ArrayList<>();
        if(intervals==null || intervals.length<=0){
            return intervals;
        }
        //Sort the intervals based on start time
        Arrays.sort(intervals,Comparator.comparingInt(interval->interval[0]));
        for(int[] interval :intervals){

            System.out.println(Arrays.toString(interval));

        }
        int currentStart=intervals[0][0];
        int currentEnd=intervals[0][1];

        //Iterate through the intervals and merge overlapping intervals
        for(int i=1;i<intervals.length;i++){
            int nextStart=intervals[i][0];
            int nextEnd=intervals[i][1];
            //If the next interval overlaps with the current interval, merge them
            if(nextStart<=currentEnd){
                currentEnd=Math.max(currentEnd,nextEnd);
            }else{
                merged.add(new int[]{currentStart,currentEnd});
                currentStart=nextStart;
                currentEnd=nextEnd;
            }
        }

        merged.add(new int[]{currentStart,currentEnd});
        return merged.toArray(new int[merged.size()][]);
    }
    public static void main(String[] args) {

        int[][] intervals = new int[][]{{1,3},{2,6},{15,18},{8,10}};
        int[][] result = mergeInterval(intervals);
        System.out.println("Result of merge interval : ");
        for(int[] interval :result){

            System.out.println(Arrays.toString(interval));

        }

    }


}
