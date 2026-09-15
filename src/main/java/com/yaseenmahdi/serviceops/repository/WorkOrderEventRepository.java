package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.WorkOrderEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface WorkOrderEventRepository extends JpaRepository<WorkOrderEvent, UUID> { List<WorkOrderEvent> findByWorkOrderIdOrderByOccurredAtAsc(UUID workOrderId); }
