# Stock Service Review Lab

Small Spring Boot service used as a realistic code-review portability lab.

## Public API

- `GET /api/products` lists the catalog.
- `POST /api/products/reservations` reserves stock.

The application is exposed through the reverse proxy configuration in `infra/nginx.conf`.
