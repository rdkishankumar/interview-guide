The problem **LeetCode 394: Decode String** asks us to decode an encoded string format like `3[a]2[bc]` into `aaabcbc`. The encoding rule is `k[encoded_string]`, where the `encoded_string` inside the square brackets is repeated exactly `k` times. Crucially, nested structures are allowed (e.g., `3[a2[c]]` becomes `accaccacc`).

This problem maps directly to the **Nested Expression Parsing** pattern. Here is how to move from a brute force/recursive mental model to an optimal stack-based approach.

---

## 1. The Brute Force Mindset: Deepest Brackets First (String Manipulation)

A brute-force approach mimics how a human would solve it on paper: scan the string, find the innermost set of square brackets `[...]`, decode that small segment, replace it in the original string, and repeat the process until no brackets remain.

### How it works:

1. Scan the string from left to right until you find the *first* closing bracket `]`.
2. Look backward from that position to find its corresponding opening bracket `[`.
3. Look further backward to extract the integer multiplier $k$.
4. Decode that single chunk (repeat the substring $k$ times).
5. Splice the decoded string back into the main string, replacing the `k[substring]` block.
6. Reset your scan and repeat from step 1 until all brackets are gone.

### Complexity:

* **Time Complexity:** $O(N^2)$ or worse. Every time you decode an inner bracket, you reconstruct the string. If you have deep nesting like `2[2[2[2[a]]]]`, you spend massive time copying substrings over and over.
* **Space Complexity:** $O(N)$ to create new modified strings during replacement phases.

---

## 2. The Intuitive Pattern: Recursion (Divide and Conquer)

Because the problem has a nested structure ("a block inside a block"), it naturally forms a tree-like hierarchy. This makes **Recursion** the most intuitive structural pattern.

### How it works:

Think of a recursive function `decode(index)` that processes the string from a given position.

* If it sees regular characters, it appends them to a local result string.
* If it hits a digit, it builds the number `k`.
* If it hits an opening bracket `[`, it pauses and recursively calls itself to decode the *inside* of the bracket.
* The recursive call processes everything up until the matching closing bracket `]` and returns the decoded sub-segment.
* The parent function multiplies that returned segment `k` times, appends it, and continues scanning where the sub-call left off.

### Code Example (Java):

```java
class Solution {
    private int pointer = 0; // Global pointer to track string position across recursive calls

    public String decodeString(String s) {
        StringBuilder result = new StringBuilder();
        int k = 0;

        while (pointer < s.length()) {
            char ch = s.charAt(pointer);

            if (Character.isDigit(ch)) {
                k = k * 10 + (ch - '0');
                pointer++;
            } else if (ch == '[') {
                pointer++; // Skip '['
                String nestedString = decodeString(s); // Recursive Call
                
                // Append the nested string k times
                while (k > 0) {
                    result.append(nestedString);
                    k--;
                }
            } else if (ch == ']') {
                pointer++; // Skip ']'
                return result.toString(); // Return decoded inner block to parent
            } else {
                result.append(ch);
                pointer++;
            }
        }
        return result.toString();
    }
}

```

### Complexity:

* **Time Complexity:** $O(\text{Length of Output String})$ because every character in the final decoded output is touched and appended.
* **Space Complexity:** $O(N)$ where $N$ is the depth of nesting, which determines the maximum size of the call stack.

---

## 3. The Optimal Pattern: Iterative Solution Using Two Stacks

While recursion is clean, it relies on the implicit system call stack. In production environments, deep nesting can risk a `StackOverflowError`. The absolute optimal approach converts this to an iterative pattern using **Explicit Stacks**.

Because we are tracking two different tracking types that change at different rates—the multiplier $k$ and the string prefixes—we use **two separate stacks**:

1. `countStack`: Stores the integer multipliers ($k$).
2. `stringStack`: Stores the previous string segments we accumulated *before* hitting the current `[` boundary.

### How it works (The State Machine):

As you iterate through the string character by character:

* **It's a Digit:** Build up your `currentK` integer variable.
* **It's an Alphabetic Character:** Append it to your `currentString` builder.
* **It's `[` (Entering a Nest):** We need to save our current state before diving deeper. Push `currentK` to `countStack` and push `currentString` to `stringStack`. Reset both `currentK` and `currentString` to zero/empty.
* **It's `]` (Exiting a Nest):** Pop the repeat count from `countStack`. Pop the outer prefix string from `stringStack`. Reconstruct the segment: `outerPrefix + (currentString repeated count times)`. This newly merged string becomes your new `currentString`.

### Code Example (Java):

