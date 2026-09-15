package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.api.dto.Responses.*;
import com.yaseenmahdi.serviceops.domain.*;
import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import com.yaseenmahdi.serviceops.exception.NotFoundException;
import com.yaseenmahdi.serviceops.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class WorkOrderService {
    private final WorkOrderRepository workOrders;
    private final ServiceSiteRepository sites;
    private final AssetRepository assets;
    private final TechnicianRepository technicians;
    private final InventoryItemRepository inventory;
    private final WorkOrderPartRepository parts;
    private final AppointmentRepository appointments;
    private final WorkOrderEventRepository events;
    private final ApiViewMapper mapper;

    public WorkOrderService(WorkOrderRepository workOrders, ServiceSiteRepository sites, AssetRepository assets, TechnicianRepository technicians, InventoryItemRepository inventory, WorkOrderPartRepository parts, AppointmentRepository appointments, WorkOrderEventRepository events, ApiViewMapper mapper) {
        this.workOrders = workOrders; this.sites = sites; this.assets = assets; this.technicians = technicians; this.inventory = inventory; this.parts = parts; this.appointments = appointments; this.events = events; this.mapper = mapper;
    }

    public WorkOrderView create(CreateWorkOrder request) {
        ServiceSite site = sites.findById(request.siteId()).orElseThrow(() -> new NotFoundException("Service site not found"));
        Asset asset = null;
        if (request.assetId() != null) {
            asset = assets.findById(request.assetId()).orElseThrow(() -> new NotFoundException("Asset not found"));
            if (!asset.getSite().getId().equals(site.getId())) throw new BusinessRuleException("Asset does not belong to the selected service site");
        }
        WorkOrder order = workOrders.save(new WorkOrder(site, asset, request.summary().trim(), request.description().trim(), request.priority()));
        events.save(new WorkOrderEvent(order, "CREATED", "Work order created"));
        return view(order);
    }

    @Transactional(readOnly = true)
    public List<WorkOrderView> list(WorkOrderStatus status) {
        List<WorkOrder> values = status == null ? workOrders.findAllByOrderByCreatedAtDesc() : workOrders.findByStatusOrderByCreatedAtDesc(status);
        return values.stream().map(this::view).toList();
    }

    @Transactional(readOnly = true)
    public WorkOrderView get(UUID id) { return view(requireOrder(id)); }

    public WorkOrderView assign(UUID id, AssignTechnician request) {
        WorkOrder order = requireOrder(id);
        Technician technician = technicians.findById(request.technicianId()).orElseThrow(() -> new NotFoundException("Technician not found"));
        if (!technician.isActive()) throw new BusinessRuleException("Inactive technicians cannot be assigned");
        order.assign(technician);
        events.save(new WorkOrderEvent(order, "TECHNICIAN_ASSIGNED", "Assigned to " + technician.getFullName()));
        return view(order);
    }

    public WorkOrderView transition(UUID id, TransitionWorkOrder request) {
        WorkOrder order = requireOrder(id);
        WorkOrderStatus previous = order.getStatus();
        order.transitionTo(request.status());
        events.save(new WorkOrderEvent(order, "STATUS_CHANGED", previous + " -> " + order.getStatus()));
        if (request.status() == WorkOrderStatus.CANCELLED) releaseReservedParts(order);
        return view(order);
    }

    public WorkOrderView reservePart(UUID id, ReservePart request) {
        WorkOrder order = requireOrder(id);
        if (order.isTerminal()) throw new BusinessRuleException("Parts cannot be reserved for a closed work order");
        InventoryItem item = inventory.findById(request.inventoryItemId()).orElseThrow(() -> new NotFoundException("Inventory item not found"));
        item.reserve(request.quantity());
        parts.save(new WorkOrderPart(order, item, request.quantity()));
        events.save(new WorkOrderEvent(order, "PART_RESERVED", request.quantity() + " x " + item.getSku() + " reserved"));
        return view(order);
    }

    public WorkOrderView releasePart(UUID orderId, UUID partId) {
        WorkOrder order = requireOrder(orderId);
        WorkOrderPart part = parts.findById(partId).orElseThrow(() -> new NotFoundException("Part reservation not found"));
        if (!part.getWorkOrder().getId().equals(orderId)) throw new BusinessRuleException("Part reservation does not belong to this work order");
        part.getInventoryItem().release(part.getQuantity());
        part.markReleased();
        events.save(new WorkOrderEvent(order, "PART_RELEASED", part.getQuantity() + " x " + part.getInventoryItem().getSku() + " released"));
        return view(order);
    }

    public WorkOrderView schedule(UUID id, ScheduleAppointment request) {
        WorkOrder order = requireOrder(id);
        if (order.isTerminal()) throw new BusinessRuleException("Cannot schedule a closed work order");
        Technician technician = technicians.findById(request.technicianId()).orElseThrow(() -> new NotFoundException("Technician not found"));
        String zoneName = request.timeZone() == null || request.timeZone().isBlank() ? order.getSite().getTimeZone() : request.timeZone().trim();
        ZoneId zone = ZoneId.of(zoneName);
        var start = request.startLocal().atZone(zone).toInstant();
        var end = request.endLocal().atZone(zone).toInstant();
        if (!end.isAfter(start)) throw new BusinessRuleException("Appointment end time must be after the start time");
        if (order.getTechnician() != null && !order.getTechnician().getId().equals(technician.getId())) {
            throw new BusinessRuleException("Appointment technician must match the work-order assignment");
        }
        if (appointments.hasConflict(technician.getId(), AppointmentStatus.SCHEDULED, start, end)) throw new BusinessRuleException("Technician already has an overlapping appointment");
        appointments.save(new Appointment(order, technician, start, end, zoneName));
        if (order.getTechnician() == null) order.assign(technician);
        events.save(new WorkOrderEvent(order, "APPOINTMENT_SCHEDULED", "Appointment scheduled in " + zoneName));
        return view(order);
    }

    public WorkOrderView complete(UUID id, CompleteWorkOrder request) {
        WorkOrder order = requireOrder(id);
        for (WorkOrderPart part : parts.findByWorkOrderIdAndStatus(id, PartReservationStatus.RESERVED)) {
            part.getInventoryItem().consumeReserved(part.getQuantity());
            part.markConsumed();
        }
        appointments.findByWorkOrderIdOrderByStartAt(id).stream().filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED).forEach(Appointment::complete);
        order.complete(request.resolutionNotes());
        events.save(new WorkOrderEvent(order, "COMPLETED", "Work order completed"));
        return view(order);
    }

    private void releaseReservedParts(WorkOrder order) {
        for (WorkOrderPart part : parts.findByWorkOrderIdAndStatus(order.getId(), PartReservationStatus.RESERVED)) {
            part.getInventoryItem().release(part.getQuantity());
            part.markReleased();
        }
        appointments.findByWorkOrderIdOrderByStartAt(order.getId()).stream().filter(a -> a.getStatus() == AppointmentStatus.SCHEDULED).forEach(Appointment::cancel);
    }

    private WorkOrder requireOrder(UUID id) { return workOrders.findById(id).orElseThrow(() -> new NotFoundException("Work order not found")); }

    private WorkOrderView view(WorkOrder order) {
        var partViews = parts.findByWorkOrderIdOrderByCreatedAt(order.getId()).stream().map(mapper::part).toList();
        var appointmentViews = appointments.findByWorkOrderIdOrderByStartAt(order.getId()).stream().map(mapper::appointment).toList();
        var eventViews = events.findByWorkOrderIdOrderByOccurredAtAsc(order.getId()).stream().map(mapper::event).toList();
        Asset asset = order.getAsset(); Technician tech = order.getTechnician();
        return new WorkOrderView(order.getId(), order.getSite().getId(), order.getSite().getName(), asset == null ? null : asset.getId(), asset == null ? null : asset.getName(), tech == null ? null : tech.getId(), tech == null ? null : tech.getFullName(), order.getSummary(), order.getDescription(), order.getPriority(), order.getStatus(), order.getResolutionNotes(), order.getCreatedAt(), order.getUpdatedAt(), order.getCompletedAt(), order.getCancelledAt(), partViews, appointmentViews, eventViews);
    }
}
