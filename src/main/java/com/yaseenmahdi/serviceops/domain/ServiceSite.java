package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "service_sites")
public class ServiceSite extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "address_line1", nullable = false, length = 180)
    private String addressLine1;

    @Column(nullable = false, length = 100)
    private String city;

    @Column(nullable = false, length = 80)
    private String state;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "time_zone", nullable = false, length = 80)
    private String timeZone;

    protected ServiceSite() {}

    public ServiceSite(Customer customer, String name, String addressLine1, String city, String state, String postalCode, String timeZone) {
        this.customer = customer;
        this.name = name;
        this.addressLine1 = addressLine1;
        this.city = city;
        this.state = state;
        this.postalCode = postalCode;
        this.timeZone = timeZone;
    }

    public UUID getId() { return id; }
    public Customer getCustomer() { return customer; }
    public String getName() { return name; }
    public String getAddressLine1() { return addressLine1; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getPostalCode() { return postalCode; }
    public String getTimeZone() { return timeZone; }
}
