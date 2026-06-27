```java


class Solution {

    public List<List<Integer>> permute(int[] nums) {
        List<Integer> track = new ArrayList<>();
        List<List<Integer>> result = new ArrayList<>();
        int n = nums.length;
        solve(0, n, nums, track, result);
        return result;
    }

    private void solve(int idx, int n, int[] nums, List<Integer> track, List<List<Integer>> result) {
        if (idx == n) {
            result.add(new ArrayList<>(track));
            return;
        }
        for (int index = idx; index < n; index++) {
            track.add(nums[index]);
            swap(idx, index, nums);
            solve(idx + 1, n, nums, track, result);
            track.remove(track.size() - 1);
            swap(idx, index, nums);
        }
    }

    private void swap(int idx, int index, int[] nums) {
        int num = nums[index];
        nums[index] = nums[idx];
        nums[idx] = num;
    }
}
```

## LeetCode 46. Permutations (Notes)

Permutations involve finding all possible arrangements of a given set of elements. Unlike combinations, **order matters** (e.g., `[1, 2, 3]` and `[1, 3, 2]` are completely distinct). This particular implementation uses an **in-place swap-based backtracking** mechanism.

### \## 1. The Core Mental Model: Slots vs. Choices

To understand this algorithm, visualize **$n$ empty slots** in front of you that you need to fill using the elements in `nums`.

*   **`idx` (The Slot Pointer):** Tracks which specific position in the array you are trying to fill _at this moment_. You must fill slot `0`, then slot `1`, then slot `2`, sequentially. It controls the **vertical depth** of your recursion.

*   **`index` (The Candidate Scanner):** A loop variable that scans across the array to find which elements are available to be swapped into the current slot `idx`. It controls the **horizontal choices** at the current level.


### \## 2. Why the Recursive Call uses `idx + 1` and NOT `index + 1`

This is the most critical structural logic of the swap-based permutation method:

1.  **Locking the Slot:** When you swap the element at `index` with the element at `idx`, you have officially decided that `nums[index]` will occupy slot `idx` for this branch.

2.  **Moving to the Next Slot:** Now that slot `idx` is taken care of, your immediate next task is to fill the very next slot: **`idx + 1`**.

3.  **The Flaw of `index + 1`:** `index` is just a loop runner. If you reach out to the end of the array to pick a candidate (say, `index = 2`), passing `index + 1` would make the next recursion layer jump all the way to slot `3`. This completely skips filling slots `1` and `2`, leaving them unprocessed and fracturing the permutation tree.


### \## 3. Complete Dry Run

*   **Input Array:** `nums = [1, 2, 3]` ($n = 3$)

*   **Initial State:** `idx = 0`, `track = []`


#### Execution Trace (Leftmost Branch Validation):

1.  **Level 0 (`idx = 0`): Filling Slot 0**

    *   Loop starts at `index = idx = 0`.

    *   **Choose:** `track.add(nums[0])` $\\rightarrow$ `track = [1]`.

    *   `swap(0, 0)` $\\rightarrow$ `nums` remains `[1, 2, 3]`.

    *   **Explore:** Recursively calls `solve(idx + 1, ...)` $\\rightarrow$ **`solve(1, ...)`**

2.  **Level 1 (`idx = 1`): Filling Slot 1**

    *   Loop starts at `index = idx = 1`.

    *   **Choose:** `track.add(nums[1])` $\\rightarrow$ `track = [1, 2]`.

    *   `swap(1, 1)` $\\rightarrow$ `nums` remains `[1, 2, 3]`.

    *   **Explore:** Recursively calls `solve(idx + 1, ...)` $\\rightarrow$ **`solve(2, ...)`**

3.  **Level 2 (`idx = 2`): Filling Slot 2**

    *   Loop starts at `index = idx = 2`.

    *   **Choose:** `track.add(nums[2])` $\\rightarrow$ `track = [1, 2, 3]`.

    *   `swap(2, 2)` $\\rightarrow$ `nums` remains `[1, 2, 3]`.

    *   **Explore:** Recursively calls `solve(idx + 1, ...)` $\\rightarrow$ **`solve(3, ...)`**

4.  **Level 3 (`idx = 3`): Base Case Hit**

    *   Condition met: `idx == n` ($3 == 3$).

    *   A deep copy of `track` (`[1, 2, 3]`) is saved to `result`.

    *   Returns back to Level 2.

5.  **Backtracking (Un-choose) at Level 2:**

    *   `track.remove(2)` $\\rightarrow$ `track = [1, 2]`.

    *   `swap(2, 2)` restores elements. Loop finishes. Returns to Level 1.

6.  **Pivoting to Next Candidate at Level 1:**

    *   `track.remove(1)` $\\rightarrow$ `track = [1]`.

    *   `swap(1, 1)` restores elements.

    *   **Loop increments:** `index = 2`.

    *   **Choose:** `track.add(nums[2])` (which is `3`) $\\rightarrow$ `track = [1, 3]`.

    *   `swap(1, 2)` $\\rightarrow$ `nums` becomes `[1, 3, 2]`.

    *   **Explore:** Calls `solve(idx + 1)` $\\rightarrow$ **`solve(2, ...)`** (fills slot 2 next).


### \## 4. The 3 Pillars inside the Loop

```java



    for (int index = idx; index < n; index++) {
        // 1. CHOOSE
        track.add(nums[index]);
        swap(idx, index, nums); // Lock candidate into slot 'idx'
        
        // 2. EXPLORE
        solve(idx + 1, n, nums, track, result); // Move to slot 'idx + 1'
        
        // 3. UN-CHOOSE (BACKTRACK)
        track.remove(track.size() - 1);
        swap(idx, index, nums); // Undo the swap for the next loop run
    }
```


### \## 5. Complexity Summary

*   **Time Complexity:** $\\mathcal{O}(n \\cdot n!)$

    There are $n!$ unique permutations. For each permutation leaf node, copying the path of size $n$ into the final result list takes $\\mathcal{O}(n)$ time.

*   **Space Complexity:** $\\mathcal{O}(n)$

    The recursion stack depth is strictly bounded by the number of slots, which scales linearly up to $n$.