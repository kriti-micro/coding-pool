package practise;

public class StringReverseUsingRecursion {
    public static StringBuilder sb=new StringBuilder();
    public static String recursion(String s){

        if(!s.equals("")) {
            System.out.println(s.charAt(s.length() - 1));
            sb.append(s.charAt(s.length() - 1));
            System.out.println(s.substring(0,s.length()-1));
            recursion(s.substring(0,s.length()-1));
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        String s="Kriti";
        String reverseString=recursion(s);
        System.out.println("Reverse String of "+s+" is "+reverseString);
    }
}
