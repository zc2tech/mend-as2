# Quick Reference: REST API Response Rules DSL

## UI Location
1. Login to WebUI as your user (e.g., admin1)
2. Navigate to: **My REST API Config**
3. Click tab: **My REST API Response**

## Creating a Rule

### Basic Fields
- **HTTP Method**: GET, POST, PUT, DELETE, or * (any)
- **Path Pattern**: The path to match (e.g., `/a/b`, `/api/*`)
- **Match Type**: How to match the path
- **Status Code**: HTTP status (e.g., 200, 400, 500)
- **Content Type**: Response format (usually `application/json`)
- **Response Body**: The actual response content
- **Enabled**: Toggle to enable/disable rule

### Match Types Explained

| Match Type | Description | Example Pattern | Matches | Doesn't Match |
|------------|-------------|----------------|---------|---------------|
| **exact** | Path must match exactly | `/api/users` | `/api/users` | `/api/users/123`, `/api` |
| **prefix** | Path must start with pattern | `/api` | `/api`, `/api/users`, `/api/v1/test` | `/test`, `/apiv2` |
| **wildcard** | Use `*` for any chars, `?` for one char | `/api/*/detail` | `/api/users/detail`, `/api/orders/detail` | `/api/detail`, `/api/users/123/detail` |
| **regex** | Full regex support | `^/api/v[0-9]+/.*$` | `/api/v1/users`, `/api/v2/test` | `/api/users`, `/api/vX/test` |

### Variables in Response Body

Use these variables in your response body for dynamic values:

- `${path}` - The request path (e.g., `/a/b/c`)
- `${method}` - The HTTP method (e.g., `POST`)
- `${requestId}` - The unique request ID (UUID)
- **`${1}`, `${2}`, ...** - Numbered regex capture groups (when using regex match type)
- **`${groupName}`** - Named regex capture groups (e.g., `${customerId}`, `${orderId}`)

**Example with simple variables**:
```json
{
  "message": "You requested ${method} ${path}",
  "requestId": "${requestId}",
  "timestamp": "2026-07-05"
}
```

**Example with regex capture groups** (NEW!):
```
Pattern: ^/customers/(?<customerId>[^/]+)/orders/(?<orderId>[^/]+)$
Match Type: regex

Response Body:
{
  "customer": "${customerId}",
  "order": "${orderId}",
  "message": "Order ${orderId} for customer ${customerId}"
}
```

See `REGEX_CAPTURE_GROUPS_GUIDE.md` for detailed examples.

## Common Examples

### Example 1: Success Response
```
Method: POST
Path: /orders/create
Match Type: exact
Status: 200
Content-Type: application/json
Body:
{
  "success": true,
  "message": "Order created successfully",
  "orderId": "12345"
}
```

### Example 2: Error Response for Missing Sub-path
```
Method: POST
Path: /
Match Type: exact
Status: 500
Content-Type: application/json
Body:
{
  "error": "you must specify sub-path",
  "hint": "Try /orders, /users, /products"
}
```

### Example 3: Wildcard API Route
```
Method: GET
Path: /api/*
Match Type: wildcard
Status: 200
Content-Type: application/json
Body:
{
  "message": "API endpoint",
  "path": "${path}",
  "method": "${method}"
}
```

### Example 4: Version-specific API (Regex)
```
Method: *
Path: ^/api/v1/.*$
Match Type: regex
Status: 200
Content-Type: application/json
Body:
{
  "version": "1.0",
  "endpoint": "${path}"
}
```

### Example 5: Health Check
```
Method: GET
Path: /health
Match Type: exact
Status: 200
Content-Type: application/json
Body:
{
  "status": "healthy",
  "timestamp": "2026-07-05"
}
```

### Example 6: Catch-All 404
```
Method: *
Path: /*
Match Type: wildcard
Status: 404
Content-Type: application/json
Body:
{
  "error": "Not found",
  "path": "${path}",
  "hint": "Check your API documentation"
}
```
**Note**: Place this rule LAST (lowest priority) as a catch-all.

## Rule Priority

- Rules are evaluated **top to bottom** (priority 0 → 1 → 2 → ...)
- **First matching rule wins**
- Use ↑/↓ buttons to reorder rules
- Lower priority number = higher precedence

**Example Order**:
```
Priority 0: POST /orders/create  (exact)     ← Evaluated first
Priority 1: POST /orders/*       (wildcard)
Priority 2: POST /*              (wildcard)
Priority 3: * /*                 (catch-all) ← Evaluated last
```

## Testing Your Rules

### Using curl:
```bash
# Test POST /a/b
curl -X POST https://localhost:8443/as2/userapi/admin1/a/b \
  -H "Content-Type: application/json" \
  -k

# Test with authentication
curl -X POST https://localhost:8443/as2/userapi/admin1/a/b \
  -H "Content-Type: application/json" \
  -u username:password \
  -k
```

### Using Postman:
1. Method: POST
2. URL: `https://localhost:8443/as2/userapi/admin1/a/b`
3. Headers: `Content-Type: application/json`
4. Auth: Basic Auth (if configured)
5. Send request

## Tips & Best Practices

1. **Start Specific, End General**: Put specific rules (exact match) at the top, wildcards at the bottom
2. **Test As You Go**: Add one rule, test it, then add more
3. **Use Variables**: Make responses dynamic with `${path}`, `${method}`, `${requestId}`
4. **Disable, Don't Delete**: Temporarily disable rules instead of deleting them
5. **Document Complex Regex**: Add comments in a "message" field explaining complex patterns
6. **Catch-All Last**: Always put catch-all rules (`* /*`) at the bottom
7. **Status Codes Matter**: Use correct HTTP status codes (200 success, 400 client error, 500 server error)
8. **JSON Format**: Validate your JSON before saving (use a JSON validator)

## Troubleshooting

### Rule Not Matching?
- Check the priority order (is another rule matching first?)
- Verify the match type (exact vs prefix vs wildcard)
- Check if the rule is enabled
- Test the pattern: `/api/test` with prefix `/api` should match

### Wrong Response Returned?
- Check logs: each request logs which rule matched
- Ensure your rule is higher priority than competing rules
- Verify the path pattern exactly matches your request

### Variables Not Replaced?
- Make sure syntax is exact: `${path}` not `$path` or `{path}`
- Variables only work in response body, not in headers

## URL Format

Your API base URL:
```
https://[hostname]:[port]/as2/userapi/[username]/[your-sub-path]
```

Example:
```
https://localhost:8443/as2/userapi/admin1/orders/create
                                        ↑        ↑
                                     username  sub-path (matched by rules)
```

The rules match against the **sub-path** portion only (e.g., `/orders/create`).

## Need Help?

- Check the logs: `log/as2_server.log` shows which rule matched
- Review the implementation doc: `REST_API_RESPONSE_RULES_IMPLEMENTATION.md`
- Test incrementally: Start with simple exact matches, then move to wildcards
