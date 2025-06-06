package faang_leetcode;

import java.util.Arrays;


//O(nlogn)-Time ,Space-O(1)
public class MeetingRooms252 {
    public static void main(String[] args) {
        int[][] intervals={
                {20,30},
                {0,15},
                {35,45},
                {10,20}
        };
        boolean canAttend=canAttendMeetings(intervals);
        System.out.println(" the person can attend meeting ? :"+canAttend);
    }

    private static boolean canAttendMeetings(int[][] intervals) {
        System.out.println("intervals = " + Arrays.deepToString(intervals));
        Arrays.sort(intervals,(a,b)->Integer.compare(a[0],b[0]));

        for (int i = 0; i < intervals.length-1; i++) {
            if(intervals[i][1]>intervals[i+1][0]){
                return false;
            }
        }
        return true;
    }

}
