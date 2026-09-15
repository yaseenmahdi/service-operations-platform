package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "work_order_events")
public class WorkOrderEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @Column(name = "event_type", nullable = false, length = 60)
    private String eventType;

    @Column(nullable = false, length = 1000)
    private String message;

    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected WorkOrderEvent() {}

    public WorkOrderEvent(WorkOrder workOrder, String eventType, String message) {
        this.workOrder = workOrder;
        this.eventType = eventType;
        this.message = message;
        this.occurredAt = Instant.now();
    }

    public UUID getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public String getEventType() { return eventType; }
    public String getMessage() { return message; }
    public Instant getOccurredAt() { return occurredAt; }
}
