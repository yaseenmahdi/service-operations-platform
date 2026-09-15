package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.PartReservationStatus;
import com.yaseenmahdi.serviceops.domain.WorkOrderPart;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface WorkOrderPartRepository extends JpaRepository<WorkOrderPart, UUID> { List<WorkOrderPart> findByWorkOrderIdOrderByCreatedAt(UUID workOrderId); List<WorkOrderPart> findByWorkOrderIdAndStatus(UUID workOrderId, PartReservationStatus status); }
