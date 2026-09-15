package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "customers", indexes = @Index(name = "idx_customer_email", columnList = "email", unique = true))
public class Customer extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 140)
    private String name;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(length = 40)
    private String phone;

    @Column(nullable = false)
    private boolean active = true;

    protected Customer() {}

    public Customer(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
