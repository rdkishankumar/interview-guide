Here is the detailed structural breakdown and code note for your specific approach to LeetCode **3. Longest Substring Without Repeating Characters**.

### The Core Logic of Your Pattern

Your solution works beautifully because it uses the `HashMap` as a **safety valve**.

1. **Expand:** The `for` loop continuously advances the right side of the window, adding characters and tracking their frequencies.
2. **Detect Violation:** The moment a character's frequency hits `2`, a duplicate is detected.
3. **Shrink:** The inner `while` loop forces the `left` pointer forward, systematically evicting characters and reducing their counts in the map until the frequency of that duplicate character drops back down to `1`.
4. **Capture:** Once the map is valid again, the maximum length is updated.

---

### Code Blueprint & Revision Sheet

```java
import java.util.HashMap;
import java.util.Map;

class Solution {

    public int lengthOfLongestSubstring(String s) {
        int n = s.length();
        
        // Edge Case Handling
        if (s.isEmpty()) {
            return 0;
        }
        
        int maxLength = Integer.MIN_VALUE;
        Map<Character, Integer> map = new HashMap<>();
        int left = 0;
        
        // 'index' acts as the 'right' pointer expanding the window
        for (int index = 0; index < n; index++) {
            char ch = s.charAt(index);
            
            // Step 1: Expand phase (absorb current character)
            map.put(ch, map.getOrDefault(ch, 0) + 1);
            
            // Step 2: Shrink phase (triggered ONLY when a violation is found)
            while (map.get(ch) > 1) {
                char charLeft = s.charAt(left);
                
                // Evict the element at 'left' from our map counts
                map.put(charLeft, map.get(charLeft) - 1);
                if (map.get(charLeft) == 0) {
                    map.remove(charLeft);
                }
                
                // Shrink the window boundary inward
                left++;
            }
            
            // Step 3: Update phase (window is guaranteed valid here)
            maxLength = Math.max(maxLength, index - left + 1);
        }
        
        return maxLength;
    }
}

```

---

### Dry Run Execution Walkthrough

To visualize why this structure works perfectly, track how the variables change when evaluating `s = "pwwkew"`:

| `index` (Right) | `ch` | Map State (Before `while`) | `while` Condition (`map.get(ch) > 1`) | Action Taken | Window (`left` to `index`) | `maxLength` |
| --- | --- | --- | --- | --- | --- | --- |
| **0** | `'p'` | `{'p': 1}` | `1 > 1` (False) | Skip `while` | `"p"` | `1` |
| **1** | `'w'` | `{'p': 1, 'w': 1}` | `1 > 1` (False) | Skip `while` | `"pw"` | `2` |
| **2** | `'w'` | `{'p': 1, 'w': 2}` | `2 > 1` (**True**) | Evict `'p'` (`left++`), then evict first `'w'` (`left++`) | `"w"` | `2` |
| **3** | `'k'` | `{'w': 1, 'k': 1}` | `1 > 1` (False) | Skip `while` | `"wk"` | `2` |
| **4** | `'e'` | `{'w': 1, 'k': 1, 'e': 1}` | `1 > 1` (False) | Skip `while` | `"wke"` | **`3`** |
| **5** | `'w'` | `{'w': 2, 'k': 1, 'e': 1}` | `2 > 1` (**True**) | Evict `'w'` from left (`left++`) | `"kew"` | `3` |

### Key Takeaways for Revision

* **Complexity:** Runs in $O(n)$ time because every character is added to the map once by the `for` loop and removed at most once by the `while` loop. Space complexity is $O(1)$ effectively because the map size is capped by the total number of unique characters in the alphabet.
* **Why this is resilient:** By setting the `while` condition explicitly to evaluate the current character (`map.get(ch) > 1`), you never need to scan the whole map to see if it's valid. The map *only* becomes invalid because of the character you just added at `index`.