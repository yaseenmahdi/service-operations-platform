package com.yaseenmahdi.serviceops.repository;

import com.yaseenmahdi.serviceops.domain.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;
public interface AssetRepository extends JpaRepository<Asset, UUID> { List<Asset> findBySiteIdOrderByName(UUID siteId); }
