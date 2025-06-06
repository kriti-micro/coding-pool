package faang_leetcode;

import java.util.*;

//Time and Space complexity -O(v+e) Leet code 207
public class CourseSchedule {
    public static boolean canFinish(int numCourses, int[][] prerequisites) {
        System.out.println("numCourses = " + numCourses + ", prerequisites = " + Arrays.deepToString(prerequisites));
        HashMap<Integer,List<Integer>> courseGraph=new HashMap<>();
        for(int[] pre:prerequisites){
            if(courseGraph.containsKey(pre[1])){
                courseGraph.get(pre[1]).add(pre[0]);
                System.out.println(pre[1] + " : " + courseGraph.get(pre[1]));
            }else{
                List<Integer> nextCourses = new LinkedList<>();
                nextCourses.add(pre[0]);
                courseGraph.put(pre[1],nextCourses);
                System.out.println(pre[1] + " : " + courseGraph.get(pre[1]));
            }
        }
        HashSet<Integer> visited = new HashSet<>();
        //return false for infinite loop .Logic in courseSchedule
        for(int currentCourse=0;currentCourse<numCourses;currentCourse++){
            if(courseSchedule(currentCourse,visited,courseGraph)==false){
                return false;
            }
        }
        return true;
    }

    private static boolean courseSchedule(int currentCourse, HashSet<Integer> visited, HashMap<Integer, List<Integer>> courseGraph) {

        System.out.println("currentCourse = " + currentCourse + ", visited = " + visited + ", courseGraph = " + courseGraph);
        if(visited.contains(currentCourse)){
         return false;
        }

         //if empty then return true
         if(courseGraph.get(currentCourse)==null){
            return true;
         }

         visited.add(currentCourse);
        System.out.println("After addition in visited -> currentCourse = " + currentCourse + ", visited = " + visited + ", courseGraph = " + courseGraph);
         //checking for list of courses map to that currentcourse
         for(int pre : courseGraph.get(currentCourse)){
             if(courseSchedule(pre,visited,courseGraph)==false){
                 return false;
             }
         }
         visited.remove(currentCourse);
         System.out.println(" removing from visited "+visited + " course "+currentCourse);
         courseGraph.put(currentCourse,null);//making empty if prerequisite found in Hashmap
        return true;
    }



    public static void main(String[] args) {
        int numCourses=6;
        int[][] prerequisites={{0,1},{1,2},{1,4},{1,5},{2,3},{3,4},{4,5},{5,3}};
        boolean result=canFinish(numCourses,prerequisites);
        System.out.println("the courses can be finished = " + result);
    }
}
