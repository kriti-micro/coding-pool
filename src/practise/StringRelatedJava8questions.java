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
        System.out.println("------7. Check anagram : -----------");
        checkAnagram();
        System.out.println("------8. Check Palindrome : -----------");
        checkPalindrome();
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

    private static void reverseEachWordInSentence() {
        String sentence="Hello world";
        String result=Arrays.stream(sentence.split(" ")).map(s->new StringBuilder(s).reverse().toString()).collect(Collectors.joining(" "));
        System.out.println(" The reverse of each word in sentence "+sentence+" is "+result);
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
