# 1. Difference between PUT and POST, PATCH

While both `PUT` and `POST` are standard HTTP methods used to send data to a server, their underlying architectural intent, behaviors, and rules around data handling are entirely different.

The easiest way to differentiate them is by focusing on two core principles: **Idempotency** and **Resource Creation Control**.

---

## 1. Idempotency (The Biggest Difference)

An HTTP method is **idempotent** if making identical multiple requests has the same effect on the server state as making a single request.

* **`PUT` is Idempotent:** If you send the exact same `PUT` request 10 times in a row, the side effect on the server is identical to sending it once. You are explicitly telling the server, *"Make the resource look exactly like this payload."*
* **`POST` is Non-Idempotent:** If you send the exact same `POST` request 10 times, you will likely create 10 different records or trigger 10 separate actions on the server (e.g., placing 10 separate orders).

---

## 2. Resource Management & URI Control

* **`PUT` (Upsert / Replace):** The **client** decides the exact identifier (URI) of the resource. If the resource already exists at that URI, it is completely replaced/overwritten. If it does not exist, it is created.
* *Analogy:* You decide the exact slot number in a parking lot. If a car is already in slot #42, you replace it. If slot #42 is empty, you park there.
* *URI Example:* `PUT /api/users/42`


* **`POST` (Create / Append):** The **server** decides the identifier (URI). You send the data payload to a generic collection endpoint, and the server processes it, creates the record, and assigns it an auto-generated ID.
* *Analogy:* You hand your car to a valet. They decide exactly where to park it and hand you back a ticket stub (the generated ID).
* *URI Example:* `POST /api/users` (The server responds with the location of the new resource, like `/api/users/109`).



---

## 3. Side-by-Side Comparison

| Feature | `POST` | `PUT` |
| --- | --- | --- |
| **Primary Intent** | Create a new subordinate resource, or execute an action. | Create or completely replace a resource at a specific URI. |
| **Idempotent?** | **No** (Multiple identical requests create multiple records). | **Yes** (Multiple identical requests yield the exact same server state). |
| **URI Target** | Points to a **Collection** (e.g., `/users`). | Points to a **Specific Resource** (e.g., `/users/42`). |
| **ID Generation** | Handled by the **Server** (Auto-increment, UUIDs). | Handled/Known by the **Client** before sending. |
| **Caching** | Responses are generally **not cacheable** by default. | Responses are **not cacheable** (but it invalidates existing caches of that URI). |

---

## 4. Practical Example Logs

### Example of `POST` (Creating a user)

```http
POST /api/users HTTP/1.1
Host: example.com
Content-Type: application/json

{
    "name": "Alice",
    "email": "alice@example.com"
}

```

**Server Response:** `201 Created` with Header `Location: /api/users/583` (The server assigned ID 583).

### Example of `PUT` (Updating or setting that exact user)

```http
PUT /api/users/583 HTTP/1.1
Host: example.com
Content-Type: application/json

{
    "name": "Alice Smith",
    "email": "alice.smith@example.com"
}

```

**Server Response:** `200 OK` (The resource at ID 583 has been completely overwritten with the new names and emails). If you run this exact `PUT` script again, nothing changes on the server because the data matches perfectly.

----
# 2 . PUT VS POST VS PATCH

When designing or consuming RESTful APIs, **POST**, **PUT**, and **PATCH** are the three primary HTTP methods used to send data to a server. While they all modify server state, they have distinct semantics regarding **idempotency**, **scope of the update**, and **resource handling**.

Here is the architectural breakdown of how they compare.

---

## 1. Core Definitions

### POST (Create / Execute)

* **Intent:** Submits an entity to the specified collection. The server processes the entity and typically creates a *new* subordinate resource with a server-generated ID.
* **Payload:** Contains the full data of the new resource.
* **Target URI:** A collection endpoint (e.g., `/api/orders`).

### PUT (Replace / Upsert)

* **Intent:** Replaces the target resource completely with the request payload. If the resource does not exist at that specific URI, it can create it (Upsert).
* **Payload:** Must contain the **complete, modified resource representation**. Missing fields are overwritten with `null` or defaults.
* **Target URI:** A specific resource endpoint (e.g., `/api/orders/101`).

### PATCH (Partial Update)

* **Intent:** Applies a **partial modification** to an existing resource. It is used when you only want to change a few specific fields without touching the rest of the object.
* **Payload:** Contains only the fields being updated, or a set of instructions (like JSON Patch).
* **Target URI:** A specific resource endpoint (e.g., `/api/orders/101`).

---

## 2. Idempotency & Side Effects

An HTTP method is **idempotent** if executing it multiple times yields the exact same server state as executing it a single time.

* **POST is NOT Idempotent:** If you send a `POST` request three times, the server will blindly create three separate database rows (e.g., generating three different invoices or placing three identical orders).
* **PUT is Idempotent:** If you send a `PUT` request containing a complete resource payload ten times, the first request updates/creates it. The subsequent nine requests simply overwrite the resource with the exact same data, leaving the server state unchanged.
* **PATCH is generally Idempotent (but can be non-idempotent):** If your `PATCH` body specifies a strict assignment like `{"status": "shipped"}`, running it multiple times has no new side effects (idempotent). However, if your patch body specifies a relative operation like `{"accumulatedPoints": "+10"}`, running it multiple times changes the state repeatedly, making it non-idempotent.

---

## 3. Side-by-Side Comparison Matrix

| Feature | POST | PUT | PATCH |
| --- | --- | --- | --- |
| **Action** | Create / Append | Replace / Re-create | Modify partially |
| **Idempotent** | **No** | **Yes** | **Mainly Yes** (depends on logic) |
| **Target URI** | Collection (`/users`) | Specific (`/users/42`) | Specific (`/users/42`) |
| **Payload Requirement** | Complete new entity | Complete replacement entity | Only modified fields/instructions |
| **Handling of Omitted Fields** | N/A (creates new) | Overwrites with defaults/`null` | Leaves untouched |

---

## 4. Practical Scenario: Updating a User Profile

Imagine an existing user resource at `/api/users/7` with the following database state:

```json
{ "id": 7, "name": "Sarah", "role": "User", "status": "Active" }

```

### Scenario A: The PUT Request

You want to change Sarah's name to "Sarah Jenkins" using `PUT`. You send:

```http
PUT /api/users/7
{ "name": "Sarah Jenkins" }

```

* **Resulting Server State:** `{ "id": 7, "name": "Sarah Jenkins", "role": null, "status": null }`
* **Why?** Because `PUT` replaces the *entire* resource. Since you left out `role` and `status`, they were wiped out.

### Scenario B: The PATCH Request

You want to change Sarah's name to "Sarah Jenkins" using `PATCH`. You send:

```http
PATCH /api/users/7
{ "name": "Sarah Jenkins" }

```

* **Resulting Server State:** `{ "id": 7, "name": "Sarah Jenkins", "role": "User", "status": "Active" }`
* **Why?** Because `PATCH` only updates the fields explicitly provided, leaving the rest of the record intact.