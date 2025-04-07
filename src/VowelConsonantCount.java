import java.util.Scanner;

public class VowelConsonantCount {
    public static void main(String[] args) {
        System.out.println("Program for VowelConsonantCount :");
        Scanner sc =new Scanner(System.in);
        String str=sc.nextLine();
        getVowelConsonantCount(str);
        System.out.println("Program for VowelConsonantCount1 :");
        String str1=sc.nextLine();
        getVowelConsonantCount1(str1);
    }

    private static void getVowelConsonantCount(String str) {
        int vowel=0;
        int consonant=0;
        char[] charArr=str.toLowerCase().toCharArray();
        for(char c:charArr){
            if(c=='a' || c=='e' || c=='i' || c=='o' || c=='u')
                vowel += 1;
            else
                consonant += 1;
        }
        System.out.println("The vowel count is "+vowel+" and consonent is "+consonant);
    }
    private static void getVowelConsonantCount1(String str) {
        int vowel=0;
        int consonant=0;
        char[] charArr=str.toLowerCase().toCharArray();
        for(char c:charArr){
            if(c>'a' && c<'z') {
                if("aeiou".indexOf(c) !=-1)
                    vowel += 1;
                else
                    consonant += 1;
            }
        }
        System.out.println("The vowel count is "+vowel+" and consonent is "+consonant);
    }
}
