import java.util.Scanner;

public class Anagram {
    public static void main(String[] args){
        System.out.println("Program for Anagram :Two strings are anagrams if they contain the same characters in a different order. :");
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the input String 1:");
        String str1=sc.nextLine();
        System.out.println("Enter the input String 2:");
        String str2=sc.nextLine();
        System.out.println("The String "+str1+" and "+str2+" is anagram ? "+ isAnagram(str1, str2));
    }

    private static boolean isAnagram(String str1, String str2) {
        return true;
    }
}
