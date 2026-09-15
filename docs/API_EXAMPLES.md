# API Walkthrough

The easiest way to explore the API is through Swagger UI at `/swagger-ui.html`. The default application profile seeds a customer, service site, asset, technicians, inventory items, and one open work order.

## 1. Inspect the demo dashboard

```bash
curl http://localhost:8080/api/dashboard
```

## 2. List customers and service sites

```bash
curl http://localhost:8080/api/customers
curl http://localhost:8080/api/customers/<customer-id>/sites
```

## 3. List work orders

```bash
curl http://localhost:8080/api/work-orders
curl 'http://localhost:8080/api/work-orders?status=OPEN'
```

## 4. Assign a technician

```bash
curl -X POST http://localhost:8080/api/work-orders/<work-order-id>/assignment \
  -H 'Content-Type: application/json' \
  -d '{"technicianId":"<technician-id>"}'
```

## 5. Move into active work

```bash
curl -X POST http://localhost:8080/api/work-orders/<work-order-id>/status \
  -H 'Content-Type: application/json' \
  -d '{"status":"IN_PROGRESS"}'
```

## 6. Reserve a part

```bash
curl -X POST http://localhost:8080/api/work-orders/<work-order-id>/parts \
  -H 'Content-Type: application/json' \
  -d '{"inventoryItemId":"<inventory-item-id>","quantity":1}'
```

## 7. Schedule an appointment

If the request omits `timeZone`, the site time zone is used automatically.

```bash
curl -X POST http://localhost:8080/api/work-orders/<work-order-id>/appointments \
  -H 'Content-Type: application/json' \
  -d '{"technicianId":"<technician-id>","startLocal":"2026-09-20T09:00:00","endLocal":"2026-09-20T11:00:00"}'
```

## 8. Complete the work order

Completion consumes any still-reserved parts and marks scheduled appointments completed.

```bash
curl -X POST http://localhost:8080/api/work-orders/<work-order-id>/completion \
  -H 'Content-Type: application/json' \
  -d '{"resolutionNotes":"Completed service and verified normal operation."}'
```

## 9. Localized status labels

Send an `Accept-Language` header to retrieve human-readable status labels.

```bash
curl -H 'Accept-Language: fr' http://localhost:8080/api/reference/work-order-statuses
```
