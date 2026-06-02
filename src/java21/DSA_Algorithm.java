package java21;

import java.util.*;

public class DSA_Algorithm {

//    Arrays & HashMap — Two Sum, duplicates, group anagrams, top K frequent
//    Sliding Window — longest substring, max sum subarray, K distinct chars
//    String & Stack — valid parentheses, reverse words, palindrome check
//    Sorting & Search — merge sorted arrays, second largest, binary search
//    Linked List — reverse, detect cycle (Floyd's algorithm)
//    Recursion & DP — Fibonacci optimized, climbing stairs

    public static void main(String args[]) {
        Scanner sc = new Scanner(System.in);
        String algoName = sc.nextLine();
        switch(algoName){
            case "Two Sum","1":
                new DSA_Algorithm().twoSum();
                break; 
            case "Longest Substring Without Repeating Characters","2" :
                new DSA_Algorithm().longestSubstringWithoutRepeatingCharacters();
                break;
            case "Merging 2 sorted Arrays","3"  :
                new DSA_Algorithm().mergingTwoSortedArray();
                break;
            case "Valid Parentheses", "4" :
                System.out.println(new DSA_Algorithm().checkParenthesis("({[]}[])"));
                break;
            case "Missing No","5"  :
                System.out.println(new DSA_Algorithm().missingNumber(new int[]{3,0,1}));
                break;
            case "Anagram","6" :
                System.out.println(new DSA_Algorithm().isAnagram("anagram","nagaram"));
                break;
            default:
                System.out.println("No algo found");
        }
        }

    //Time Complexity n Space
    private boolean isAnagram(String str1, String str2) {
        if(str1.length()!=str2.length()) return false;
        //We can use HashMap to count the frequency of each character in the first string and then decrement the count for each character in the second string. If any count is not zero at the end, then the strings are not anagrams.
        //But usint int array is more efficient as we know that the characters are from a-z and we can use the index to count the frequency of each character.No need to do hashing and can be used for only alphabet characters.
        int[] charCount=new int[26];
        for(char c:str1.toCharArray()){ charCount[c-'a']++;}
        for(char c:str2.toCharArray()){ charCount[c-'a']--;}
        for(int n : charCount){ if(n!=0) return false;}
        return true;
    }

    private int missingNumber(int[] nums) {
        int n=nums.length;
        int actual=n*(n+1)/2;// sum of 0+1+2+...+n
        int expected = 0;
        for(int k:nums){
            expected += k;
        }
        return actual-expected;
    }

    public  boolean checkParenthesis(String str){
        //Use Deque instead of Stack — Stack extends Vector (synchronized, slower). Mentioning this shows senior-level awareness.
        Deque<Character> stack =new ArrayDeque<>();
        for(char c : str.toCharArray()){
            if(c=='(' || c=='{' || c=='['){
                stack.push(c);
            }else{
                if(stack.isEmpty()) return false;
                //If we encounter a closing parenthesis, we pop from the stack and check if it matches the corresponding opening parenthesis. If it doesn't match, we return false.
                //If we use stack we can use top() method but in Deque we use peek() method to check the top element without removing it, and pop() to remove the top element.
                char top=stack.pop();
                if(c==']' && top !='[') return false;
                if(c=='}' && top !='{') return false;
                if(c==')' && top !='(') return false;
            }
        }
        return stack.isEmpty();
    }

    //Time - O(n) and Space - O(1) with brute force it is n^3
        /*We use a sliding window, Use 2 pointers left and right */
    private void longestSubstringWithoutRepeatingCharacters() {
        String str="abcdabcbace";
        Set<Character> seen=new LinkedHashSet<>();
        int left=0;
        int max =0;
        for(int right = 0;right<str.length();right++){
            char c = str.charAt(right);
            System.out.println("Window = "+str.substring(left,right+1));
            //If the character is already present in the set,
            // we need to move the left pointer until we remove the duplicate character from the set.
            // This ensures that we always have a window of unique characters.
            while(seen.contains(c)){
                seen.remove(str.charAt(left));
                left++;
            }
            seen.add(c);
            System.out.println(c+" "+left+" "+right);
            max= Math.max(right-left+1,max);
            System.out.println(max+"------ "+seen);

        }
        System.out.println("Max "+max);

    }

    //Time - O(n) and Space - O(1) with brute force it is n^3
    private void twoSum(){
        //time - O(n) and space - O(n)
        int[] arr = new int[]{6, 7, 2, 15};
        int target = 9;
        //No n index
        HashMap<Integer, Integer> hm = new HashMap<>();
        for (int i = 0; i < arr.length - 1; i++) {
            int other = target - arr[i];
            if (hm.containsKey(other)) {
                System.out.println(Arrays.toString(new int[]{i, hm.get(other)}));
            }
            hm.put(arr[i], i);
        }
        System.out.println(hm);

    }

    //Time - O(n) and Space - O(1) with brute force it is n^3
    private void mergingTwoSortedArray(){
        int[] arr1 = {1,3,5};
        int[] arr2 = {0,2,4,6};
        //Merging 2 sorted Arrays
        int res[]=new int[arr1.length+arr2.length];
        int left=0,right=0,k=0;
        System.out.println("k = "+k);
        while(left<arr1.length && right<arr2.length){
            System.out.println("left = "+left+" right = "+right);
            if(arr1[left]<arr2[right]){
                res[k++]=arr1[left++];
            }else{
                res[k++]=arr2[right++];
            }
            System.out.println("k = "+k+" "+Arrays.toString(res));
        }
        //remaining elements
        while(left<arr1.length) res[k++]=arr1[left++];
        while(right<arr2.length) res[k++]=arr2[right++];
        System.out.println(Arrays.toString(res));
    }
}


