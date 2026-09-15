package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.ServiceSite;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface ServiceSiteRepository extends JpaRepository<ServiceSite, UUID> { List<ServiceSite> findByCustomerIdOrderByName(UUID customerId); }
