-- Check if there are any TLS certificates in the system keystore
SELECT 
    id,
    user_id,
    CASE 
        WHEN user_id = 0 THEN 'System-wide (Sys TLS)'
        ELSE 'User-specific'
    END as keystore_type,
    CASE 
        WHEN purpose = 1 THEN 'Sign/Encrypt'
        WHEN purpose = 2 THEN 'TLS/SSL'
        ELSE 'Unknown'
    END as purpose_name,
    storagetype,
    CASE 
        WHEN storagedata IS NULL OR length(storagedata) = 0 THEN 'EMPTY'
        ELSE 'Has data (' || length(storagedata) || ' bytes)'
    END as storage_status,
    lastchanged
FROM keydata
WHERE purpose = 2
ORDER BY user_id, id;
