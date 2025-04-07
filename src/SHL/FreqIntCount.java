package SHL;

import java.util.*;

/*Design a way to sort the list of positive integers in the descending order according to
frequency of the elements. The elements with higher frequency come before those with lower frequency.
Elements with the same frequency come in the same order as they appear in the given list
 */
public class FreqIntCount {
    public static void main(String ...args){
        System.out.println("Program for Freq of Int Count");
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter the Array of Integer :");
        //Input : 40 20 30 20 30 40 40 10
        // In : 1 2 2 3 3 3 4 4 5 5 5 5 6 6 6 7 8 9 10
        int[] nonSortArr=new int[19];
        for(int i=0;i<nonSortArr.length;i++)
            nonSortArr[i]=sc.nextInt();

        for(int i=0;i<nonSortArr.length;i++)
            System.out.print(nonSortArr[i]);
        
        int[] result=frequencySort(nonSortArr);
        System.out.println("\n Desc sorting of freq of Int :");
        for(int i=0;i<nonSortArr.length;i++)
            System.out.print(" "+result[i]);
    }

    private static int[] frequencySort(int[] nonSortArr) {
        LinkedHashMap<Integer,Integer> freqMap=new LinkedHashMap<>();
        //HashMap we don't store the same key twice and keys are unique
        for(int i=0;i<nonSortArr.length;i++)
            freqMap.put(nonSortArr[i],freqMap.getOrDefault(nonSortArr[i],0)+1);

        List<Map.Entry<Integer,Integer>> freq=new ArrayList<>(freqMap.entrySet());
        Collections.sort(freq, new Comparator<Map.Entry<Integer, Integer>>() {
            @Override
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return o2.getValue()-o1.getValue();
            }
        });
        int[] result=new int[nonSortArr.length];
        int count=0;
        System.out.println("\n Printing the sorted map entries list :");
        for(Map.Entry<Integer,Integer> item:freq){
            System.out.print(" Key "+item.getKey()+" Value "+item.getValue());
            for(int i=0;i<item.getValue();i++){
                result[count]= item.getKey();
                count++;
            }
        }
        return result;
    }
}
