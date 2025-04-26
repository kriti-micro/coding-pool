package faang_leetcode;

//https://leetcode.com/problems/decode-ways/
//Dynamic Programming t=O(n) s=O(1)
public class DecodeWays {
    public static int numDecodings(String s) {
        System.out.println("s = " + s);
        if(s.charAt(0)=='0'){
            return 0;
        }
        int one=1;
        int two=1;
        System.out.println("Initial value of  one = " + one+" and two = "+two);
        for(int i = 1;i<s.length();i++){
            int current=0;
            if(s.charAt(i)!='0'){
                current=one;
                System.out.println("current = " + current);
            }
            int value=Integer.parseInt(s.substring(i-1,i+1));
            if(value>=10 && value<=26){
                current=current+two;
                System.out.println("current = " + current);
            }
            two=one;
            one=current;
            System.out.println("character = " + s.charAt(i)+", value = "+value+", one ="+one+", two = "+two);
        }
        return one;
    }

    public static void main(String[] args) {
        String s1="2123";
        String s2="2128";//it will not increment
        //int result=numDecodings(s1);
        int result=numDecodings(s2);
        System.out.println("The no ways string could be decoded = " + result);
    }
}
