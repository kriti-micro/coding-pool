package SHL;

import java.util.Arrays;
import java.util.Scanner;

public class TownNameIdentifier {
    /*
    * There are N people living in a state.
    * In this state, people add their town name before their first name.
    * Write an algorithm to find the name of the town of the given N people where the name of the town is the common substring and has the maximum length.

 I/P : 3
GreenhillJohn
GreenhillMary
GreenhillJames

    * */

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read number of people
        int n = Integer.parseInt(scanner.nextLine());

        String[] names = new String[n];

        // Read each name
        for (int i = 0; i < n; i++) {
            names[i] = scanner.nextLine();
        }

        String commonTownName = findCommonTownName(names);
        System.out.println(commonTownName);
    }

    // Function to find longest common prefix
    private static String findCommonTownName(String[] names) {
        if(names==null || names.length==0) return "";
        //Setting 1st name as prefix and comparing it with other names i.e. 2nd and 3rd
        String prefix=names[0];

        for(int i =1;i<names.length;i++){
                while(!names[i].startsWith(prefix)){
                    // Reduce the prefix one character at a time
                    System.out.println("Names [i] = "+names[i]+" prefix = " + prefix);
                    prefix=prefix.substring(0,prefix.length()-1);
                    if(prefix.isEmpty()) return "";// No common prefix
                }
        }
        return prefix;
    }
}
