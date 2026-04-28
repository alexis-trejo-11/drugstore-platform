package io.github.alexisTrejo11.drugstore.inventories.shared.domain.order;

import java.util.UUID;

public class StringId {
    private final String value;

    protected StringId(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static StringId generate() {
        return new StringId(UUID.randomUUID().toString());
    }
}
