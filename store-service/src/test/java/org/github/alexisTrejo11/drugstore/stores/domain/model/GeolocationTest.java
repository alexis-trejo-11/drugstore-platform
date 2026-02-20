package org.github.alexisTrejo11.drugstore.stores.domain.model;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GeolocationTest {

    @Test
    void validateBounds() {
        Geolocation g = Geolocation.of(0.0, 0.0);
        assertDoesNotThrow(g::validate);

        Geolocation badLat = Geolocation.of(100.0, 0.0);
        assertThrows(IllegalArgumentException.class, badLat::validate);

        Geolocation badLon = Geolocation.of(0.0, 200.0);
        assertThrows(IllegalArgumentException.class, badLon::validate);
    }
}

