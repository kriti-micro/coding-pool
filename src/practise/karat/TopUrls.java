package practise.karat;

import java.util.*;
import java.nio.file.*;
import java.io.*;


public class TopUrls {

    //complexity O(nlogk) for maintaining the min heap and O(n) for reading the file, space complexity O(n) for storing the frequency map
    public static List<String> topNUrls(Path file,int k ) throws IOException{
        // No valid Top-N request
        if(k<=0){
            return Collections.emptyList();
        }

        //url->freq
        HashMap<String,Integer> freq=new HashMap<>();

        //Read file line by line
        try(BufferedReader reader=Files.newBufferedReader(file)){
            String line;
            while((line=reader.readLine())!=null){

                String url=line.trim();
                //ignore blank lines
                if(url.isEmpty()){
                    continue;
                }

                //Does the same
                //freq.merge(url,1,Integer::sum);
                freq.put(url,freq.getOrDefault(url,0)+1);

            }
        }

        /*
         * Min Heap.
         *
         * First compare frequency ASCENDING.
         * If frequency is equal,
         * compare URL DESCENDING.
         *
         * Why?
         * We want the WORST candidate at the root
         * so we can remove it when heap size > N.
         */
        PriorityQueue<Map.Entry<String,Integer>> minHeap=new PriorityQueue<>(
                Comparator.comparingInt(Map.Entry<String,Integer>::getValue).thenComparing(Map.Entry<String,Integer>::getKey,Comparator.reverseOrder())
        );

        for(Map.Entry<String,Integer> e: freq.entrySet()){
            // Add candidate
            minHeap.offer(e);
            // Keep only top N candidates
            if(minHeap.size()>k){
                minHeap.poll();
            }
        }

        List<String> result=new ArrayList<>();

        // Poll gives worst -> best
        while(!minHeap.isEmpty()){
            result.add(minHeap.poll().getKey());
        }

        // Convert to best -> worst
        Collections.reverse(result);

        return result;
    }

    public static void main(String args[]) throws IOException {

        List<String> result= topNUrls(Path.of("/Users/deepakpavaiya/IdeaProjects/coding-pool/src/practise/karat/LogUrl.txt"),2);
        System.out.println("List of top n urls : "+result);

    }

}