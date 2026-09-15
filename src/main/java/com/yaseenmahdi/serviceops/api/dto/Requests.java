package com.yaseenmahdi.serviceops.api.dto;

import com.yaseenmahdi.serviceops.domain.WorkOrderPriority;
import com.yaseenmahdi.serviceops.domain.WorkOrderStatus;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public final class Requests {
    private Requests() {}

    public record CreateCustomer(@NotBlank @Size(max=140) String name, @NotBlank @Email @Size(max=180) String email, @Size(max=40) String phone) {}
    public record CreateSite(@NotBlank @Size(max=120) String name, @NotBlank @Size(max=180) String addressLine1, @NotBlank @Size(max=100) String city, @NotBlank @Size(max=80) String state, @NotBlank @Size(max=20) String postalCode, @NotBlank @Size(max=80) String timeZone) {}
    public record CreateAsset(@NotBlank @Size(max=120) String name, @NotBlank @Size(max=100) String assetType, @NotBlank @Size(max=100) String serialNumber, LocalDate installedOn) {}
    public record CreateTechnician(@NotBlank @Size(max=140) String fullName, @NotBlank @Email @Size(max=180) String email, Set<@NotBlank @Size(max=80) String> skills) {}
    public record CreateInventoryItem(@NotBlank @Size(max=60) String sku, @NotBlank @Size(max=160) String name, @Min(0) int quantityOnHand, @Min(0) int reorderPoint) {}
    public record ReceiveInventory(@Min(1) int quantity) {}
    public record CreateWorkOrder(@NotNull UUID siteId, UUID assetId, @NotBlank @Size(max=180) String summary, @NotBlank @Size(max=4000) String description, @NotNull WorkOrderPriority priority) {}
    public record AssignTechnician(@NotNull UUID technicianId) {}
    public record TransitionWorkOrder(@NotNull WorkOrderStatus status) {}
    public record ReservePart(@NotNull UUID inventoryItemId, @Min(1) int quantity) {}
    public record ScheduleAppointment(@NotNull UUID technicianId, @NotNull LocalDateTime startLocal, @NotNull LocalDateTime endLocal, @Size(max=80) String timeZone) {}
    public record CompleteWorkOrder(@NotBlank @Size(max=4000) String resolutionNotes) {}
}
