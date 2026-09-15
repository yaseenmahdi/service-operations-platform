package com.yaseenmahdi.serviceops.domain;

import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

class WorkOrderTest {
    @Test void enforcesLifecycleTransitionsAndCompletionRequirements() {
        Customer customer = new Customer("Customer", "customer@example.com", null);
        ServiceSite site = new ServiceSite(customer, "Site", "1 Main", "Denver", "CO", "80202", "America/Denver");
        WorkOrder order = new WorkOrder(site, null, "Issue", "Description", WorkOrderPriority.NORMAL);
        Technician tech = new Technician("Tech", "tech@example.com", Set.of("General"));

        assertThatThrownBy(() -> order.transitionTo(WorkOrderStatus.COMPLETED)).isInstanceOf(BusinessRuleException.class);
        order.assign(tech);
        order.transitionTo(WorkOrderStatus.IN_PROGRESS);
        order.complete("Resolved and verified");
        assertThat(order.getStatus()).isEqualTo(WorkOrderStatus.COMPLETED);
        assertThat(order.getCompletedAt()).isNotNull();
    }
}
