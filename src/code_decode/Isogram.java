package code_decode;

import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

//Isogram ->does not contain duplicate character
public class Isogram {
    public static void main(String[] args) {
        boolean isogram=true;
        Scanner sc=new Scanner(System.in);
        String input=sc.nextLine();
        char[] inpArr=input.toCharArray();
        Set<Character> charSet=new HashSet<>();
        for(char c : inpArr){
            if(charSet.contains(c)){
                isogram=false;
                break;
            }else{
                charSet.add(c);
            }
        }
        System.out.println("The string is isogram : "+isogram);
    }
}
