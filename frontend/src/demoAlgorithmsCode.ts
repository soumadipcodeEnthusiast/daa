export const DEMO_JAVA_CODE = `package demo;

import java.util.*;

/**
 * Demo showcase class containing multiple classical algorithms and data structures
 * for Code Reverse reverse-engineering and complexity analysis.
 */
public class DemoAlgorithms {

    // -------------------------------------------------------------------------
    // 1. Quick Sort (Divide and Conquer, O(n log n) expected)
    // -------------------------------------------------------------------------
    public void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            int pi = partition(arr, low, high);
            quickSort(arr, low, pi - 1);
            quickSort(arr, pi + 1, high);
        }
    }

    private int partition(int[] arr, int low, int high) {
        int pivot = arr[high];
        int i = low - 1;
        for (int j = low; j < high; j++) {
            if (arr[j] <= pivot) {
                i++;
                int temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        int temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    // -------------------------------------------------------------------------
    // 2. Binary Search (Logarithmic Search, O(log n))
    // -------------------------------------------------------------------------
    public int binarySearch(int[] arr, int target) {
        int low = 0;
        int high = arr.length - 1;
        while (low <= high) {
            int mid = low + (high - low) / 2;
            if (arr[mid] == target) {
                return mid;
            } else if (arr[mid] < target) {
                low = mid + 1;
            } else {
                high = mid - 1;
            }
        }
        return -1;
    }

    // -------------------------------------------------------------------------
    // 3. Two Pointers (Linear Convergence, O(n))
    // -------------------------------------------------------------------------
    public boolean twoPointerPairSum(int[] sortedArr, int target) {
        int left = 0;
        int right = sortedArr.length - 1;
        while (left < right) {
            int sum = sortedArr[left] + sortedArr[right];
            if (sum == target) {
                return true;
            } else if (sum < target) {
                left++;
            } else {
                right--;
            }
        }
        return false;
    }

    // -------------------------------------------------------------------------
    // 4. Bubble Sort (Quadratic Comparison Sort, O(n^2))
    // -------------------------------------------------------------------------
    public void bubbleSort(int[] arr) {
        int n = arr.length;
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (arr[j] > arr[j + 1]) {
                    int temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }
            }
        }
    }

    // -------------------------------------------------------------------------
    // 5. Breadth-First Search (Graph Traversal, O(V + E))
    // -------------------------------------------------------------------------
    public List<Integer> bfs(int startNode, List<List<Integer>> adjacencyList, int numNodes) {
        List<Integer> traversalOrder = new ArrayList<>();
        boolean[] visited = new boolean[numNodes];
        Queue<Integer> queue = new LinkedList<>();

        visited[startNode] = true;
        queue.add(startNode);

        while (!queue.isEmpty()) {
            int current = queue.poll();
            traversalOrder.add(current);
            for (int neighbor : adjacencyList.get(current)) {
                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    queue.add(neighbor);
                }
            }
        }
        return traversalOrder;
    }

    // -------------------------------------------------------------------------
    // 6. Depth-First Search (Recursive Graph Traversal, O(V + E))
    // -------------------------------------------------------------------------
    public void dfs(int node, boolean[] visited, List<List<Integer>> adj) {
        visited[node] = true;
        for (int neighbor : adj.get(node)) {
            if (!visited[neighbor]) {
                dfs(neighbor, visited, adj);
            }
        }
    }

    // -------------------------------------------------------------------------
    // 7. Dynamic Programming (Tabulation, O(m * n))
    // -------------------------------------------------------------------------
    public int longestCommonSubsequence(String text1, String text2) {
        int m = text1.length();
        int n = text2.length();
        int[][] dp = new int[m + 1][n + 1];

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (text1.charAt(i - 1) == text2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1] + 1;
                } else {
                    dp[i][j] = Math.max(dp[i - 1][j], dp[i][j - 1]);
                }
            }
        }
        return dp[m][n];
    }

    // -------------------------------------------------------------------------
    // 8. Core Data Structures: PriorityQueue (Min-Heap) and Frequency Map
    // -------------------------------------------------------------------------
    public List<Integer> findTopKFrequent(int[] nums, int k) {
        Map<Integer, Integer> freqMap = new HashMap<>();
        for (int num : nums) {
            freqMap.put(num, freqMap.getOrDefault(num, 0) + 1);
        }

        PriorityQueue<Integer> minHeap = new PriorityQueue<>(Comparator.comparingInt(freqMap::get));
        for (int key : freqMap.keySet()) {
            minHeap.add(key);
            if (minHeap.size() > k) {
                minHeap.poll();
            }
        }

        List<Integer> topK = new ArrayList<>(minHeap);
        return topK;
    }
}
`
