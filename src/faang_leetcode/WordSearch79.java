package faang_leetcode;

import java.util.Arrays;

public class WordSearch79 {

    char[][] board;
    int rows;
    int cols;

    public static void main(String[] args) {
        char[][] board={
                {'A','B','C','E'},
                {'S','F','C','S'},
                {'A','D','E','E'}
        };
        String word="ABCCED";
        WordSearch79 w=new WordSearch79();
        boolean result=w.exist(board,word);
        System.out.println("Does the word exist in matrix = " + result);
    }

    private boolean exist(char[][] board, String word) {
        System.out.println("board = " + Arrays.deepToString(board) + ", word = " + word);
        this.board=board;
        this.rows=board.length;
        this.cols=board[0].length;

        System.out.println(" Rows : "+rows+" Cols : "+cols);
        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                if(backtrack(r,c,word,0)){
                    return true;
                }
            }
        }
        return false;
    }

    private boolean backtrack(int r, int c, String word, int index) {
        System.out.println("r = " + r + ", c = " + c + ", word = " + word + ", index = " + index);
        if(index>=word.length()){
            return true;
        }

        if(r<0 || r>=rows || c<0 || c>=cols || this.board[r][c] != word.charAt(index)){
            return false;
        }

        int[] rowdir={0,1,0,-1};
        int[] coldir={1,0,-1,0};
        this.board[r][c]='#';

        boolean ret=false;
        for (int d = 0; d < 4; d++) {
            ret=backtrack(r+rowdir[d],c+coldir[d],word,index+1);
            if(ret){
                break;
            }
        }
        this.board[r][c]=word.charAt(index);
        return ret;
    }
}
