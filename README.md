# Stock Service Review Lab

Small Spring Boot service used as a realistic code-review portability lab.

## Public API

- `GET /api/products` lists the catalog.
- `GET /api/products/{sku}` returns one product; unknown SKUs return `404`.
- `POST /api/products/reservations` reserves stock for one product.
- `POST /api/products/reservations/batch` reserves several lines as one operation.

### Batch reservation contract

A batch is atomic: either every requested line is reserved or stock remains unchanged. Duplicate SKUs are allowed and their quantities count together. A rejected batch returns `409 Conflict`; an accepted batch returns `200 OK`.

The application is exposed through the reverse proxy configuration in `infra/nginx.conf`.
