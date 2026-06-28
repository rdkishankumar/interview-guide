LeetCode 526. Beautiful Arrangement
This problem is an elegant variation of generating permutations, where we count arrangements that satisfy a specific mathematical rule.

## 1. Understanding the Problem
We are given an integer n. We want to construct a permutation of numbers from 1 to n (using each number exactly once) such that the permutation is beautiful.

An arrangement is considered beautiful if, for every position i (from 1 to n), either of the following conditions is true:

The number at position i is divisible by i (nums[i](modi)==0).

i is divisible by the number at position i (i(modnums[i])==0).

Our goal is to return the total number of unique beautiful arrangements possible.

## 2. Blueprinting the Backtracking Tree
We can visualize this using our standard Slots vs. Choices mental model:

idx (The Slot Pointer): Tracks the current position index we are trying to fill (from 1 up to n).

index (The Candidate Scanner): Loops through the array elements to find a valid number to swap into slot idx.

The Backtracking Magic (Pruning Rule):
In standard permutations, we swap elements blindly and check if they are valid at the very end (base case). For this problem, that would cause a Time Limit Exceeded (TLE) error.

Instead, we check the rule before making the recursive call. If a number doesn't fit the divisibility rule for slot idx, we don't even bother exploring further down that branch.

## 3. Clean Java Implementation (Using idx and swap)
Since we are generating arrangements using unique numbers (1 to n) without duplicates, we can use the high-speed In-Place Swapping template. We track our total count using a single-element primitive array result passed by reference.

```java
class Solution {
public int countArrangement(int n) {
// Initialize the array with numbers 1 to n
int[] nums = new int[n];
for (int i = 0; i < n; i++) {
nums[i] = i + 1;
}

        // Use a single-element array to track the total count across recursive calls
        int[] result = new int[1];
        
        // Start filling from slot 1 (1-based indexing for the problem logic)
        solve(1, n, nums, result);
        return result[0];
    }

    private void solve(int idx, int n, int[] nums, int[] result) {
        // Base Case: All slots successfully filled with valid numbers
        if (idx > n) {
            result[0]++;
            return;
        }

        // Standard permutation loop: index scans options available for slot 'idx'
        // Note: array is 0-indexed, so we map slot 'idx' to array position 'idx - 1'
        for (int index = idx - 1; index < n; index++) {
            
            // PRUNING OPTIMIZATION: Check the beautiful condition BEFORE swapping/exploring
            // We test if the candidate number nums[index] is valid for slot 'idx'
            if (nums[index] % idx == 0 || idx % nums[index] == 0) {
                
                // 1. Choose: Lock the valid candidate into slot 'idx'
                swap(idx - 1, index, nums);
                
                // 2. Explore: Move strictly to the next slot (idx + 1)
                solve(idx + 1, n, nums, result);
                
                // 3. Un-choose (Backtrack): Undo the swap for the next loop run
                swap(idx - 1, index, nums);
            }
        }
    }

    private void swap(int i, int j, int[] nums) {
        int temp = nums[i];
        nums[i] = nums[j];
        nums[j] = temp;
    }
}


```
## 4. The 3 Pillars of this Backtracking Template
Inside our beautiful arrangement conditional check, the structural phases mirror our standard swap template perfectly:

Choose: Lock the scanned candidate into the active position: swap(idx - 1, index, nums).

Explore: Move deeper down the tree by calling solve(idx + 1, ...) to evaluate the next slot position.

Backtrack (Un-choose): Reset the array layout by executing swap(idx - 1, index, nums) right back, keeping the array structurally prepared for the next horizontal loop candidate.

## 5. Complexity Breakdown
Time Complexity: O(Number of Beautiful Arrangements)
The absolute worst-case time bound for basic permutations is O(n!). However, because our divisibility check eliminates the vast majority of invalid branches at the very top of the tree, the actual number of states explored is incredibly small. For n=15 (the maximum constraint), this executes in a few milliseconds.

Space Complexity: O(n)
The memory footprint is entirely determined by the maximum depth of the recursive call stack, which reaches exactly n frames before hitting the base case condition. No extra data structures are allocated.