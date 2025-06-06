package faang_leetcode;

import java.util.*;

//417
public class PacificAtlanticWaterFlow {
    public static List<List<Integer>> pacificAtlantic(int[][] heights){
        System.out.println("heights = " + Arrays.deepToString(heights));
        //return if no heights
        if(heights.length == 0 || heights[0].length == 0){
            return new ArrayList<>();
        }

        int row=heights.length;
        int col=heights[0].length;
        System.out.println("rows = " + row + " cols "+col);

        //creating 2 matrix for storing boolean values for atlantic and Pacific
        boolean[][] pacificReachable = new boolean[row][col];
        boolean[][] atlanticReachable = new boolean[row][col];

        for(int i =0;i<row;i++){
            dfs(i,0,pacificReachable,heights);
            dfs(i,col-1,atlanticReachable,heights);
        }

        for(int j=0;j<col;j++){
            dfs(0,j,pacificReachable,heights);
            dfs(row-1,j,atlanticReachable,heights);
        }

        List<List<Integer>> result=new ArrayList<>();
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                if(pacificReachable[i][j] && atlanticReachable[i][j]){
                    result.add(List.of(i,j));
                }
            }
        }
        return result;
    }

    private static void dfs(int row, int col, boolean[][] reachable, int[][] heights) {
        System.out.println("row = " + row + ", col = " + col + ", reachable = " + Arrays.deepToString(reachable) + ", heights = " + Arrays.deepToString(heights));

        int[][] directions= new int[][]{{0, 1}, {1, 0}, {-1, 0}, {0, -1}};
        System.out.println("directions : "+Arrays.deepToString(directions));
        reachable[row][col]=true;
        for(int[] dir :directions){
            int newRow=row+dir[0];
            int newCol=col+dir[1];

            System.out.println("newRow = " + newRow + ", newCol = " + newCol);
            if(newRow<0 || newRow>=heights.length || newCol<0 || newCol>=heights[0].length){
                continue;
            }
            if(reachable[newRow][newCol]){
                continue;
            }
            if(heights[newRow][newCol]<heights[row][col]){
                continue;
            }
            dfs(newRow,newCol,reachable,heights);
        }



    }

    public static void main(String[] args) {
        int heights[][]=new int[][]{{1,2,2,3,5},{3,2,3,4,4},{2,4,5,3,1},{6,7,1,4,5},{5,1,1,2,4}};
        List<List<Integer>> result=pacificAtlantic(heights);
        System.out.println("result = " + result.toString());
    }


}
