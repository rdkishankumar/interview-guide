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

---
# How to Create an Idempotent POST API?
By standard definition, HTTP `POST` is **non-idempotent**. If a client sends a `POST /api/orders` request three times due to a shaky internet connection, the server will blindly process it three times, resulting in duplicate charges or ghost orders.

However, in modern distributed systems, making a `POST` API **idempotent** is critical for financial transactions, ordering systems, and billing pipelines. The standard pattern to achieve this is the **Idempotency Key Pattern** (frequently called the Stripe Pattern).

Here is exactly how to design and build an idempotent `POST` API.

---

## 1. The Core Architectural Pattern (Idempotency Key)

The concept relies on a unique identifier generated by the client called an **Idempotency Key** (usually a UUIDv4 or UUIDv7). The client sends this unique key inside the HTTP headers of the `POST` request.

### The Step-by-Step Workflow:

1. **Client Action:** The client generates a unique token (e.g., `Idempotency-Key: 9b1deb4d-3b7d-4bad-9bdd-2b0d7b3dcb6d`) right before making the `POST` request.
2. **Server Check:** The server intercepts the request and checks a fast cache/database (like Redis) to see if this key has been processed before.
3. **Scenario A (First Time):** If the key is *not* found, the server saves the key with a status of `STARTED` or `IN_PROGRESS` and executes the business logic. Once finished, it saves the final response payload in Redis alongside the key and returns it to the client.
4. **Scenario B (Duplicate Request):** If the key *is* found in Redis with a status of `SUCCESS`, the server bypasses the business logic completely. It immediately returns the **cached response** back to the client.
5. **Scenario C (Concurrent In-Flight Request):** If the key is found but has a status of `IN_PROGRESS`, the server returns an HTTP status code `409 Conflict` or tells the client to retry momentarily, preventing race conditions.

---

## 2. API Design Specifications

An idempotent POST API should look like this under the hood:

### The Request Headers

```http
POST /api/v1/payments HTTP/1.1
Host: api.yourdomain.com
Authorization: Bearer jwt_token_here
Idempotency-Key: e82bdf12-70b1-460d-8302-39070f7cf424
Content-Type: application/json

{
  "amount": 150.00,
  "currency": "USD",
  "destination_account": "acc_88492"
}

```

### The Server Response Headers

When returning a cached result, it is best practice to notify the client via custom headers that the response was fetched from an idempotent cache rather than re-evaluated.

```http
HTTP/1.1 200 OK
Content-Type: application/json
Original-Request-Timestamp: 2026-06-24T10:00:00Z
Is-Idempotent-Response: true

{
  "payment_id": "pay_993021",
  "status": "COMPLETED"
}

```

---

## 3. Server-Side Pseudo-Code Implementation

This pattern is typically implemented as a global Middleware, Filter, or Interceptor layer so individual business controllers don't have to duplicate the boilerplate logic.

```java
public Response handlePostRequest(Request request) {
    String idempotencyKey = request.getHeader("Idempotency-Key");
    
    // 1. If no key is provided, bypass idempotency or reject request based on API policy
    if (idempotencyKey == null) {
        return processBusinessLogic(request); 
    }
    
    // 2. Atomic check-and-set operation in Redis (using SETNX or a distributed lock)
    CacheRecord record = cacheStore.get(idempotencyKey);
    
    if (record != null) {
        if ("IN_PROGRESS".equals(record.getStatus())) {
            return Response.status(409).entity("Concurrent request in progress. Retry shortly.").build();
        }
        // If SUCCESS, return the exactly cached response
        return record.getCachedResponse();
    }
    
    // 3. Mark key as processing to lock out concurrent double-clicks
    cacheStore.put(idempotencyKey, new CacheRecord("IN_PROGRESS"), TTL_24_HOURS);
    
    try {
        // Execute the database mutations, payment processing, etc.
        Response response = processBusinessLogic(request);
        
        // 4. Update cache with final response payload
        cacheStore.put(idempotencyKey, new CacheRecord("SUCCESS", response), TTL_24_HOURS);
        return response;
        
    } catch (Exception e) {
        // 5. If it fails catastrophically, evict the key so the client can try again
        cacheStore.remove(idempotencyKey);
        return Response.status(500).entity("Processing failed. Please retry.").build();
    }
}

```

---

## 4. Crucial Production Considerations

* **Time-To-Live (TTL) Selection:** Idempotency cache records shouldn't live forever. Typically, set a TTL between **24 to 72 hours**. This provides ample time for network retries while keeping Redis memory footprints low.
* **Payload Hash Validation:** What happens if a malicious client uses the same `Idempotency-Key` but changes the request body fields (e.g., changing amount from $150 to $15,000)?
* *Fix:* The server should hash the incoming request body and store that hash alongside the idempotency key. If the key matches but the payload hash differs, reject the request with a `400 Bad Request` because the request parameters are compromised.


* **Database Fallback:** If your caching layer (Redis) goes down, make sure your database layer acts as a safety net. You can create a dedicated `idempotency_keys` database table with a **Unique Constraint** on the key column. If Redis fails, a database `UniqueIndexViolationException` will safely block a duplicate row creation.