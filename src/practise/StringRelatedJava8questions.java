package practise;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StringRelatedJava8questions {

    public static void main(String[] args) {
        System.out.println("------1. countFreqOfEachWord called : -----------");
        countFreqOfEachWord();
        System.out.println("------2. countFreqOfEachChar called : -----------");
        countFreqOfEachChar();
        System.out.println("------3. firstNonRepeatedCharacter called : -----------");
        firstNonRepeatedCharacter();
        System.out.println("------4. firstRepeatedCharacter called : -----------");
        firstRepeatedCharacter();
        System.out.println("------5. firstDuplicateCharacter called : -----------");
        firstDuplicateCharacter();
        System.out.println("------6. Reverse each word in a sentence called : -----------");
        reverseEachWordInSentence();
        reverseEachWordUsingCollections();
        reverseEachWordUsingReduce();
        System.out.println("------7. Check anagram : -----------");
        checkAnagram();
        checkAnagramUsingArraysStreamApproach();
        checkAnagramUsing26IntArr();
        groupAnagramWords();
        System.out.println("------8. Check Palindrome : -----------");
        checkPalindrome();
        checkPalindrome1();
        System.out.println("-----9.  Sort words by length-------");
        sortWordsByLength();
        System.out.println("-----10.  Find the longest word-------");
        findLongestWord();
        System.out.println("-----11.  Find the shoertest word-------");
        findShortestWord();
        System.out.println("-----12.  Remove the duplicates-------");
        removeDuplicateFromList();
        System.out.println("-----13.  Get duplicate element from the list-------");
        getDuplicateFromList();
        System.out.println("-----14.  Count occurences in the list-------");
        countOccurencesInList();
        System.out.println("-----15.  Convert list to uppercase-------");
        convertListToUppercase();
        System.out.println("-----16.  Join words-------");
        joinWords();
        System.out.println("-----17.  Top 3 most frequent words-------");
        top3MostFreqWords();
        System.out.println("-----18.  Group words by starting letter-------");
        groupWordsByStartingLetter();
        System.out.println("-----19.  Find the most repeated word-------");
        findTheMostRepeatedWords();
        System.out.println("-----20. Words that appear only once-------");
        wordsThatAppearOnlyOnce();
        System.out.println("-----21. Filter words longer than 5 character-------");
        filterWordsLongerThan5Char();
        System.out.println("-----22. Avg Word Length-------");
        avgWordLength();
        System.out.println("-----23. roman to integer-------");
        romanToInteger();



    }

    private static void romanToInteger() {
        String s="MCMXCIV";
        int result=0;
        Map<Character,Integer> hmap=Map.of('I',1,'V',5,'X',10,'L',50,'C',100,'D',500,'M',1000);
        for(int i =0;i<s.length();i++){
            int curr=hmap.get(s.charAt(i));
            int next=i+1<s.length()?hmap.get(s.charAt(i+1)):0;
            result += (curr < next) ? -curr : curr;
        }
        System.out.println("The roman to integer is "+result);
        // romanToInt("XIV") → 14
        // romanToInt("MCMXCIV") → 1994
    }

    private static void avgWordLength() {
        String s = "Java Stream API";
        Double avgWordLength=Arrays.stream(s.split(" ")).collect(Collectors.averagingInt(w->w.length()));
        System.out.println(" The Avg word length : "+avgWordLength);
    }

    private static void filterWordsLongerThan5Char() {
        String s = "Java Streams are powerful";
        List<String> list=Arrays.stream(s.split(" ")).filter(w->w.length()>5).toList();
        System.out.println(" list with word length Longer than 5 :"+list);
    }

    private static void wordsThatAppearOnlyOnce() {
        String s = "a b a c b d";
        List<String> uniqueList=Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(w->w,Collectors.counting()))
                .entrySet().stream().filter(e->e.getValue()==1).map(Map.Entry::getKey).toList();
        System.out.println("Words that appear only Once : "+uniqueList);
    }

    private static void findTheMostRepeatedWords() {
        String s = "hi hi hello hello hello";
        String mostRepeatedWord =Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(w->w,Collectors.counting()))
                .entrySet().stream().max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        System.out.println("The most repeated Word : "+mostRepeatedWord);
    }

    private static void groupWordsByStartingLetter() {
        String s = "hello hi heat apple bat";
        Map<Character,List<String>> map=Arrays.stream(s.split(" ")).collect(Collectors.groupingBy(w->w.charAt(0)));
        System.out.println(" Group words by starting letter : "+map);

        /**
         * We can replace List<Character> to wildcard ? & o/p for below is {r=[r], s=[s], t=[t], g=[g], h=[h], i=[i, i, i], k=[k], n=[n]}
         * Map<Character, List<Character>> freqChar = "Kriti Singh".toLowerCase()
         *                 .replace(" ", "")
         *                 .chars()
         *                 .mapToObj(c -> (char) c)
         *                 .collect(Collectors.groupingBy(Function.identity()));
         *
         *         System.out.println(freqChar);
         */
    }

    private static void top3MostFreqWords() {
        String s = "a b a c a b e d c c";
        Map<String,Long> map=Arrays.stream(s.split("\\s+")).collect(Collectors.groupingBy(w->w,Collectors.counting()));
        List<Map.Entry<String,Long>> list = map.entrySet().stream().sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())).limit(3).toList();
        System.out.println("Most 3 freq words : "+list);
    }

    private static void joinWords() {
        List<String> words = Arrays.asList("Java", "is", "awesome");
        String sentence= words.stream().collect(Collectors.joining(" "));
        System.out.println(" After joining words from list : "+sentence);
    }

    private static void convertListToUppercase() {
        List<String> list = Arrays.asList("hello", "kriti");
        List<String> uppercaseList=list.stream().map(String::toUpperCase).toList();
        System.out.println("UpperCase list : "+uppercaseList);
    }

    private static void countOccurencesInList() {
        List<String> list = Arrays.asList("a","b","a","c","b","a");
        Map<String,Long> map = list.stream().collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
        System.out.println("Count occurences in the list : "+map);
    }


    private static void getDuplicateFromList() {
        List<Integer> list = Arrays.asList(1,2,2,3,3,4);
        //Using Collections.frequency(list,i)
        Set<Integer> duplicateList = list.stream().filter(i->Collections.frequency(list,i)>1).collect(Collectors.toSet());
        System.out.println(" The duplicate List :"+duplicateList);
    }

    private static void removeDuplicateFromList() {
        List<Integer> list = Arrays.asList(1,2,2,3,3,4);
        List<Integer> uniqueList=list.stream().distinct().toList();
        System.out.println(" The Unique List :"+uniqueList);
    }

    private static void findShortestWord() {
        String s = "Java Kotlin Go Python";
        String shortWord= Arrays.stream(s.split(" ")).min(Comparator.comparingInt(String::length)).orElse("");
        System.out.println("The shortest word using min in string \""+s+"\""+" is "+shortWord);
    }

    private static void findLongestWord() {
        String s = "Java 8 Stream Interview";
        String longestWord=Arrays.stream(s.split(" ")).sorted(Comparator.comparing(String::length).reversed()).findFirst().orElse(null);
        System.out.println("The longest word using findfirst in string \""+s+"\""+" is "+longestWord);

        //or
        String longestWordUsingMax=Arrays.stream(s.split(" ")).max(Comparator.comparingInt(String::length)).orElse("");
        System.out.println("The longest word using max in string \""+s+"\""+" is "+longestWordUsingMax);

    }

    private static void sortWordsByLength() {
        String s="Hello Java Stream";
        List<String> list = Arrays.stream(s.split("\\s+")).sorted(Comparator.comparingInt(String::length)).collect(Collectors.toList());
        System.out.println(" Sorting word by length of the String \""+s+"\" : "+list);
    }

    private static void checkPalindrome() {
        String s="Madam";
        boolean result = IntStream.range(0,s.length()/2).allMatch(i->s.toLowerCase().charAt(i)==(s.toLowerCase().charAt(s.length()-1-i)));
        System.out.println(" The String "+s+" is palindrome using range[2nd param not included] n String method : "+result);
        //or
        boolean result1 = IntStream.range(0,s.length()/2).allMatch(i->Character.toLowerCase(s.charAt(i))==Character.toLowerCase(s.charAt(s.length()-1-i)));
        System.out.println(" The String "+s+" is palindrome using range[2nd param not included] n Character method : "+result1);

        //To understand range n rengeClosed so Palindrome use range
        IntStream.range(0,s.length()/2).forEach(i->System.out.println("Using range : "+s.toLowerCase().charAt(i) + " " +(s.toLowerCase().charAt(s.length()-1-i))));
        IntStream.rangeClosed(0,s.length()/2).forEach(i->System.out.println("Using rangeClosed : "+s.toLowerCase().charAt(i) + " " +(s.toLowerCase().charAt(s.length()-1-i))));
    }

    private static void checkPalindrome1() {
        String s="A man, a plan, a canal: Panama";
        String clean = s.replaceAll("[^a-zA-Z0-9]","").toLowerCase();
        boolean result = IntStream.range(0,clean.length()/2).allMatch(i->s.charAt(i)==(s.charAt(s.length()-1-i)));
        System.out.println(" The String "+s+" is palindrome using range[2nd param not included] n String method : "+result);
        }

    private static void checkAnagram() {
        String a="listen";
        String b="silent";

        //Note : boxed() can be used instead of mapToObject
        boolean result=a.toLowerCase().chars().mapToObj(c->c).sorted().collect(Collectors.toList())
                .equals(b.toLowerCase().chars().boxed().sorted().collect(Collectors.toList()));
        System.out.println(" The word a  "+a+" and b "+b+" is anagram  : "+result);

        //For testing
        a.chars().boxed().sorted().forEach((c)->{System.out.println(" a "+c);});
        System.out.println(" Printing all letters of word a and b in UNICODE :");
        b.chars().mapToObj(c->c).sorted().forEach((c)->System.out.println(" b "+c));

        //checking list comparison using equals so printing list
        System.out.println("Printing list for test : "+a.toLowerCase().chars().mapToObj(c->c).sorted().collect(Collectors.toList()));
    }

    public static void checkAnagramUsingArraysStreamApproach(){
        String a="listen";
        String b="silent";

        boolean result = Arrays.equals(a.chars().sorted().toArray(),b.chars().sorted().toArray()); //.toArray method used
        System.out.println(" The word a  "+a+" and b "+b+" is anagram  : "+result);
    }

    public static void checkAnagramUsing26IntArr(){
        String a="listen";
        String b="silent";
        boolean s=true;

        if(a.length()!=b.length())s=false;
        int[] fre=new int[26];
        for(char c:a.toCharArray())fre[c-'a']++;
        for(char c:b.toCharArray())fre[c-'a']--;

        for(int i=0;i<26;i++){
            if(fre[i]!=0){
                s=false;
                break;
            }
        }
        System.out.println(" is anagram : "+s);
    }

    public static void groupAnagramWords(){

        List<String> arr=List.of("eat","tea","tan","ate","nat","bat");
        Map<String,List<String>> map = arr.stream().collect(Collectors.groupingBy(
                w->{
                    char[] ch= w.toLowerCase().toCharArray();
                    Arrays.sort(ch);
                    return String.valueOf(ch);
                }
        ));
        System.out.println(" The group anagram words : "+map);

        //groupingBy() takes each stream element, applies your classifier function to produce a key, finds or creates the List associated with that key, and adds the original element to that list.
        /* *
         Map<String, List<String>> result = new HashMap<>();
          for (String word : words) {
            String key = getSortedKey(word);
            result
                .computeIfAbsent(key, k -> new ArrayList<>())
                .add(word);
           }
        * */
    }

    private static void reverseEachWordInSentence() {
        String sentence="Hello world";
        String result=Arrays.stream(sentence.split(" ")).map(s->new StringBuilder(s).reverse().toString()).collect(Collectors.joining(" "));
        System.out.println(" The reverse of each word in sentence "+sentence+" is "+result);
    }

    private static void reverseEachWordUsingCollections(){
        String sentence="Hello Java World";
        String result = Arrays.stream(sentence.split(" ")).collect(Collectors.collectingAndThen(
                Collectors.toList(),
                list->{
                    Collections.reverse(list);
                    return String.join(" ",list);
                }
        ));
        System.out.println(" The reverse of only sentence "+sentence+" is "+result);
    }

    private static void reverseEachWordUsingReduce(){
        String sentence="Hello Java World";
        String result = Arrays.stream(sentence.split(" ")).reduce("",(a,b)->b+" "+a);
        System.out.println(" The reverse of only sentence "+sentence+" is "+result);
    }

    private static void firstDuplicateCharacter() {
        String s="Mississippi";
        Map<Character,Long> map=s.toLowerCase().chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),Collectors.counting())).entrySet().stream().filter(e->e.getValue()>1).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue));
        System.out.println(" The duplicated Chars in word "+s+" is "+map);
    }

    private static void firstRepeatedCharacter() {
        String s="abac";
        Character result = s.toLowerCase().chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),LinkedHashMap::new,Collectors.counting())).entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(e->e.getValue()>1).map(Map.Entry::getKey).findFirst().orElse(null);
        System.out.println(" The firstRepeatedCharacter in word : "+s+ " is "+result);
    }

    private static void firstNonRepeatedCharacter() {
        String s = "aabbcddes";
        System.out.println(" The word is "+s);
        System.out.println(" 1st direct way print character without separate variable :");
        s.toLowerCase().chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),LinkedHashMap::new,Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(e->e.getValue()==1).findFirst().ifPresent(System.out::println);

        //to fetch character
        Character result = s.toLowerCase().chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),LinkedHashMap::new,Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue()).filter(e->e.getValue()==1).map(Map.Entry::getKey).findFirst().orElse(null);
        System.out.println(" 2nd way To get character in separte variable:"+result);

        //Map for verifying after sorting
        LinkedHashMap<Character,Long> mapForVerification=s.toLowerCase().chars().mapToObj(c->(char)c).collect(Collectors.groupingBy(Function.identity(),LinkedHashMap::new,Collectors.counting()))
                .entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1,e2)->e1,LinkedHashMap::new));
        System.out.println(" 3rd way To print map after sorting using Collectors.toMap :"+mapForVerification);
    }

    private static void countFreqOfEachChar() {
        String s="Banana9";
        System.out.println(" The word is "+s);
        Map<Character,Long> map=
                s.toLowerCase()
                .chars()
                .mapToObj(c->(char)c)
                .filter(Character::isLetter)
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));

        System.out.println(" The result is "+map);

        //to des order
        System.out.println(" To print map in desc order ");
        map.entrySet()
                .stream()
                .sorted( Map.Entry.<Character,Long>comparingByValue().reversed())
                .forEach(e->System.out.println(e.getKey()+" : "+e.getValue()));
        /**
         *
         * // ❌ Clunky Way (Requires manual types) Compilation error if we revove entrytype during entry comparing
         * freqChar.entrySet().stream()
         *     .sorted(Map.Entry.<Character, Long>comparingByValue().reversed())
         *
         * //  Modern Clean Way (No manual types needed!)
         * freqChar.entrySet().stream()
         *     .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
         *     .forEach(e -> System.out.println(e.getKey() + "= " + e.getValue()));
         */
        System.out.println(" Alternative way to print map in desc order ");
                        map.entrySet()
                        .stream()
                        .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                        .forEach(e->System.out.println(e.getKey()+" : "+e.getValue()));



    }

    public static void countFreqOfEachWord(){
        String word="Hello Kriti HellO";
        Map<String,Long> freqMap=Arrays.stream(word.split(" "))
                .map(String::toLowerCase).collect(Collectors.groupingBy(
                        Function.identity(),
                        Collectors.counting()
                ));
        System.out.println(" The result is "+freqMap);


        String word1="Hello, Kriti 0 It's your School.";
        System.out.println("\n How regex works in split for word : "+word1);
        //An alternative way by space
        System.out.println("\n Only by space delimiter : "+Arrays.toString(word1.split("\\s+")));
        System.out.println(" Only non alphanumeric delimiter : "+Arrays.toString(word1.split("[^A-Za-z0-9]+")));
        System.out.println(" Only  multiple delimiter : "+Arrays.toString(word1.split("[ ,;:@!#$%^&*()]+")));
        System.out.println(" Only non alphanumeric delimiter : "+Arrays.toString(word1.split("[^\\p{IsAlphabetic}]+")));
        System.out.println(" Only digits delimiter : "+Arrays.toString(word1.split("\\d+")));


    }
}
