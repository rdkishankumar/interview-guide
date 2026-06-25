Let’s apply your exact fixed-size sliding window pattern to LeetCode **1876. Substrings of Size Three with Distinct Characters**.

In this problem, the window size $k$ is fixed at **3**. Instead of keeping track of a mathematical sum, our business logic needs to check if the 3 characters inside our window are distinct (i.e., no two characters are the same).

### The Logic Adjustments

* **Window Size ($k$):** Always 3.
* **Tracking Elements:** Since the window size is so small ($k = 3$), we can easily check if the characters are distinct using a simple `if` condition on the characters at indices `left`, `left + 1`, and `right`.

---

### Java Implementation (Using Your Pattern)

```java
class Solution {
    public int countGoodSubstrings(String s) {
        int n = s.length();
        int left = 0;
        int right = 0;
        int k = 3; // Fixed window size of 3
        int goodSubstringsCount = 0;
        
        while (right < n) {
            int window = right - left + 1;
            
            // Case A: Window is smaller than 3, keep expanding right
            if (window < k) {
                right++;
            } 
            // Case B: Window size is exactly 3
            else if (window == k) {
                // Business Logic: Check if all 3 characters in the current window are unique
                char c1 = s.charAt(left);
                char c2 = s.charAt(left + 1);
                char c3 = s.charAt(right); // same as left + 2
                
                if (c1 != c2 && c1 != c3 && c2 != c3) {
                    goodSubstringsCount++;
                }
                
                // Slide the window forward
                left++;
                right++;
            }
        }
        
        return goodSubstringsCount;
    }
}

```

---

### Why your template makes this trivial:

1. **No Edge Case Stress:** If the string length `n` is less than 3, the loop terminates safely because `window` will never reach `k`, and it returns `0` correctly.
2. **Clean State Changes:** You don't have to manage any complex data structures like HashSets to check for duplicates because the constraint is locked at 3. A simple $O(1)$ comparison of 3 character variables does the trick perfectly.
3. **Complexity:** This runs in $O(n)$ time complexity and uses $O(1)$ auxiliary space.