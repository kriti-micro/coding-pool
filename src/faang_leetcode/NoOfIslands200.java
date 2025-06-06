package faang_leetcode;

import java.util.Arrays;

public class NoOfIslands200 {
    //Time n space O(m*n)
    public static void main(String[] args) {
        char[][] matrix=new char[][]{
                {'1','1','1','1','0'},
                {'1','1','0','1','0'},
                {'1','1','0','0','0'},
                {'0','0','0','0','0'}};
        int results=numIslands(matrix);
        System.out.println("results of matrix1 = " + results);
        char[][] matrix1=new char[][]{
                {'0','0','0','0','0','0'},
                {'0','1','1','0','1','0'},
                {'0','1','0','0','1','0'},
                {'0','0','0','0','1','0'},
                {'0','1','1','0','0','1'}
        };
         results=numIslands(matrix1);
        System.out.println("results of matrix2 = " + results);
    }

    private static int numIslands(char[][] matrix) {
        System.out.println("matrix = " + Arrays.deepToString(matrix));
        int row=matrix.length;
        int col=matrix[0].length;
        int islands=0;
        for(int i=0;i<row;i++){
            for(int j=0;j<col;j++){
                if(matrix[i][j]=='1'){
                    islands++;
                    dfs(i,j,matrix);

                }
            }
        }
        return islands;
    }

    private static void dfs(int row, int col, char[][] matrix) {
        System.out.println("row = " + row + ", col = " + col + ", matrix = " + Arrays.deepToString(matrix));
        int newRow=matrix.length;
        int newCol=matrix[0].length;

        int[][] directions={{1,0},{0,1},{-1,0},{0,-1}};
        if(row<0 || col<0 || row>=newRow || col>=newCol || matrix[row][col]=='0'){
            return;
        }

        matrix[row][col]='0';
        for(int[] dir : directions){
            System.out.println(("checking for adjacent cells : "+dir[0]+","+dir[1]));
            dfs(row+dir[0],col+dir[1],matrix);
        }
    }
}
