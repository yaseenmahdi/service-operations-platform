# Design Decisions

## Reserve first, consume later

Parts are not removed from on-hand inventory when a technician merely plans to use them. A reservation reduces available quantity; completion converts reserved quantities into consumed stock. Cancellation releases reservations.

This distinction prevents the same part from being promised to multiple work orders while still reflecting physical stock accurately.

## Controlled work-order transitions

Lifecycle transitions are modeled explicitly rather than accepting arbitrary status updates. Invalid transitions return a conflict response and terminal work orders cannot be reopened accidentally.

## Store appointment instants in UTC

Local date/time input is interpreted in the service site's IANA time zone and converted to UTC for storage. The original zone is retained for display. This avoids ambiguous timestamp handling across regions and daylight-saving changes.

## Audit events instead of relying only on mutable state

A work order's current status answers "where is it now?" while the event stream answers "how did it get here?" Important actions are therefore recorded as separate events.

## DTOs at the API boundary

JPA entities are not serialized directly. Request/response DTOs keep persistence details out of the public contract and avoid accidental lazy-loading or bidirectional-relationship serialization problems.

## Authentication is out of scope for the demo

The repository focuses on backend workflow design. A production version would add OIDC/JWT authentication, role-based authorization, and tenant boundaries. Leaving authentication out is deliberate rather than implying the current API is internet-ready.
