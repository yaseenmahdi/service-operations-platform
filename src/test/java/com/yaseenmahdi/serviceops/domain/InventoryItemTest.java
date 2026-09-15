package com.yaseenmahdi.serviceops.domain;

import com.yaseenmahdi.serviceops.exception.BusinessRuleException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class InventoryItemTest {
    @Test void reserveReleaseAndConsumeMaintainInventoryInvariants() {
        InventoryItem item = new InventoryItem("TEST-1", "Test Part", 10, 2);
        item.reserve(4);
        assertThat(item.getQuantityReserved()).isEqualTo(4);
        assertThat(item.availableQuantity()).isEqualTo(6);
        item.release(1);
        item.consumeReserved(3);
        assertThat(item.getQuantityOnHand()).isEqualTo(7);
        assertThat(item.getQuantityReserved()).isZero();
    }

    @Test void cannotReserveMoreThanAvailable() {
        InventoryItem item = new InventoryItem("TEST-2", "Test Part", 2, 1);
        assertThatThrownBy(() -> item.reserve(3)).isInstanceOf(BusinessRuleException.class).hasMessageContaining("Insufficient");
    }
}
