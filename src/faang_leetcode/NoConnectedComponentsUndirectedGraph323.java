package faang_leetcode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//time-O(v+e)
public class NoConnectedComponentsUndirectedGraph323 {
    public static void main(String[] args) {
        int n = 5;
        int[][] edges = {
                {0, 1},
                {1, 2},
                {3, 4}
        };
        int totalComponents = countComponents(n, edges);
        System.out.println(" The total connected components : " + totalComponents);
    }

    private static int countComponents(int n, int[][] edges) {
        System.out.println("n = " + n + ", edges = " + Arrays.deepToString(edges));
        int counter = 0;
        int[] visited = new int[n];
        System.out.println("visited = " + Arrays.toString(visited));
        List<Integer>[] adjacencyList = new ArrayList[n];
        for (int i = 0; i < n; i++) {
            adjacencyList[i] = new ArrayList<>();
        }
//        for(int i=0;i<edges.length;i++){
//            adjacencyList[edges[i][0]].add(edges[i][1]);
//            adjacencyList[edges[i][1]].add(edges[i][0]);
//        }
        for (int[] edge : edges) {
            adjacencyList[edge[0]].add(edge[1]);
            adjacencyList[edge[1]].add(edge[0]);
        }
        System.out.println("adjacencyList = " + Arrays.deepToString(adjacencyList));

        for (int i = 0; i < n; i++) {
            if(visited[i]==0){
                counter++; // found a new component
                dfs(adjacencyList,visited,i);
            }
        }
        return counter;
    }
    private static void dfs(List<Integer>[] adjacencyList, int[] visited, int node){
        visited[node]=1;// mark as visited

        for (int j = 0; j < adjacencyList[node].size(); j++) {
            int neighbor=adjacencyList[node].get(j);
            //Checking for all neighbor and skipping if visited
            if(visited[neighbor]==0){
                dfs(adjacencyList,visited,neighbor);
            }
        }
    }
}
