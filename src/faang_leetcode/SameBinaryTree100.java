package faang_leetcode;

import java.util.Arrays;

public class SameBinaryTree100 {
    static boolean isSameTree(TreeNode p, TreeNode q){
        if(p==null && q==null){
            return true;
        }
        if(p==null || q==null){
            return false;
        }
        if(p.val != q.val){
            return false;
        }
        return isSameTree(p.left,q.left) && isSameTree(p.right,q.right);
    }
    public static void main(String[] args) {
        TreeNode p=new TreeNode(3,new TreeNode(10),
                new TreeNode(20,new TreeNode(15),new TreeNode(7)));
        TreeNode q=new TreeNode(3,new TreeNode(10),
                new TreeNode(20,new TreeNode(15),new TreeNode(7)));

        boolean issametree=isSameTree(p,q);
        System.out.println("is same tree = " + issametree);
    }
}
