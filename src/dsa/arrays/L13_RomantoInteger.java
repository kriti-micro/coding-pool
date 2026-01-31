package dsa.arrays;

import java.util.HashMap;
import java.util.Map;

public class L13_RomantoInteger {



    //O(1),O(1)
    public static void main(String[] args) {

        String s="MCMXCIV";
        int result=romanToInt(s);
        System.out.println(" the Roman to integer is  : "+result);
        String s1="MDXLIX";
        int result1=romanToIntUsingComparison(s1);
        System.out.println(" the Roman to integer is  : "+result1);
    }

    private static int romanToIntUsingComparison(String s1) {
        Map<Character, Integer> map = new HashMap<>();
        map.put('I', 1);
        map.put('V', 5);
        map.put('X', 10);
        map.put('L', 50);
        map.put('C', 100);
        map.put('D', 500);
        map.put('M', 1000);
        int result=0;
        for (int i = 0; i < s1.length(); i++) {
            int curr=map.get(s1.charAt(i));
            int next=(i+1>=s1.length())?0:map.get(s1.charAt(i+1));
            if(curr<next){
                result -= curr;
            }else{
                result +=  curr;
            }
        }
        return result;
    }

    private static int romanToInt(String s) {
        System.out.println("s = " + s);
        HashMap<String,Integer> map=new HashMap<>();
        map.put("I",1);
        map.put("v",5);
        map.put("x",10);
        map.put("L",50);
        map.put("C",100);
        map.put("D",500);
        map.put("M",1000);
        map.put("IV",4);
        map.put("IX",9);
        map.put("XL",40);
        map.put("XC",90);
        map.put("CD",400);
        map.put("CM",900);
        int i=0;
        int result=0;
        while(i<s.length()){
            String twoSymbol=s.substring(i,i+2);
            System.out.println(" TwoSymbol : "+twoSymbol);
            if(map.containsKey(twoSymbol)){
                result += map.get(twoSymbol);
                i=i+2;
                continue;
            }
            String oneSymbol=s.substring(i,i+1);
            System.out.println(" OneSymbol : "+oneSymbol);
            result += map.get(oneSymbol);
            i=i+1;
        }
        return result;
    }
}
