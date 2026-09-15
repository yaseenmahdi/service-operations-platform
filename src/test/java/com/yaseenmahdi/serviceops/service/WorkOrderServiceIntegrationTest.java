package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.domain.*;
import com.yaseenmahdi.serviceops.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class WorkOrderServiceIntegrationTest {
    @Autowired WorkOrderService service;
    @Autowired CustomerRepository customers;
    @Autowired ServiceSiteRepository sites;
    @Autowired TechnicianRepository technicians;
    @Autowired InventoryItemRepository inventory;

    @Test void completingWorkOrderConsumesReservedPartsAndRecordsLifecycle() {
        Customer customer = customers.save(new Customer("Acme", "acme@example.com", null));
        ServiceSite site = sites.save(new ServiceSite(customer, "HQ", "1 Main", "Denver", "CO", "80202", "America/Denver"));
        Technician technician = technicians.save(new Technician("Alex Tech", "alex@example.com", Set.of("General")));
        InventoryItem filter = inventory.save(new InventoryItem("FILTER-1", "Filter", 10, 2));

        var order = service.create(new CreateWorkOrder(site.getId(), null, "Replace filter", "Routine replacement", WorkOrderPriority.NORMAL));
        service.assign(order.id(), new AssignTechnician(technician.getId()));
        service.transition(order.id(), new TransitionWorkOrder(WorkOrderStatus.IN_PROGRESS));
        service.reservePart(order.id(), new ReservePart(filter.getId(), 2));
        var completed = service.complete(order.id(), new CompleteWorkOrder("Replaced filter and verified airflow"));

        InventoryItem refreshed = inventory.findById(filter.getId()).orElseThrow();
        assertThat(completed.status()).isEqualTo(WorkOrderStatus.COMPLETED);
        assertThat(refreshed.getQuantityOnHand()).isEqualTo(8);
        assertThat(refreshed.getQuantityReserved()).isZero();
        assertThat(completed.events()).extracting(event -> event.eventType()).contains("CREATED", "TECHNICIAN_ASSIGNED", "PART_RESERVED", "COMPLETED");
    }
}
