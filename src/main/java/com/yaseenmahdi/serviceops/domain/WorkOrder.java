package com.yaseenmahdi.serviceops.domain;

import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "work_orders")
public class WorkOrder extends AuditableEntity {
    private static final Map<WorkOrderStatus, Set<WorkOrderStatus>> ALLOWED_TRANSITIONS = Map.of(
        WorkOrderStatus.OPEN, EnumSet.of(WorkOrderStatus.ASSIGNED, WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.CANCELLED),
        WorkOrderStatus.ASSIGNED, EnumSet.of(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.WAITING_PARTS, WorkOrderStatus.CANCELLED),
        WorkOrderStatus.IN_PROGRESS, EnumSet.of(WorkOrderStatus.WAITING_PARTS, WorkOrderStatus.COMPLETED, WorkOrderStatus.CANCELLED),
        WorkOrderStatus.WAITING_PARTS, EnumSet.of(WorkOrderStatus.IN_PROGRESS, WorkOrderStatus.COMPLETED, WorkOrderStatus.CANCELLED),
        WorkOrderStatus.COMPLETED, EnumSet.noneOf(WorkOrderStatus.class),
        WorkOrderStatus.CANCELLED, EnumSet.noneOf(WorkOrderStatus.class)
    );

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private long version;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private ServiceSite site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "technician_id")
    private Technician technician;

    @Column(nullable = false, length = 180)
    private String summary;

    @Column(nullable = false, length = 4000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkOrderPriority priority;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private WorkOrderStatus status = WorkOrderStatus.OPEN;

    @Column(name = "resolution_notes", length = 4000)
    private String resolutionNotes;

    @Column(name = "completed_at")
    private Instant completedAt;

    @Column(name = "cancelled_at")
    private Instant cancelledAt;

    protected WorkOrder() {}

    public WorkOrder(ServiceSite site, Asset asset, String summary, String description, WorkOrderPriority priority) {
        this.site = site;
        this.asset = asset;
        this.summary = summary;
        this.description = description;
        this.priority = priority;
    }

    public void assign(Technician technician) {
        if (isTerminal()) throw new BusinessRuleException("Cannot assign a technician to a closed work order");
        this.technician = technician;
        if (status == WorkOrderStatus.OPEN) transitionTo(WorkOrderStatus.ASSIGNED);
    }

    public void transitionTo(WorkOrderStatus target) {
        if (target == status) return;
        if (!ALLOWED_TRANSITIONS.getOrDefault(status, Set.of()).contains(target)) {
            throw new BusinessRuleException("Invalid work-order transition: " + status + " -> " + target);
        }
        status = target;
        if (target == WorkOrderStatus.CANCELLED) cancelledAt = Instant.now();
    }

    public void complete(String resolutionNotes) {
        if (technician == null) throw new BusinessRuleException("A technician must be assigned before completion");
        if (resolutionNotes == null || resolutionNotes.isBlank()) throw new BusinessRuleException("Resolution notes are required to complete a work order");
        transitionTo(WorkOrderStatus.COMPLETED);
        this.resolutionNotes = resolutionNotes.trim();
        completedAt = Instant.now();
    }

    public boolean isTerminal() { return status == WorkOrderStatus.COMPLETED || status == WorkOrderStatus.CANCELLED; }

    public UUID getId() { return id; }
    public ServiceSite getSite() { return site; }
    public Asset getAsset() { return asset; }
    public Technician getTechnician() { return technician; }
    public String getSummary() { return summary; }
    public String getDescription() { return description; }
    public WorkOrderPriority getPriority() { return priority; }
    public WorkOrderStatus getStatus() { return status; }
    public String getResolutionNotes() { return resolutionNotes; }
    public Instant getCompletedAt() { return completedAt; }
    public Instant getCancelledAt() { return cancelledAt; }
}
