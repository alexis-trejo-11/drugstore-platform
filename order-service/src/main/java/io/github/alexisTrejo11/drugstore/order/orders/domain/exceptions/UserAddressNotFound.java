package io.github.alexisTrejo11.drugstore.order.orders.domain.exceptions;

import libs_kernel.exceptions.NotFoundException;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;


public class UserAddressNotFound extends NotFoundException {
    public UserAddressNotFound(AddressID addressID) {
        super("User Delivery Address", "id", addressID.value());
    }
}

