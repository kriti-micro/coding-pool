package faang_leetcode;

import java.util.*;

//Time n Space - O(v+e) O(v) check
public class CloneGraph {

        HashMap<Node,Node> hmp=new HashMap<>();
        public Node cloneGraph(Node node) {

            if(node == null){
                return node;
            }
            if(hmp.containsKey(node)){
                return hmp.get(node);
            }
            Node clonedNode=new Node(node.val,new ArrayList());
            hmp.put(node,clonedNode);
            for(Node neighbor:node.neighbors){
                clonedNode.neighbors.add(cloneGraph(neighbor));
            }
            return clonedNode;
        }

    public static void main(String[] args) {
        // Create a simple graph: 1--2
        //                         |  |
        //                         4--3
        Node node1 = new Node(1);
        Node node2 = new Node(2);
        Node node3 = new Node(3);
        Node node4 = new Node(4);

        node1.neighbors.add(node2);
        node1.neighbors.add(node4);

        node2.neighbors.add(node1);
        node2.neighbors.add(node3);

        node3.neighbors.add(node2);
        node3.neighbors.add(node4);

        node4.neighbors.add(node1);
        node4.neighbors.add(node3);

        CloneGraph sol = new CloneGraph();
        Node clonedGraph = sol.cloneGraph(node1);

        // Print cloned graph
        Set<Node> visited = new HashSet<>();
        printGraph(clonedGraph, visited);
    }

    // Helper method to print the graph (DFS)
    public static void printGraph(Node node, Set<Node> visited) {
        if (node == null || visited.contains(node)) return;
        visited.add(node);
        System.out.print("Node " + node.val + " neighbors: ");
        for (Node neighbor : node.neighbors) {
            System.out.print(neighbor.val + " ");
        }
        System.out.println();
        for (Node neighbor : node.neighbors) {
            printGraph(neighbor, visited);
        }
    }

}


// Definition for a Node.
class Node {
    public int val;
    public List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new ArrayList<Node>();
    }
    public Node(int _val, ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}

