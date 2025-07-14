package faang_leetcode;

import java.util.Arrays;

public class MaximumDepthOfBinaryTree104 {
    public static void main(String[] args) {
        TreeNode root=new TreeNode(3,new TreeNode(9),
                new TreeNode(20,new TreeNode(15),new TreeNode(7)));

        int maxDepthOfTree=maxDepth(root);
        System.out.println("root = " + root.val+','+root.left.val+","+root.right.val+","+
                root.left.left+","+root.left.right+","+root.right.left.val+","+root.right.right.val);
        System.out.println("maxDepth = " + maxDepthOfTree);
    }

    private static int maxDepth(TreeNode root) {
        if(root==null){
            return 0;
        }else{
            int leftMax=maxDepth(root.left);
            int rightMax=maxDepth(root.right);
            return Math.max(leftMax,rightMax)+1;
        }
    }

}

 // Definition for a binary tree node.
   class TreeNode {
      int val;
      TreeNode left;
      TreeNode right;
      TreeNode() {}
      TreeNode(int val) { this.val = val; }
      TreeNode(int val, TreeNode left, TreeNode right) {
          this.val = val;
          this.left = left;
          this.right = right;
      }
  }

