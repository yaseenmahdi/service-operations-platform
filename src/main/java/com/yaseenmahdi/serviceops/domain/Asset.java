package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "assets", indexes = @Index(name = "idx_asset_serial", columnList = "serial_number", unique = true))
public class Asset extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private ServiceSite site;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "asset_type", nullable = false, length = 100)
    private String assetType;

    @Column(name = "serial_number", nullable = false, length = 100, unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AssetStatus status = AssetStatus.ACTIVE;

    @Column(name = "installed_on")
    private LocalDate installedOn;

    protected Asset() {}

    public Asset(ServiceSite site, String name, String assetType, String serialNumber, LocalDate installedOn) {
        this.site = site;
        this.name = name;
        this.assetType = assetType;
        this.serialNumber = serialNumber;
        this.installedOn = installedOn;
    }

    public UUID getId() { return id; }
    public ServiceSite getSite() { return site; }
    public String getName() { return name; }
    public String getAssetType() { return assetType; }
    public String getSerialNumber() { return serialNumber; }
    public AssetStatus getStatus() { return status; }
    public LocalDate getInstalledOn() { return installedOn; }
    public void setStatus(AssetStatus status) { this.status = status; }
}
