package SHL;

import java.util.*;

public class MaxHappyFriends {



// The cake is divided into N × M pieces.
//Each piece has a unique fruit, numbered from 1 to N*M.
//    There are K friends.
//    Each friend has a list of their favorite fruits.
//    Each piece can be given to only one friend.
//    Each friend can receive only one piece.
//    A friend is happy if they get a piece with a fruit from their favorite list.

//This is a bipartite graph problem:
//Left side: Friends (K nodes)
//Right side: Fruits (N×M nodes)
//An edge exists if a friend likes a fruit.
//Then, we apply maximum bipartite matching (using DFS[Depth First Search ] or BFS-based algorithms like Hopcroft-Karp)
static Map<Integer, List<Integer>> adjList = new HashMap<>();
    static int[] fruitAssignedTo; // fruitAssignedTo[fruit] = friend
    static boolean[] visited;

    public static int maxHappyFriends(int numFruits, int K, Map<Integer, List<Integer>> favMap) {
        fruitAssignedTo = new int[numFruits + 1];
        Arrays.fill(fruitAssignedTo, -1); // -1 means unassigned

        int result = 0;

        for (int friend = 0; friend < K; friend++) {
            visited = new boolean[numFruits + 1];
            if (dfs(friend, favMap)) {
                result++;
            }
        }

        return result;
    }

    private static boolean dfs(int friend, Map<Integer, List<Integer>> favMap) {
        for (int fruit : favMap.getOrDefault(friend, new ArrayList<>())) {
            if (visited[fruit]) continue;
            visited[fruit] = true;

            // If fruit is unassigned or the previously assigned friend can be reassigned
            if (fruitAssignedTo[fruit] == -1 || dfs(fruitAssignedTo[fruit], favMap)) {
                fruitAssignedTo[fruit] = friend;
                System.out.println("fruit = " + fruit + ", friend = " + friend);
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int N = 2, M = 3; // Total 6 fruits: 1 to 6
        int K = 3;        // 3 friends

        Map<Integer, List<Integer>> favMap = new HashMap<>();

        // Friend 0 likes fruits 1 and 2
        favMap.put(0, Arrays.asList(1, 2));

        // Friend 1 likes fruits 2, 3
        favMap.put(1, Arrays.asList(2, 3));

        // Friend 2 likes fruit 4
        favMap.put(2, Arrays.asList(2, 4));

        int maxHappy = maxHappyFriends(N * M, K, favMap);
        System.out.println("Max happy friends: " + maxHappy);  // Output: 3
    }
}
