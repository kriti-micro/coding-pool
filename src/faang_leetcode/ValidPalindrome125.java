package faang_leetcode;

import java.util.Arrays;

public class ValidPalindrome125 {
    public static void main(String[] args) {
        String s="A man, a plan, a canal: Panama";
        boolean isPalindromeString=isPalindromeUsingPointer(s);
        System.out.println("String = " + s+" is palindrome -"+isPalindromeString);
        boolean isPalindromeStringUsingReverse=isPalindromeUsingReverseApproach(s);
        System.out.println("String = " + s+" is palindrome -"+isPalindromeStringUsingReverse);

    }

    //O(n),O(n)
    private static boolean isPalindromeUsingReverseApproach(String s) {
        StringBuilder sb=new StringBuilder();
        for(char c:s.toCharArray()){
            if(Character.isLetterOrDigit(c)){
            sb.append(Character.toLowerCase(c));
            }
        }
        String cleaned=sb.toString();
        String reversed=sb.reverse().toString();
        return cleaned.equals(reversed);
    }

    //O(n),O(1)-no extra memory used
    private static boolean isPalindromeUsingPointer(String s) {
        System.out.println("s = " + s);
        int left=0;
        int right=s.length()-1;

        while(left<right){
            System.out.println("left char = " + s.charAt(left)+" right char ="+s.charAt(right));
            while(left<right && !Character.isLetterOrDigit(s.charAt(left))){
                System.out.println("left = " + left);
                left++;
            }
            while(left<right && !Character.isLetterOrDigit(s.charAt(right))){
                System.out.println("right = " + right);
                right--;
            }
            if(Character.toLowerCase(s.charAt(left))!=Character.toLowerCase(s.charAt(right))){
                return false;
            }
            left++;
            right--;
        }
        return true;
    }
}
