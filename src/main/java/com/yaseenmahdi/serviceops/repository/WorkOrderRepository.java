package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.WorkOrder;
import com.yaseenmahdi.serviceops.domain.WorkOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface WorkOrderRepository extends JpaRepository<WorkOrder, UUID> { List<WorkOrder> findByStatusOrderByCreatedAtDesc(WorkOrderStatus status); List<WorkOrder> findAllByOrderByCreatedAtDesc(); long countByStatus(WorkOrderStatus status); }
