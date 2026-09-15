package com.yaseenmahdi.serviceops.domain;

import jakarta.persistence.*;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "technicians", indexes = @Index(name = "idx_technician_email", columnList = "email", unique = true))
public class Technician extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "full_name", nullable = false, length = 140)
    private String fullName;

    @Column(nullable = false, length = 180, unique = true)
    private String email;

    @Column(nullable = false)
    private boolean active = true;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "technician_skills", joinColumns = @JoinColumn(name = "technician_id"))
    @Column(name = "skill", nullable = false, length = 80)
    private Set<String> skills = new LinkedHashSet<>();

    protected Technician() {}

    public Technician(String fullName, String email, Set<String> skills) {
        this.fullName = fullName;
        this.email = email;
        if (skills != null) this.skills.addAll(skills);
    }

    public UUID getId() { return id; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public boolean isActive() { return active; }
    public Set<String> getSkills() { return Set.copyOf(skills); }
    public void setActive(boolean active) { this.active = active; }
}