```java
class Solution {
    public String decodeString(String s) {
        Stack<Integer> countStack = new Stack<>();
        Stack<StringBuilder> stringStack = new Stack<>();
        StringBuilder currentString = new StringBuilder();
        int currentK = 0;

        for (char ch : s.toCharArray()) {
            if (Character.isDigit(ch)) {
                currentK = currentK * 10 + (ch - '0');
            } else if (ch == '[') {
                // Save the current state onto stacks
                countStack.push(currentK);
                stringStack.push(currentString);
                
                // Reset states for the new inner bracket context
                currentString = new StringBuilder();
                currentK = 0;
            } else if (ch == ']') {
                // Decode the current inner block
                int repeatTimes = countStack.pop();
                StringBuilder decodedBlock = new StringBuilder(stringStack.pop());
                
                // Append currentString to its outer prefix string repeatTimes
                while (repeatTimes > 0) {
                    decodedBlock.append(currentString);
                    repeatTimes--;
                }
                // The outer prefix + repeated block becomes our current working string
                currentString = decodedBlock;
            } else {
                currentString.append(ch);
            }
        }
        return currentString.toString();
    }
}

```

### Complexity:

* **Time Complexity:** $O(\text{Length of Output String})$ — Linear relative to the size of the final decoded characters we produce.
* **Space Complexity:** $O(M + K)$ where $M$ is the number of nested bracket layers (stack depths) and $K$ is the length of memory holding the growing strings. This avoids the overhead of internal runtime recursion call stacks.

---
# Dry Run

Let's execute a detailed line-by-line dry run of your two-stack code. We will use a classic nested example that tests every edge case: **`s = "3[a]2[bc]"`**.

### Initial State

Before entering the loop:

* `countStack` = `[]`
* `stringStack` = `[]`
* `currentString` = `""`
* `currentK` = `0`

---

## Step-by-Step Execution Trace

### **Part 1: Processing `3[a]**`

| Step | Char | Matched Block | Action Taken | Variables State | Stacks State |
| --- | --- | --- | --- | --- | --- |
| **1** | `'3'` | `isDigit` | `currentK = 0 * 10 + 3` | `currentK = 3`<br>

<br>`currentString = ""` | `countStack = []`<br>

<br>`stringStack = []` |
| **2** | `'['` | `ch == '['` | Push current states, then reset tracking variables. | `currentK = 0`<br>

<br>`currentString = ""` | `countStack = [3]`<br>

<br>`stringStack = [""]` |
| **3** | `'a'` | `else` (char) | Append `'a'` to `currentString`. | `currentK = 0`<br>

<br>`currentString = "a"` | `countStack = [3]`<br>

<br>`stringStack = [""]` |
| **4** | `']'` | `ch == ']'` | Pop `repeatTimes = 3`. <br>

<br>Pop `decodedBlock = ""` (from stringStack).<br>

<br>Append `"a"` three times to `decodedBlock`. | `currentK = 0`<br>

<br>`currentString = "aaa"` | `countStack = []`<br>

<br>`stringStack = []` |

---

### **Part 2: Processing `2[bc]**`

Continuing immediately with `currentString = "aaa"` and `currentK = 0`:

| Step | Char | Matched Block | Action Taken | Variables State | Stacks State |
| --- | --- | --- | --- | --- | --- |
| **5** | `'2'` | `isDigit` | `currentK = 0 * 10 + 2` | `currentK = 2`<br>

<br>`currentString = "aaa"` | `countStack = []`<br>

<br>`stringStack = []` |
| **6** | `'['` | `ch == '['` | Push state: `"aaa"` goes to string stack, `2` goes to count stack. Reset local variables. | `currentK = 0`<br>

<br>`currentString = ""` | `countStack = [2]`<br>

<br>`stringStack = ["aaa"]` |
| **7** | `'b'` | `else` (char) | Append `'b'` to `currentString`. | `currentK = 0`<br>

<br>`currentString = "b"` | `countStack = [2]`<br>

<br>`stringStack = ["aaa"]` |
| **8** | `'c'` | `else` (char) | Append `'c'` to `currentString`. | `currentK = 0`<br>

<br>`currentString = "bc"` | `countStack = [2]`<br>

<br>`stringStack = ["aaa"]` |
| **9** | `']'` | `ch == ']'` | Pop `repeatTimes = 2`. <br>

<br>Pop `decodedBlock = "aaa"`. <br>

<br>Loop appends `"bc"` twice to `decodedBlock` (`"aaa" + "bc" + "bc"`). | `currentK = 0`<br>

<br>`currentString = "aaabcbc"` | `countStack = []`<br>

<br>`stringStack = []` |

---

### **Loop Termination**

The loop finishes because we have reached the end of the string array.

The final statement executes:

```java
return currentString.toString(); // Returns "aaabcbc"

```

The code evaluates the expression successfully without missing any characters or leaving stray frames on the stack.