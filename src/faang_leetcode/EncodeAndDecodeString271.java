package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EncodeAndDecodeString271 {
    public static void main(String[] args) {
        String s="Hello,";

        String encoded=encode(Arrays.asList("Hello,","Kriti"));//converted to single encoded String
        List<String> decoded=decode(encoded);//decoded from single string to List<String>

        System.out.println("encoded String from List of String = " + encoded );
        System.out.println("decoded String to List of String = " + decoded);
    }

    private static List<String> decode(String s) {
        if(s.equals(Character.toString((char)258))){
            return new ArrayList<>();
        }
        //Take any char as separater
        String separate=Character.toString((char)257);
        return Arrays.asList(s.split(separate));
    }

    private static String encode(List<String> strs) {
        if(strs.size()==0){
            return Character.toString((char)258);
        }
        String separate=Character.toString((char)257);
        StringBuilder sb=new StringBuilder();
        for(String str:strs){
            sb.append(str);
            sb.append(separate);
        }
        sb.deleteCharAt(sb.length()-1);
        return sb.toString();
    }
}
