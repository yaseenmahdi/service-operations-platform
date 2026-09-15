package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Requests.*;
import com.yaseenmahdi.serviceops.domain.*;
import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import com.yaseenmahdi.serviceops.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class AppointmentSchedulingIntegrationTest {
    @Autowired WorkOrderService service;
    @Autowired CustomerRepository customers;
    @Autowired ServiceSiteRepository sites;
    @Autowired TechnicianRepository technicians;

    @Test void rejectsOverlappingTechnicianAppointmentsAndPreservesSiteTimezone() {
        Customer customer = customers.save(new Customer("Acme 2", "acme2@example.com", null));
        ServiceSite site = sites.save(new ServiceSite(customer, "Mountain Site", "2 Main", "Denver", "CO", "80203", "America/Denver"));
        Technician technician = technicians.save(new Technician("Jordan Tech", "jordan@example.com", Set.of("General")));
        var first = service.create(new CreateWorkOrder(site.getId(), null, "First", "First appointment", WorkOrderPriority.NORMAL));
        var second = service.create(new CreateWorkOrder(site.getId(), null, "Second", "Second appointment", WorkOrderPriority.NORMAL));
        var start = LocalDateTime.of(2026, 9, 20, 9, 0);

        var scheduled = service.schedule(first.id(), new ScheduleAppointment(technician.getId(), start, start.plusHours(2), null));
        assertThat(scheduled.appointments().getFirst().timeZone()).isEqualTo("America/Denver");
        assertThatThrownBy(() -> service.schedule(second.id(), new ScheduleAppointment(technician.getId(), start.plusHours(1), start.plusHours(3), null)))
            .isInstanceOf(BusinessRuleException.class).hasMessageContaining("overlapping");
    }
}
