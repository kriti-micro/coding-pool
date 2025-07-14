package faang_leetcode;

import java.util.Arrays;

public class ValidateBinarySearchTree98 {
     Integer prev;

    public static void main(String[] args) {
        TreeNode root=new TreeNode(5,
                new TreeNode(3,new TreeNode(2),new TreeNode(4)),
                new TreeNode(7,new TreeNode(4),new TreeNode(8)));
        boolean isValidBTFlag=new ValidateBinarySearchTree98().isValidBST(root);
        System.out.println("This is the valid Binary Search Tree = " + isValidBTFlag);
    }

    private boolean isValidBST(TreeNode root) {
        this.prev=null;
        return inOrder(root);
    }

    private boolean inOrder(TreeNode root) {
        if(root==null){
            return true;
        }
        //check left subtree
        if(!inOrder(root.left)){
            return false;
        }
        //Main logic
        if(this.prev!=null && root.val<this.prev){
            return false;
        }
        this.prev=root.val;
        //check right subtree
        return inOrder(root.right);
    }
}
