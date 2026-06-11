package io.github.alexisTrejo11.drugstore.order.external.address.infrastructure.repository;

import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddress;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

public interface AddressRepository {

  DeliveryAddress getAddressByID(AddressID id);
  DeliveryAddress getAddressByIDAndUserID(AddressID id, UserID userID);
}
