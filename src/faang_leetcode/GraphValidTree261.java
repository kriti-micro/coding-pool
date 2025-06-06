package faang_leetcode;

import java.util.*;

//Time and Space -- O(n)
public class GraphValidTree261 {
    public static void main(String[] args) {
        int n=5;
        int[][] edges={
                {0,1},
                {0,2},
                {0,3},
                {1,4}
        };
        boolean result=validTree(n,edges);
        //Ensured the graph has no cycles and just enough connections to be a tree.
        System.out.println(" the result is validTree :"+result);
    }

    private static boolean validTree(int n, int[][] edges) {
        System.out.println("n = " + n + ", edges = " + Arrays.deepToString(edges));

        //A valid tree must have exactly n-1 edges.
        if(edges.length != n-1)
            return false;

        List<List<Integer>> adjacencyList=new ArrayList<>();
        for(int i=0;i<n;i++){
            adjacencyList.add(new ArrayList<>());
        }

        //All connections are two-way (undirected)
        for(int[] edge : edges){
            adjacencyList.get(edge[0]).add(edge[1]);
            adjacencyList.get(edge[1]).add(edge[0]);
        }
        System.out.println("adjacencyList = " + adjacencyList);

        //Start DFS traversal from node 0 using a stack (iterative approach).
        // Also keep a visited set to avoid revisiting nodes and to detect cycles.
        Stack<Integer> stack =new Stack<>();
        HashSet<Integer> visited=new HashSet<>();
        stack.push(0);
        visited.add(0);

        while(!stack.isEmpty()){
            int node=stack.pop();
            for(int neighbour : adjacencyList.get(node)){
                if(visited.contains(neighbour)){
                    continue;//skip the logic further for current neighbour loop
                }
                stack.push(neighbour);
                visited.add(neighbour);
            }
            System.out.println(" the stack : "+stack);
        }
        System.out.println(" the visited having :"+visited);
        if(visited.size()==n){
            return true;
        }
    return false;
    }
}
