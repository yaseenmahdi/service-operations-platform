package com.yaseenmahdi.serviceops.domain;

import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "work_order_parts")
public class WorkOrderPart extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inventory_item_id", nullable = false)
    private InventoryItem inventoryItem;

    @Column(nullable = false)
    private int quantity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PartReservationStatus status = PartReservationStatus.RESERVED;

    protected WorkOrderPart() {}

    public WorkOrderPart(WorkOrder workOrder, InventoryItem inventoryItem, int quantity) {
        this.workOrder = workOrder;
        this.inventoryItem = inventoryItem;
        this.quantity = quantity;
    }

    public void markConsumed() {
        if (status != PartReservationStatus.RESERVED) throw new BusinessRuleException("Only reserved parts can be consumed");
        status = PartReservationStatus.CONSUMED;
    }

    public void markReleased() {
        if (status != PartReservationStatus.RESERVED) throw new BusinessRuleException("Only reserved parts can be released");
        status = PartReservationStatus.RELEASED;
    }

    public UUID getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public InventoryItem getInventoryItem() { return inventoryItem; }
    public int getQuantity() { return quantity; }
    public PartReservationStatus getStatus() { return status; }
}
