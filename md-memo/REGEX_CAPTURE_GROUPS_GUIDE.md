# Regex Capture Groups - Complete Guide

## Overview
The REST API Response Rules support **regex capture groups**, allowing you to extract parts of the request path and use them in your response body. This guide covers all match types and how to use them effectively.

## Table of Contents
1. [Match Types Overview](#match-types-overview)
2. [Path Pattern Examples](#path-pattern-examples)
3. [Response Body Variables](#response-body-variables)
4. [Regex Capture Groups](#regex-capture-groups)
5. [Real-World Examples](#real-world-examples)
6. [Common Patterns](#common-patterns)
7. [Testing Your Rules](#testing-your-rules)

---

## Match Types Overview

### 1. **Exact Match** (`exact`)
Path must match **exactly** - character for character.

**When to use:** Fixed endpoints with no variations.

**Examples:**
```
Pattern: /api/health
Matches: /api/health ✓
Doesn't match: /api/health/check ✗, /api/health/ ✗
```

**Response Body:**
```json
{
  "status": "healthy",
  "path": "${path}",
  "method": "${method}"
}
```

---

### 2. **Prefix Match** (`prefix`)
Path must **start with** the pattern.

**When to use:** Match all paths under a certain prefix.

**Examples:**
```
Pattern: /api/v1
Matches: /api/v1 ✓, /api/v1/users ✓, /api/v1/orders/123 ✓
Doesn't match: /api/v2/users ✗, /api ✗
```

**Response Body:**
```json
{
  "message": "API v1 endpoint",
  "fullPath": "${path}"
}
```

---

### 3. **Wildcard Match** (`wildcard`)
Use `*` for any sequence of characters, `?` for single character.

**When to use:** Simple pattern matching without regex complexity.

**Pattern Syntax:**
- `*` = matches zero or more characters
- `?` = matches exactly one character

**Examples:**
```
Pattern: /api/*/detail
Matches: /api/users/detail ✓, /api/orders/detail ✓
Doesn't match: /api/users/123/detail ✗ (too many segments)

Pattern: /file-????.txt
Matches: /file-2024.txt ✓, /file-abcd.txt ✓
Doesn't match: /file-12.txt ✗ (only 2 chars), /file-12345.txt ✗ (5 chars)
```

**Response Body:**
```json
{
  "message": "Wildcard matched",
  "path": "${path}"
}
```

**Note:** Wildcards don't support capture - use regex for that.

---

### 4. **Regex Match** (`regex`)
Full regular expression support with **capture groups**.

**When to use:** 
- Extract values from path (customer ID, order number, etc.)
- Complex validation (numeric IDs, date formats, etc.)
- Multiple variations in one pattern

**Pattern Syntax (Java regex):**
- `^` = start of string
- `$` = end of string
- `[0-9]+` = one or more digits
- `[^/]+` = one or more non-slash characters
- `(?<name>...)` = named capture group
- `(...)` = numbered capture group

**Examples:**

#### Simple Regex (no captures)
```
Pattern: ^/api/v[0-9]+/users$
Matches: /api/v1/users ✓, /api/v2/users ✓
Doesn't match: /api/vX/users ✗, /api/v1/users/123 ✗
```

#### Numbered Capture Groups
```
Pattern: ^/customers/([^/]+)/orders/([^/]+)$
Matches: /customers/C123/orders/O456
         Group ${1} = "C123"
         Group ${2} = "O456"
```

**Response Body:**
```json
{
  "customer": "${1}",
  "order": "${2}"
}
```

#### Named Capture Groups (RECOMMENDED)
```
Pattern: ^/customers/(?<customerId>[^/]+)/orders/(?<orderId>[^/]+)$
Matches: /customers/C123/orders/O456
         ${customerId} = "C123"
         ${orderId} = "O456"
```

**Response Body:**
```json
{
  "customerId": "${customerId}",
  "orderId": "${orderId}",
  "message": "Order ${orderId} belongs to customer ${customerId}"
}
```

---

## Path Pattern Examples

**Pattern**: `^/a/([^/]+)/b/([^/]+)/c$`  
**Match Type**: regex

This pattern captures two groups:
- Group 1: Value between `/a/` and `/b/`
- Group 2: Value between `/b/` and `/c`

**Request**: `POST /a/customer123/b/order456/c`

**Response Body**:
```json
{
  "customer": "${1}",
  "order": "${2}",
  "message": "Processing order ${2} for customer ${1}"
}
```

**Actual Response**:
```json
{
  "customer": "customer123",
  "order": "order456",
  "message": "Processing order order456 for customer customer123"
}
```

### Named Capture Groups (Recommended)

**Pattern**: `^/a/(?<customerId>[^/]+)/b/(?<orderId>[^/]+)/c$`  
**Match Type**: regex

This pattern uses **named groups** with the syntax `(?<name>...)`

**Request**: `POST /a/ABC123/b/ORD999/c`

**Response Body**:
```json
{
  "customerId": "${customerId}",
  "orderId": "${orderId}",
  "fullPath": "${path}",
  "method": "${method}"
}
```

**Actual Response**:
```json
{
  "customerId": "ABC123",
  "orderId": "ORD999",
  "fullPath": "/a/ABC123/b/ORD999/c",
  "method": "POST"
}
```

## Supported Variables

| Variable | Description | Example Value |
|----------|-------------|---------------|
| `${path}` | Full request path | `/a/ABC123/b/ORD999/c` |
| `${method}` | HTTP method | `POST` |
| `${requestId}` | Unique request ID | `uuid-string` |
| `${1}`, `${2}`, ... | Numbered capture groups | `ABC123`, `ORD999` |
| `${groupName}` | Named capture groups | Same as numbered |

## Real-World Examples

### Example 1: Customer Orders API

**Rule Configuration**:
```
Method: GET
Path: ^/customers/(?<customerId>[^/]+)/orders/(?<orderId>[^/]+)$
Match Type: regex
Status: 200
Content-Type: application/json
```

**Response Body**:
```json
{
  "customerId": "${customerId}",
  "orderId": "${orderId}",
  "status": "found",
  "message": "Order ${orderId} belongs to customer ${customerId}"
}
```

**Test**:
```bash
curl https://localhost:8443/as2/userapi/admin1/customers/C123/orders/O456
```

**Result**:
```json
{
  "customerId": "C123",
  "orderId": "O456",
  "status": "found",
  "message": "Order O456 belongs to customer C123"
}
```

### Example 2: Versioned API Endpoints

**Rule Configuration**:
```
Method: *
Path: ^/api/v(?<version>[0-9]+)/(?<resource>.*)$
Match Type: regex
Status: 200
Content-Type: application/json
```

**Response Body**:
```json
{
  "apiVersion": "v${version}",
  "resource": "${resource}",
  "supported": true
}
```

**Test**:
```bash
curl https://localhost:8443/as2/userapi/admin1/api/v2/users
```

**Result**:
```json
{
  "apiVersion": "v2",
  "resource": "users",
  "supported": true
}
```

### Example 3: ID Validation with Response

**Rule Configuration**:
```
Method: GET
Path: ^/items/(?<itemId>[A-Z0-9]{5,10})$
Match Type: regex
Status: 200
Content-Type: application/json
```

**Response Body**:
```json
{
  "itemId": "${itemId}",
  "format": "valid",
  "message": "Item ID ${itemId} matches expected format"
}
```

This will match: `/items/ABC123`, `/items/XY789`  
Won't match: `/items/abc` (lowercase), `/items/1234` (too short)

### Example 4: Multiple Path Segments

**Rule Configuration**:
```
Method: POST
Path: ^/(?<category>[^/]+)/(?<subcategory>[^/]+)/(?<action>[^/]+)$
Match Type: regex
Status: 200
Content-Type: application/json
```

**Response Body**:
```json
{
  "category": "${category}",
  "subcategory": "${subcategory}",
  "action": "${action}",
  "message": "Executing ${action} in ${category}/${subcategory}"
}
```

**Test**:
```bash
curl -X POST https://localhost:8443/as2/userapi/admin1/products/electronics/update
```

**Result**:
```json
{
  "category": "products",
  "subcategory": "electronics",
  "action": "update",
  "message": "Executing update in products/electronics"
}
```

### Example 5: Your Original Use Case

**Rule Configuration**:
```
Method: POST
Path: ^/a/(?<val1>[^/]+)/b/(?<val2>[^/]+)/c$
Match Type: regex
Status: 200
Content-Type: application/json
```

**Response Body**:
```json
{
  "value1": "${val1}",
  "value2": "${val2}",
  "message": "Received values: ${val1} and ${val2}"
}
```

**Test**:
```bash
curl -X POST https://localhost:8443/as2/userapi/admin1/a/hello/b/world/c
```

**Result**:
```json
{
  "value1": "hello",
  "value2": "world",
  "message": "Received values: hello and world"
}
```

## Regex Pattern Cheat Sheet

| Pattern | Matches | Example |
|---------|---------|---------|
| `[^/]+` | One or more non-slash chars | `abc`, `123`, `hello-world` |
| `[0-9]+` | One or more digits | `123`, `456` |
| `[A-Z]+` | One or more uppercase letters | `ABC`, `XYZ` |
| `[a-z]+` | One or more lowercase letters | `abc`, `xyz` |
| `[A-Za-z0-9]+` | Alphanumeric only | `abc123`, `XYZ789` |
| `.*` | Any characters | Anything |
| `[^/]{5,10}` | 5 to 10 non-slash chars | `abcde`, `1234567890` |

## Named Groups Syntax

Java regex uses `(?<name>pattern)` for named groups:

```
(?<customerId>[^/]+)     ← Capture as "customerId"
(?<orderId>[0-9]+)       ← Capture as "orderId"
(?<action>[a-z]+)        ← Capture as "action"
```

## Tips & Best Practices

1. **Use Named Groups**: Much more readable than `${1}`, `${2}`
2. **Be Specific**: Use `[^/]+` instead of `.*` to avoid greedy matching
3. **Validate Format**: Use patterns like `[A-Z0-9]{5,10}` to enforce ID formats
4. **Test Your Regex**: Use https://regex101.com/ (select Java flavor) to test patterns
5. **Escape Special Chars**: Remember to escape `.` as `\.` in patterns
6. **Anchors Matter**: Use `^` and `$` to match full path only

## Common Mistakes

### ❌ Wrong: Forgot anchors
```
Pattern: /api/([^/]+)/users
Matches: /api/v1/users ✓
Also matches: /something/api/v1/users/extra ✓ (probably unintended)
```

### ✓ Right: Use anchors
```
Pattern: ^/api/([^/]+)/users$
Matches: /api/v1/users ✓
Rejects: /something/api/v1/users/extra ✗
```

### ❌ Wrong: Greedy wildcard
```
Pattern: ^/api/(.*)/users$
Request: /api/v1/test/users
Captures: "v1/test" (includes the slash!)
```

### ✓ Right: Non-slash pattern
```
Pattern: ^/api/([^/]+)/users$
Request: /api/v1/test/users
No match (as expected - path doesn't match structure)
```

## Testing in UI

1. Create a rule with regex pattern and named groups
2. Save the rule
3. Test with curl or Postman
4. Check the response to see captured values
5. Adjust pattern if needed

## Technical Details

- Uses Java `Pattern` and `Matcher` classes
- Supports both numbered groups (`${1}`) and named groups (`${name}`)
- Named groups use Java syntax: `(?<name>...)`
- All standard Java regex features are supported
- Invalid regex patterns will not match (fail gracefully)

## Migration from Existing Rules

If you have existing rules without capture groups, they continue to work unchanged. The new capture group feature is **opt-in** and backward compatible.

**Before** (still works):
```
Pattern: ^/api/v1/users$
Response: {"message": "Users endpoint"}
```

**After** (now possible):
```
Pattern: ^/api/v(?<version>[0-9]+)/users$
Response: {"message": "Users endpoint v${version}"}
```
