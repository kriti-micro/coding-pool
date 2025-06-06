package faang_leetcode;

import java.util.Arrays;

//Time O(nlogn) Space O(n)
public class MeetingRoom253 {
    public static void main(String[] args) {
        int[][] intervals={
                {0,30},
                {5,10},
                {15,20}
        };
        int conferenceRoom=minMeetingRooms(intervals);
        System.out.println("The no rooms are = " + conferenceRoom);
    }

    private static int minMeetingRooms(int[][] intervals) {
        System.out.println("intervals = " + Arrays.deepToString(intervals));

        if(intervals.length==0){
            return 0;
        }
        int[] start=new int[intervals.length];
        int[] end=new int[intervals.length];

        for (int i = 0; i < intervals.length; i++) {
            start[i]=intervals[i][0];
            end[i]=intervals[i][1];
        }

        System.out.println(" start array "+Arrays.toString(start));
        System.out.println(" end array : "+Arrays.toString(end));

        Arrays.sort(start);
        Arrays.sort(end);

        System.out.println("After sorting ");
        System.out.println(" start array "+Arrays.toString(start));
        System.out.println(" end array : "+Arrays.toString(end));

        int startptr=0;
        int endptr=0;
        int result=0;

        while(startptr<start.length){
            System.out.println(" result "+result);
            System.out.println(" start[startptr] "+start[startptr]);
            System.out.println(" end[endptr] "+end[endptr]);

            //If the current meeting starts after the earliest meeting ends, reuse room.
            //Otherwise, need a new room.
            if(start[startptr]>=end[endptr]){
                result--;
                endptr++;
            }
            result++;
            startptr++;
        }
        return result;
    }
}
