package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "appointments")
public class Appointment extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "work_order_id", nullable = false)
    private WorkOrder workOrder;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "technician_id", nullable = false)
    private Technician technician;

    @Column(name = "start_at", nullable = false)
    private Instant startAt;

    @Column(name = "end_at", nullable = false)
    private Instant endAt;

    @Column(name = "source_time_zone", nullable = false, length = 80)
    private String sourceTimeZone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AppointmentStatus status = AppointmentStatus.SCHEDULED;

    protected Appointment() {}

    public Appointment(WorkOrder workOrder, Technician technician, Instant startAt, Instant endAt, String sourceTimeZone) {
        this.workOrder = workOrder;
        this.technician = technician;
        this.startAt = startAt;
        this.endAt = endAt;
        this.sourceTimeZone = sourceTimeZone;
    }

    public void cancel() { status = AppointmentStatus.CANCELLED; }
    public void complete() { status = AppointmentStatus.COMPLETED; }

    public UUID getId() { return id; }
    public WorkOrder getWorkOrder() { return workOrder; }
    public Technician getTechnician() { return technician; }
    public Instant getStartAt() { return startAt; }
    public Instant getEndAt() { return endAt; }
    public String getSourceTimeZone() { return sourceTimeZone; }
    public AppointmentStatus getStatus() { return status; }
}
