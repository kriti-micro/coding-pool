class ReverseString {
    public static void main(String[] args) {
        System.out.println("Program for reversing a String :");
        String s="Kriti";
        String rev=revString(s);
        System.out.println("Reverse using for Loop of String "+s+" is  " +rev);
    }

    public static String revString(String s){
        StringBuilder rev=new StringBuilder(s);
        System.out.println("After rever StringBuilder value "+rev.reverse());
        System.out.println("Original StringBuilder value "+rev);
        String revstr="";
        for(int i =s.length()-1;i>=0;i--){
            revstr += s.charAt(i);
        }
        return revstr;
    }
}