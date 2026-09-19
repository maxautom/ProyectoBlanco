# Inventory reconciliation import

- POST /api/reconciliation/imports accepts sku,delta rows and requires X-Request-Id.
- The import is atomic: if any row is invalid, references an unknown SKU, or would make final stock negative, no stock is changed.
- Duplicate SKUs are valid and their deltas are aggregated before validation and application.
- Reusing the same X-Request-Id is idempotent and returns the original result without applying stock changes again.
- A rejected import returns HTTP 422. An accepted import returns HTTP 200.
- GET /api/reconciliation/imports/{requestId} returns the recorded result, or HTTP 404 when the request ID is unknown.
- Both endpoints are public and must be reachable through the normal reverse-proxy boundary.
- Each non-comment CSV row must contain exactly two columns: SKU and signed integer delta.
