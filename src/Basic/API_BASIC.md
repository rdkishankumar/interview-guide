Here are high-yield, practical scenario questions frequently asked in system design and API design interviews. They challenge your understanding of HTTP methods, security, and resource design patterns—similar to the login validation question.

---

## 1. Which HTTP Method for a Search Engine / Complex Filter?

* **The Scenario:** You are designing an advanced search page for an e-commerce platform. Users can filter products by a massive array of parameters: 50+ categories, price ranges, multiple tags, sorting criteria, and locations.

### The Approach & Decision:

While simple searches should use **`GET`** (with query parameters like `/products?search=shoes`), massive or complex filtering systems often hit architectural limitations.

* **Why `POST` is often chosen for complex search:** 1. **URL Length Limits:** Browsers and web servers (like Nginx or older proxies) enforce hard limits on URL lengths (often around 2,048 characters). A massive query payload can truncate the URL, crashing the request.
2. **Data Structure:** Passing complex nested structures or arrays via flat URL strings (`?tags=blue&tags=red&sizes=M...`) gets ugly and error-prone. Passing a clean JSON block in a `POST` body is much more maintainable.
* **The Semantic Trade-off:** By strict REST semantics, searching is a read-only operation and should be a `GET`. To balance both worlds, many architectures use a **`POST`** to a specific controller endpoint like `/api/v1/products/search`.

---

## 2. Which HTTP Method for "Heartbeat" or "Keep-Alive" Status Pings?

* **The Scenario:** A frontend application needs to ping a backend server every 30 seconds to indicate that the user is still active on the webpage, updating a `last_seen` timestamp in the database.

### The Approach & Decision:

The best choice here is **`POST`** or **`PATCH`**.

* **Why not `GET`?** Even though a heartbeat feels like a lightweight check, it directly mutates state on the server by modifying the user’s `last_seen_at` row in the database. Furthermore, browsers or intermediate Content Delivery Networks (CDNs) aggressively cache `GET` requests. If a proxy caches the `GET` ping, the subsequent 20 pings won't even reach your server, breaking your active-user tracking.
* **The Semantic Fit:** Because you are executing a non-idempotent action that alters data timestamps on every hit, a `POST` or a `PATCH` to `/api/users/me/heartbeat` guarantees the request hits the origin server every single time.

---

## 3. Designing a "File Export/Download" Endpoint (CSV/PDF Generation)

* **The Scenario:** A user hits a button to generate and download a custom analytics report. The generation process requires a heavy JSON payload outlining the specific dates, columns, and metrics they want to compile.

### The Approach & Decision:

This is a classic conflict between **`GET`** (fetching a file) and **`POST`** (sending the generation criteria).

* **The Production Solution (Asynchronous Processing):** Because compiling huge files takes time, doing this in a single synchronous block will cause gateway timeouts. The standard pattern uses a two-step process:
1. **`POST /api/reports`:** Pass the configuration JSON body. The server creates an entry in a database with a state of `PENDING`, passes the job to a background worker queue, and instantly returns a `202 Accepted` status with a report ID (e.g., `{"reportId": "rep_992"}`).
2. **`GET /api/reports/rep_992`:** The frontend polls this endpoint. Once the status changes to `COMPLETED`, this `GET` request serves the downloadable binary file.



---

## 4. Reset Password Feature: Token Generation vs. Password Update

* **The Scenario:** You need to design the endpoints for a forgot-password workflow. The user enters their email, receives a reset link, and then submits a new password.

### The Approach & Decision:

This requires breaking the feature into two distinct endpoints using different methods.

* **Step 1: Requesting the Reset Link**
* **Method:** `POST /api/auth/password-reset-requests`
* **Why:** You are submitting an email to *create* a new short-lived cryptographic token resource in your database. It also shields the email from URL log leaks.


* **Step 2: Submitting the New Password**
* **Method:** `POST` or `PUT` to `/api/auth/password-reset`
* **Why:** You are sending the highly sensitive raw new password along with the validation token. Just like the login endpoint, this must live in an encrypted `POST` body to prevent exposure in network routing logs.



---

## 5. Adding an Item to a Shopping Cart: `POST` vs `PUT` vs `PATCH`

* **The Scenario:** A user clicks "Add to Cart" on an e-commerce platform for an item they already have in their cart, increasing the quantity from 1 to 2.

### The Approach & Decision:

Depending on how your frontend handles the request state, you can justify different approaches:

* **The `POST` Approach (`POST /api/cart/items`):** The frontend simply fires an event saying, *"Add item X to the cart."* The server checks if it exists, updates the quantity internally, and returns the modified cart. This is non-idempotent; if the user double-clicks due to network lag, they might accidentally add 2 items.
* **The `PUT` Approach (`PUT /api/cart/items/item_abc`):** The frontend keeps track of the state locally and says, *"Set the total state of item_abc in the cart to exactly 2."* If this request is repeated due to a retry mechanism, the quantity stays exactly 2, making it robust and **idempotent**.

Which of these system scenarios would you like to drill down into further, or do you have a specific pattern you want to match against?