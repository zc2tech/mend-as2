-- Grant CERT_TLS_READ permission to USER role
-- This allows normal users to read system-wide TLS certificates for HTTPS connections
-- They still cannot modify system-wide TLS certificates (CERT_TLS_WRITE remains admin-only)

INSERT INTO webui_role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM webui_roles r
CROSS JOIN webui_permissions p
WHERE r.name = 'USER'
  AND p.name = 'CERT_TLS_READ'
  AND NOT EXISTS (
    SELECT 1 FROM webui_role_permissions rp
    WHERE rp.role_id = r.id AND rp.permission_id = p.id
  );
