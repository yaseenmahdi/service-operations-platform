package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.api.dto.Responses.DashboardView;
import com.yaseenmahdi.serviceops.domain.WorkOrderStatus;
import com.yaseenmahdi.serviceops.repository.InventoryItemRepository;
import com.yaseenmahdi.serviceops.repository.WorkOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardService {
    private final WorkOrderRepository workOrders;
    private final InventoryItemRepository inventory;
    public DashboardService(WorkOrderRepository workOrders, InventoryItemRepository inventory) { this.workOrders = workOrders; this.inventory = inventory; }

    @Transactional(readOnly = true)
    public DashboardView get() {
        long lowStock = inventory.findAll().stream().filter(item -> item.isBelowReorderPoint()).count();
        return new DashboardView(
            workOrders.countByStatus(WorkOrderStatus.OPEN),
            workOrders.countByStatus(WorkOrderStatus.ASSIGNED),
            workOrders.countByStatus(WorkOrderStatus.IN_PROGRESS),
            workOrders.countByStatus(WorkOrderStatus.WAITING_PARTS),
            workOrders.countByStatus(WorkOrderStatus.COMPLETED),
            workOrders.countByStatus(WorkOrderStatus.CANCELLED),
            lowStock
        );
    }
}
