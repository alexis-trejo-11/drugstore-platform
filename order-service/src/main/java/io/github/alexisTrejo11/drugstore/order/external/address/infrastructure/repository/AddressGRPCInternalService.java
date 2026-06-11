package io.github.alexisTrejo11.drugstore.order.external.address.infrastructure.repository;

import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.order.external.address.model.DeliveryAddress;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.AddressID;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

/**
 * Need to be implemented, I have issues with grpc proto and netwwork files. So
 * will be empty for now.
 * For testing mock the interface until the grpc issues are resolved.
 */
@Repository
public class AddressGRPCInternalService implements AddressRepository {

  @Override
  public DeliveryAddress getAddressByID(AddressID id) {
    return null;
  }

  @Override
  public DeliveryAddress getAddressByIDAndUserID(AddressID id, UserID userID) {
    return null;
  }
}
