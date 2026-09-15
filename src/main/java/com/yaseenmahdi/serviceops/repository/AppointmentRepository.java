package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.Appointment;
import com.yaseenmahdi.serviceops.domain.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    List<Appointment> findByWorkOrderIdOrderByStartAt(UUID workOrderId);

    @Query("""
        select count(a) > 0 from Appointment a
        where a.technician.id = :technicianId
          and a.status = :status
          and a.startAt < :endAt
          and a.endAt > :startAt
        """)
    boolean hasConflict(@Param("technicianId") UUID technicianId,
                        @Param("status") AppointmentStatus status,
                        @Param("startAt") Instant startAt,
                        @Param("endAt") Instant endAt);
}
