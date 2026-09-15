package com.yaseenmahdi.serviceops.config;

import com.yaseenmahdi.serviceops.domain.*;
import com.yaseenmahdi.serviceops.repository.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Set;

@Component
@ConditionalOnProperty(name = "app.demo-data.enabled", havingValue = "true")
public class DemoDataLoader implements ApplicationRunner {
    private final CustomerRepository customers; private final ServiceSiteRepository sites; private final AssetRepository assets;
    private final TechnicianRepository technicians; private final InventoryItemRepository inventory; private final WorkOrderRepository workOrders; private final WorkOrderEventRepository events;

    public DemoDataLoader(CustomerRepository customers, ServiceSiteRepository sites, AssetRepository assets, TechnicianRepository technicians, InventoryItemRepository inventory, WorkOrderRepository workOrders, WorkOrderEventRepository events) {
        this.customers = customers; this.sites = sites; this.assets = assets; this.technicians = technicians; this.inventory = inventory; this.workOrders = workOrders; this.events = events;
    }

    @Override @Transactional
    public void run(ApplicationArguments args) {
        if (customers.count() > 0) return;
        Customer customer = customers.save(new Customer("Northstar Property Group", "operations@northstar.example", "303-555-0142"));
        ServiceSite site = sites.save(new ServiceSite(customer, "Downtown Office", "1600 Market St", "Denver", "CO", "80202", "America/Denver"));
        Asset hvac = assets.save(new Asset(site, "Rooftop HVAC Unit 3", "HVAC", "HVAC-RTU-003", LocalDate.of(2024, 5, 12)));
        technicians.save(new Technician("Maya Chen", "maya.chen@example.com", Set.of("HVAC", "Electrical")));
        technicians.save(new Technician("Jordan Rivera", "jordan.rivera@example.com", Set.of("Networking", "IoT")));
        inventory.save(new InventoryItem("FLT-20X25", "20x25 HVAC Filter", 24, 6));
        inventory.save(new InventoryItem("THR-24V", "24V Thermostat Relay", 8, 2));
        inventory.save(new InventoryItem("CAT6-5FT", "Cat6 Patch Cable - 5 ft", 40, 10));
        WorkOrder order = workOrders.save(new WorkOrder(site, hvac, "Investigate intermittent airflow alert", "Occupants reported intermittent low-airflow alerts from the third-floor zone.", WorkOrderPriority.HIGH));
        events.save(new WorkOrderEvent(order, "CREATED", "Demo work order created"));
    }
}
