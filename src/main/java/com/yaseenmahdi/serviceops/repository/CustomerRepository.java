package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface CustomerRepository extends JpaRepository<Customer, UUID> {}
