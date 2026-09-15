package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.Technician;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface TechnicianRepository extends JpaRepository<Technician, UUID> {}
