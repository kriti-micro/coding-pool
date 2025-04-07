package SHL;

public class WordCount {
    /*
    * The an online word recognition game for kids, the user needs to find the number of times the given word occurs in the sentence. Both the given word and the sentence displayed on the user interface consist of letters from the English alphabet only and are case insensitive (ie, toddler is same as "Toddler") Neither the wond nor the sentence contain amy white-spaces or special symbols.
Write an algorithm to print the number of times the given word appears in the sentence in java
    * */
    public static void main(String[] args) {
        String sentence = "toddlertoddlerToddlertoddler";
        String word = "toddler";
        int result = countOccurrences(sentence, word);
        System.out.println("The word '" + word + "' appears " + result + " times in the sentence.");

    }

    private static int countOccurrences(String sentence, String word) {
        sentence = sentence.toLowerCase();
        word = word.toLowerCase();
        int index = 0;
        int count = 0;
        while ((index = sentence.indexOf(word, index)) != -1) {
            index += word.length();
            count++;
        }
        return count;
    }
}
