package com.yaseenmahdi.serviceops.domain;

import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "inventory_items", indexes = @Index(name = "idx_inventory_sku", columnList = "sku", unique = true))
public class InventoryItem extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Version
    private long version;

    @Column(nullable = false, length = 60, unique = true)
    private String sku;

    @Column(nullable = false, length = 160)
    private String name;

    @Column(name = "quantity_on_hand", nullable = false)
    private int quantityOnHand;

    @Column(name = "quantity_reserved", nullable = false)
    private int quantityReserved;

    @Column(name = "reorder_point", nullable = false)
    private int reorderPoint;

    protected InventoryItem() {}

    public InventoryItem(String sku, String name, int quantityOnHand, int reorderPoint) {
        if (quantityOnHand < 0 || reorderPoint < 0) throw new IllegalArgumentException("Inventory quantities cannot be negative");
        this.sku = sku;
        this.name = name;
        this.quantityOnHand = quantityOnHand;
        this.reorderPoint = reorderPoint;
    }

    public void reserve(int quantity) {
        requirePositive(quantity);
        if (availableQuantity() < quantity) {
            throw new BusinessRuleException("Insufficient available inventory for SKU " + sku);
        }
        quantityReserved += quantity;
    }

    public void release(int quantity) {
        requirePositive(quantity);
        if (quantity > quantityReserved) throw new BusinessRuleException("Cannot release more inventory than is reserved");
        quantityReserved -= quantity;
    }

    public void consumeReserved(int quantity) {
        requirePositive(quantity);
        if (quantity > quantityReserved) throw new BusinessRuleException("Cannot consume more inventory than is reserved");
        if (quantity > quantityOnHand) throw new BusinessRuleException("Cannot consume more inventory than is on hand");
        quantityReserved -= quantity;
        quantityOnHand -= quantity;
    }

    public void receive(int quantity) {
        requirePositive(quantity);
        quantityOnHand += quantity;
    }

    private void requirePositive(int quantity) {
        if (quantity <= 0) throw new BusinessRuleException("Quantity must be greater than zero");
    }

    public int availableQuantity() { return quantityOnHand - quantityReserved; }
    public boolean isBelowReorderPoint() { return availableQuantity() <= reorderPoint; }
    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public int getQuantityOnHand() { return quantityOnHand; }
    public int getQuantityReserved() { return quantityReserved; }
    public int getReorderPoint() { return reorderPoint; }
}
