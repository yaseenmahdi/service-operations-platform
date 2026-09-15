package com.yaseenmahdi.serviceops.service;

import com.yaseenmahdi.serviceops.repository.InventoryItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryMonitoringService {
    private static final Logger log = LoggerFactory.getLogger(InventoryMonitoringService.class);
    private final InventoryItemRepository inventory;
    public InventoryMonitoringService(InventoryItemRepository inventory) { this.inventory = inventory; }

    @Scheduled(fixedDelayString = "${app.inventory-monitor.interval-ms:300000}")
    @Transactional(readOnly = true)
    public void reportLowStock() {
        inventory.findAll().stream().filter(item -> item.isBelowReorderPoint()).forEach(item ->
            log.warn("Low inventory: sku={} available={} reorderPoint={}", item.getSku(), item.availableQuantity(), item.getReorderPoint()));
    }
}
