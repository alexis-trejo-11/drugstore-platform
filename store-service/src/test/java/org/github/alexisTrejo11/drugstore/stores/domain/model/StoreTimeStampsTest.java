package org.github.alexisTrejo11.drugstore.stores.domain.model;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreTimeStamps;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class StoreTimeStampsTest {

    @Test
    void markAsDeletedAndRestore() {
        StoreTimeStamps stamps = StoreTimeStamps.now();
        assertNull(stamps.getDeletedAt());

        stamps.markAsDeleted();
        assertNotNull(stamps.getDeletedAt());

        stamps.restore();
        assertNull(stamps.getDeletedAt());
    }
}

