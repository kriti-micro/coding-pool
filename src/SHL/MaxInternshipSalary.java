package SHL;

import java.util.Arrays;
import java.util.Scanner;

/*
Stephen is doing an internship in a company for N days. Each day, he may choose either an easy task or a difficult task. He may also choose to perform no task at all. He chooses a difficult task on days when and only when he did not perform any work the previous day. The amount paid by the company for both the easy and difficult tasks can vary each day, but the company always pays more for difficult tasks.

Write an algorithm to help Stephen earn the maximum salary.
Input
The first line of the input consists of two space-separated integers - num and type, representing the number of days of the internship (N) and types of tasks available for each day (type (M ) is always equal to 2 ), respectively.
The next N lines consist of M space-separated integers - easy and hard, representing the amount set to be paid for the easy task and the difficult task on that day, respectively.

Output
Print an integer representing the maximum salary that Stephen can earn.
* */
public class MaxInternshipSalary {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        // Read first line
//        4 2
//        1 2
//        4 10
//        20 21
//        2 23
//        O/P -33
        int num=scanner.nextInt();
        int type=scanner.nextInt();
        int[][] tasks=new int[num][2];
        for(int i =0;i<num;i++){
            tasks[i][0]=scanner.nextInt();// easy task
            tasks[i][1]=scanner.nextInt();//difficult task

        }
        int result=getMaxSalary(tasks,num);
        System.out.println("Max Salary = " + result);
    }

    private static int getMaxSalary(int[][] tasks, int num) {

        int[][] dp=new int[num][2]; // dp[i][0]: worked today, dp[i][1]: rested today
        // Base case for day 0
        dp[0][0]=tasks[0][0];// Only easy task is allowed on day 0
        dp[0][1]=0; // Rested on day 0
        System.out.println(" dp[i][0] worked today  dp[i][1] rested today ");
        System.out.println(" dp[0][0] "+dp[0][0] +" dp[0][1] "+dp[0][1]);
        for(int i =1;i<num;i++)
        {
            // If Stephen rests today, take max of working or resting yesterday
            dp[i][1]=Math.max(dp[i-1][0],dp[i-1][1]); //Max of previous day

            // If Stephen works today:
            int easyTask=Math.max(dp[i-1][0],dp[i-1][1])+tasks[i][0]; //Max previous day task value + value of Easy task
            int difficultTask=dp[i-1][1]+tasks[i][1]; // Only if he rested yesterday + value of difficult task

            dp[i][0]=Math.max(easyTask,difficultTask);
            System.out.println(" dp["+i+"][0] "+dp[i][0] +" dp["+i+"][1] "+dp[i][1]);
        }

        return Math.max(dp[num-1][0],dp[num-1][1]);
    }
}
