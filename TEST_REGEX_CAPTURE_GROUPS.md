# Testing Regex Capture Groups

## Sample Rules Installed

Your database now has 3 sample rules for user `admin1` (user_id=3):

### Rule 1: Exact Match (Priority 1)
```
Method: POST
Path: /a/b
Match Type: exact
Status: 200
Response: {"message": "good a/b"}
```

### Rule 2: Exact Match with Error (Priority 2)
```
Method: POST
Path: /
Match Type: exact
Status: 500
Response: {"error": "you must specify sub-path"}
```

### Rule 3: Regex with Named Capture Groups (Priority 3) ⭐ NEW!
```
Method: GET
Path: ^/customers/(?<customerId>[^/]+)/orders/(?<orderId>[^/]+)$
Match Type: regex
Status: 200
Response: {"customerId": "${customerId}", "orderId": "${orderId}", "message": "Order ${orderId} belongs to customer ${customerId}"}
```

## Test Commands

### Test 1: Basic Exact Match (Rule 1)
```bash
curl -X POST https://localhost:8443/as2/userapi/admin1/a/b -k
```

**Expected Response** (Status 200):
```json
{"message": "good a/b"}
```

---

### Test 2: Error Response (Rule 2)
```bash
curl -X POST https://localhost:8443/as2/userapi/admin1/ -k
```

**Expected Response** (Status 500):
```json
{"error": "you must specify sub-path"}
```

---

### Test 3: Regex Capture Groups (Rule 3) ⭐
```bash
curl -X GET https://localhost:8443/as2/userapi/admin1/customers/C12345/orders/O98765 -k
```

**Expected Response** (Status 200):
```json
{
  "customerId": "C12345",
  "orderId": "O98765",
  "message": "Order O98765 belongs to customer C12345"
}
```

---

### Test 4: Different Customer/Order IDs
```bash
curl -X GET https://localhost:8443/as2/userapi/admin1/customers/ACME/orders/ORD-2024-001 -k
```

**Expected Response** (Status 200):
```json
{
  "customerId": "ACME",
  "orderId": "ORD-2024-001",
  "message": "Order ORD-2024-001 belongs to customer ACME"
}
```

---

### Test 5: No Match - Returns Default Response
```bash
curl -X GET https://localhost:8443/as2/userapi/admin1/random/path -k
```

**Expected Response** (Status 200, default):
```json
{
  "requestId": "...",
  "timestamp": "...",
  "status": "success",
  "method": "GET",
  "path": "/random/path"
}
```

---

### Test 6: Partial Path - Won't Match Rule 3
```bash
curl -X GET https://localhost:8443/as2/userapi/admin1/customers/C123 -k
```

**Expected Response** (Status 200, default):
```json
{
  "requestId": "...",
  "timestamp": "...",
  "status": "success",
  "method": "GET",
  "path": "/customers/C123"
}
```

**Why?** Rule 3 requires the pattern `/customers/{id}/orders/{id}` - the path `/customers/C123` doesn't match.

---

## Additional Test Scenarios

### Scenario A: Create Your Own Number-Only Rule

**Create this rule in the UI**:
```
Method: GET
Path: ^/items/(?<itemId>[0-9]+)$
Match Type: regex
Status: 200
Content-Type: application/json
Response Body:
{
  "itemId": "${itemId}",
  "type": "numeric",
  "message": "Found item ${itemId}"
}
```

**Test**:
```bash
# Should match
curl -X GET https://localhost:8443/as2/userapi/admin1/items/12345 -k

# Should NOT match (letters)
curl -X GET https://localhost:8443/as2/userapi/admin1/items/ABC123 -k
```

---

### Scenario B: Versioned API Endpoint

**Create this rule**:
```
Method: *
Path: ^/api/v(?<version>[0-9]+)/(?<resource>.*)$
Match Type: regex
Status: 200
Response Body:
{
  "apiVersion": "v${version}",
  "resource": "${resource}",
  "method": "${method}",
  "supported": true
}
```

**Test**:
```bash
curl -X GET https://localhost:8443/as2/userapi/admin1/api/v2/users -k
curl -X POST https://localhost:8443/as2/userapi/admin1/api/v1/orders -k
```

---

### Scenario C: Multiple Segments

**Create this rule**:
```
Method: POST
Path: ^/(?<category>[^/]+)/(?<subcategory>[^/]+)/(?<action>[^/]+)$
Match Type: regex
Status: 200
Response Body:
{
  "category": "${category}",
  "subcategory": "${subcategory}",
  "action": "${action}",
  "fullPath": "${path}"
}
```

**Test**:
```bash
curl -X POST https://localhost:8443/as2/userapi/admin1/products/electronics/update -k
```

**Expected**:
```json
{
  "category": "products",
  "subcategory": "electronics",
  "action": "update",
  "fullPath": "/products/electronics/update"
}
```

---

## Debugging Tips

### Check Server Logs

After each request, check the logs to see which rule matched:

```bash
tail -f /Users/I572958/SAPDevelop/github/mend-as2/log/as2_server.log
```

Look for lines like:
```
API request received: requestId=..., method=GET, path=/customers/C123/orders/O456, rule=3
```

The `rule=3` tells you which rule ID matched.

---

### Verify Rule in Database

```bash
mysql -h localhost -P 3306 -u as2user -pas2password as2_db_config \
  -e "SELECT id, priority, enabled, http_method, path_pattern, path_match_type 
      FROM user_api_response_rules WHERE user_id=3 ORDER BY priority;"
```

---

### Test Regex Pattern Online

Before adding a rule, test your regex pattern at:
- https://regex101.com/ (select "Java 8" flavor)

Example pattern to test:
```
Pattern: ^/customers/(?<customerId>[^/]+)/orders/(?<orderId>[^/]+)$
Test string: /customers/C12345/orders/O98765
```

---

## Common Issues & Solutions

### Issue: Rule not matching

**Solution**:
1. Check if rule is enabled
2. Verify priority order (is another rule matching first?)
3. Test regex pattern at regex101.com
4. Check HTTP method matches
5. Ensure anchors `^` and `$` are correct

---

### Issue: Variable not replaced

**Solution**:
1. Check syntax: `${name}` not `$name`
2. For capture groups, ensure match type is "regex"
3. Named groups must use syntax: `(?<name>...)`
4. Check logs to verify rule matched

---

### Issue: Wrong values in response

**Solution**:
1. Test regex pattern - are groups capturing correctly?
2. Use `[^/]+` instead of `.*` for single segments
3. Check group names match variable names exactly

---

## Summary

✅ **Basic exact/prefix/wildcard matching** - works as before  
✅ **Regex matching** - now with capture groups!  
✅ **Numbered groups** - `${1}`, `${2}`, etc.  
✅ **Named groups** - `${customerId}`, `${orderId}`, etc.  
✅ **All standard variables** - `${path}`, `${method}`, `${requestId}`

You can now extract and use path segments dynamically in your responses!
