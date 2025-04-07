import java.util.Scanner;

public class Palindrome {
    public static void main(String[] args) {
        System.out.println("Program for Palindrome Test :");
        String name="Madam";
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the input String :");
        name=sc.nextLine();
        System.out.println("The String "+name+" is palindrome ? "+isPalindrome(name));

    }

    private static boolean isPalindrome(String name) {
        name=name.toLowerCase();
        for(int i=0,j=name.length()-1;i<j;i++,j--){
            if(!(name.charAt(i)==name.charAt(j)))
                return false;
        }
        return true;
    }
}
