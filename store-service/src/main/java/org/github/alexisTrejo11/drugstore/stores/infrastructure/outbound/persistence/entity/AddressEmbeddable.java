package org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Embeddable
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressEmbeddable {
    @Column(name = "country", nullable = false, length = 100)
    private String country;

    @Column(name = "state", nullable = false, length = 100)
    private String state;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "neighborhood", length = 100)
    private String neighborhood;

    @Column(name = "street", nullable = false, length = 200)
    private String street;

    @Column(name = "number", nullable = false, length = 20)
    private String number;

    @Column(name = "zip_code", nullable = false, length = 20)
    private String zipCode;
}
