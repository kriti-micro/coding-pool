package code_decode;

import java.util.Arrays;

public class StringRotation {
    static String input="codedecode";
    static String rotatedString1 ="dedecodeco";
    static String rotatedString2 = "codeedcode";

    private static boolean isStringRotated(String in,String rotatedString){
        //Join the string and check rotated string is substring of
        String appendedStr= in + in;
        return appendedStr.contains(rotatedString);
    }

    private static String rotateLeft(String in,int noOfCharRotate){
             // codedecode 0123456789 -> dedecodeco
            int size=in.length();
            return in.substring(noOfCharRotate)+in.substring(0,noOfCharRotate);
    }

    private static String rotateRight(String in,int noOfCharRotate){
        // codedecode 0123456789 -> decodedeco
        int n=in.length();
        return in.substring(n-noOfCharRotate)+in.substring(0,n-noOfCharRotate);
    }

    public static void main(String[] args) {
        System.out.println("Check the given input is rotated String : Input rotatedString1- "+rotatedString1+" " + isStringRotated(input,rotatedString1));
        System.out.println("Check the given input is rotated String : Input rotatedString2- "+rotatedString2+" " + isStringRotated(input,rotatedString2));
        System.out.println("rotate the input "+input+" String i.e. given no of char 2 to the right ,so the output is " + rotateRight(input,2));
        System.out.println("rotate the input "+input+" String i.e. given no of char 2 to the left ,so the output is " + rotateLeft(input,2));
    }
}
