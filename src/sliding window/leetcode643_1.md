Using the **Two-Pointer Loop (`left` and `right`)** pattern for fixed-size window problems is an excellent approach. It keeps your code format 100% consistent across *all* sliding window variations—whether the window size is fixed or dynamic.

Here is your exact pattern applied to **643. Maximum Average Subarray I**, fully functional with correct types and a minor adjustment for maximum precision.

### The Code Pattern

```java
class Solution {

    public double findMaxAverage(int[] nums, int k) {
        int n = nums.length;
        int left = 0;
        int right = 0;
        
        // Use Double.NEGATIVE_INFINITY or -Double.MAX_VALUE because 
        // the array elements can be negative, and averages can be less than Integer.MIN_VALUE
        double maxSum = Double.NEGATIVE_INFINITY;
        double sum = 0;
        
        while (right < n) {
            // Step 1: Expand the window by adding the element at 'right'
            sum += nums[right];
            int window = right - left + 1;
            
            // Case A: The window is still too small, just expand right boundary
            if (window < k) {
                right++;
            } 
            // Case B: The window hits exactly size 'k'
            else if (window == k) {
                // Record the maximum sum found so far
                maxSum = Math.max(maxSum, sum);
                
                // Shrink the window from the left before moving forward
                sum -= nums[left];
                left++;
                right++;
            }
        }
        
        // Divide the maximum sum by k to get the max average
        return maxSum / k;
    }
}

```

---

### Why this specific template is brilliant for all Fixed-Size problems:

By framing the problem as an expansion phase (`window < k`) and a maintenance phase (`window == k`), you follow a predictable structure:

1. **The Right Pointer Always Accumulates:** `sum += nums[right]` handles the incoming element automatically at the start of every loop iteration.
2. **The "Too Small" Gate:** The `if (window < k)` block cleanly handles building up the first valid window without writing an entirely separate initialization loop.
3. **The "Slide" Step is Self-Contained:** Once `window == k`, you do your business logic (like updating `maxSum`), remove the outgoing element (`sum -= nums[left]`), and increment both pointers (`left++`, `right++`) to cleanly shift the frame down the track.