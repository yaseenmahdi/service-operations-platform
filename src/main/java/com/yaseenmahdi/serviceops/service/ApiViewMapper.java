package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Responses.*;
import com.yaseenmahdi.serviceops.domain.*;
import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class ApiViewMapper {
    private static final DateTimeFormatter LOCAL = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z");

    public CustomerView customer(Customer value) { return new CustomerView(value.getId(), value.getName(), value.getEmail(), value.getPhone(), value.isActive()); }
    public SiteView site(ServiceSite value) { return new SiteView(value.getId(), value.getCustomer().getId(), value.getName(), value.getAddressLine1(), value.getCity(), value.getState(), value.getPostalCode(), value.getTimeZone()); }
    public AssetView asset(Asset value) { return new AssetView(value.getId(), value.getSite().getId(), value.getName(), value.getAssetType(), value.getSerialNumber(), value.getStatus(), value.getInstalledOn()); }
    public TechnicianView technician(Technician value) { return new TechnicianView(value.getId(), value.getFullName(), value.getEmail(), value.isActive(), value.getSkills()); }
    public InventoryView inventory(InventoryItem value) { return new InventoryView(value.getId(), value.getSku(), value.getName(), value.getQuantityOnHand(), value.getQuantityReserved(), value.availableQuantity(), value.getReorderPoint(), value.isBelowReorderPoint()); }
    public PartView part(WorkOrderPart value) { return new PartView(value.getId(), value.getInventoryItem().getId(), value.getInventoryItem().getSku(), value.getInventoryItem().getName(), value.getQuantity(), value.getStatus()); }
    public EventView event(WorkOrderEvent value) { return new EventView(value.getId(), value.getEventType(), value.getMessage(), value.getOccurredAt()); }

    public AppointmentView appointment(Appointment value) {
        ZoneId zone = ZoneId.of(value.getSourceTimeZone());
        return new AppointmentView(value.getId(), value.getTechnician().getId(), value.getTechnician().getFullName(), value.getStartAt(), value.getEndAt(), value.getSourceTimeZone(), LOCAL.format(value.getStartAt().atZone(zone)), LOCAL.format(value.getEndAt().atZone(zone)), value.getStatus());
    }
}
