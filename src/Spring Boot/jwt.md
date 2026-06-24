## 1. What is a JWT (JSON Web Token)?

A **JSON Web Token (JWT)** is an open standard ([RFC 7519](https://datatracker.ietf.org/doc/html/rfc7519)) that defines a compact, self-contained way for securely transmitting information between parties as a JSON object.

The core feature of a JWT is that it is **stateless**. The token itself contains all the necessary information about a user or transaction, meaning the receiving server does not need to query a central database to validate who the sender is. Instead, it relies entirely on a cryptographic signature to verify authenticity.

---

## 2. The Anatomy and Structure of a JWT

A JWT is composed of three distinct parts separated by dots (`.`):


$$\text{Header}.\text{Payload}.\text{Signature}$$

When rendered, it appears as a long, continuous string of Base64URL-encoded characters.

### A. The Header

The header typically consists of two parts: the type of the token (JWT) and the signing algorithm being used, such as HMAC SHA256 (`HS256`) or RSA (`RS256`).

```json
{
  "alg": "HS256",
  "typ": "JWT"
}

```

### B. The Payload

The payload contains the **claims**. Claims are statements about an entity (typically, the authenticated user) and additional metadata. There are three types of claims:

* **Registered claims:** Pre-defined, recommended claims (e.g., `iss` for Issuer, `exp` for Expiration time, `sub` for Subject).
* **Public claims:** Custom claims defined by developers but registered to avoid collisions.
* **Private claims:** Custom claims created to share information between specific applications (e.g., user roles).

```json
{
  "sub": "1234567890",
  "name": "John Doe",
  "role": "admin",
  "exp": 1782294000
}

```

### C. The Signature

To create the signature, the Base64URL-encoded header and payload are concatenated with a dot, and then signed using a secret key (symmetric) or a private/public key pair (asymmetric) via the algorithm specified in the header.

The signature ensures that the payload hasn't been tampered with along the way. If even a single character in the header or payload changes, the signature becomes invalid.

```text
HMACSHA256(
  base64UrlEncode(header) + "." +
  base64UrlEncode(payload),
  your-256-bit-secret
)

```

---

## 3. How JWT Authentication Works (The Flow)

JWTs are primarily used in a stateless authentication model, removing the need for server-side lookup tables.

1. **User Login:** The client sends an HTTP `POST` request containing their username and password to the authentication server.
2. **Token Generation:** The server validates the credentials against the database. If valid, the server creates a JWT (encoding user data and roles in the payload) and signs it using a private key or secret.
3. **Token Return:** The server sends the JWT back to the client application.
4. **Client Storage:** The client stores the JWT (usually in browser memory, `localStorage`, or an HttpOnly cookie).
5. **Subsequent Requests:** For every API request to protected resources, the client attaches the JWT inside the HTTP **Authorization** header using the `Bearer` schema:
```http
Authorization: Bearer <your_jwt_token>

```


6. **Server Validation:** The resource server extracts the token from the header, decodes it, verifies its cryptographic signature, checks the expiration time, and extracts the user’s identity/roles. No database hit is required to authenticate the request.

---

## 4. JWTs vs. Traditional Session-Based Authentication

Choosing between JWT and stateful session tracking drastically shapes your application's architecture.

| Feature | Traditional Session Authentication | JWT (Stateless Token) Authentication |
| --- | --- | --- |
| **State Storage** | Server-side (Memory, Redis, DB). | Client-side (Stored within the token itself). |
| **Scalability** | Harder. Requires sticky sessions, a centralized session store, or database synchronization across servers. | Extremely Easy. Any server with the signing key can validate the token independently. |
| **Revocation** | Instantaneous. Deleting the session ID from the database immediately logs the user out. | Difficult. Tokens are valid until they expire naturally, unless blacklisted. |
| **Payload Size** | Tiny (Just a simple session string ID). | Large (Contains encoded JSON data, expanding overhead on every HTTP call). |

### Primary Use Cases for JWTs:

* **Microservices and Distributed APIs:** Perfect for architectures where multiple decoupled backend systems need to authenticate a incoming request without flooding a single session database with validation queries.
* **Single-Sign On (SSO):** Allows a user to log in once at an Identity Provider (IdP) and securely pass identity claims to multiple distinct domain applications.

---

## 5. Security Concerns Developers Must Aware Of

While convenient, implementing JWTs incorrectly introduces severe vulnerabilities:

* **The `alg: "none"` Vulnerability:** Historically, some JWT libraries allowed attackers to change the header to `{"alg": "none"}` and strip the signature entirely. If the backend didn't explicitly forbid un-signed tokens, it would accept spoofed payloads blindly.
* *Mitigation:* Always use updated JWT libraries and explicitly reject tokens using the `none` algorithm during verification.


* **Token Invalidation/Revocation Problem:** Because JWTs are self-contained, you cannot easily force a logout or revoke a token if a user's account is compromised or deleted midway.
* *Mitigation:* Keep access token lifetimes extremely short (e.g., 15 minutes) and pair them with a database-tracked **Refresh Token** (stored in an HttpOnly cookie) used to rotate expired access tokens. Alternatively, maintain a high-performance Redis cache of blacklisted tokens.


* **Data Leakage (Lack of Encryption):** Base64URL encoding is **not encryption**. Anyone who intercepts a JWT can easily decode it and read your payload fields.
* *Mitigation:* Never put sensitive info (passwords, social security numbers, API secrets) inside a standard JWT payload. Use JWE (JSON Web Encryption) if the data *must* be encrypted.


* **Storage Vulnerabilities (XSS vs. CSRF):** * If you store tokens in `localStorage`, the token is vulnerable to Cross-Site Scripting (**XSS**) scripts stealing it.
* If you store tokens in a standard cookie, it is vulnerable to Cross-Site Request Forgery (**CSRF**).
* *Mitigation:* The safest production strategy for web clients is storing the JWT in an **`HttpOnly` and `Secure` cookie with `SameSite=Strict**`, rendering the token inaccessible to malicious JavaScript.