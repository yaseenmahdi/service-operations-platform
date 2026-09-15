package com.yaseenmahdi.serviceops.api.dto;

import com.yaseenmahdi.serviceops.domain.*;
import java.time.*;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public final class Responses {
    private Responses() {}

    public record CustomerView(UUID id, String name, String email, String phone, boolean active) {}
    public record SiteView(UUID id, UUID customerId, String name, String addressLine1, String city, String state, String postalCode, String timeZone) {}
    public record AssetView(UUID id, UUID siteId, String name, String assetType, String serialNumber, AssetStatus status, LocalDate installedOn) {}
    public record TechnicianView(UUID id, String fullName, String email, boolean active, Set<String> skills) {}
    public record InventoryView(UUID id, String sku, String name, int quantityOnHand, int quantityReserved, int availableQuantity, int reorderPoint, boolean belowReorderPoint) {}
    public record PartView(UUID id, UUID inventoryItemId, String sku, String name, int quantity, PartReservationStatus status) {}
    public record AppointmentView(UUID id, UUID technicianId, String technicianName, Instant startAtUtc, Instant endAtUtc, String timeZone, String localStart, String localEnd, AppointmentStatus status) {}
    public record EventView(UUID id, String eventType, String message, Instant occurredAt) {}
    public record WorkOrderView(UUID id, UUID siteId, String siteName, UUID assetId, String assetName, UUID technicianId, String technicianName, String summary, String description, WorkOrderPriority priority, WorkOrderStatus status, String resolutionNotes, Instant createdAt, Instant updatedAt, Instant completedAt, Instant cancelledAt, List<PartView> parts, List<AppointmentView> appointments, List<EventView> events) {}
    public record DashboardView(long open, long assigned, long inProgress, long waitingParts, long completed, long cancelled, long lowStockItems) {}
}
