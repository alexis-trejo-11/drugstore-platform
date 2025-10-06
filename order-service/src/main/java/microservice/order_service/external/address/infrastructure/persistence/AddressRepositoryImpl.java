package microservice.order_service.external.address.infrastructure.persistence;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.output.AddressRepository;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class AddressRepositoryImpl implements AddressRepository {
    private JpaAddressRepository jpaAddressRepository;
    private ModelMapper<DeliveryAddress, DeliveryAddressModel> addressMapper;

    @Override
    public Optional<DeliveryAddress> findByID(AddressID addressID) {
        return jpaAddressRepository.findById(addressID.value())
                .map(addressMapper::toDomain);
    }

    @Override
    public Optional<DeliveryAddress> findDefaultByUserID(UserID userID) {
        return jpaAddressRepository.findDefaultByUserId(userID.value())
                .map(addressMapper::toDomain);
    }

    @Override
    public List<DeliveryAddress> findAllByUserID(UserID userID) {
        return  jpaAddressRepository.findByUserId(userID.value())
                .stream()
                .map(addressMapper::toDomain)
                .toList();
    }

    @Override
    public DeliveryAddress save(DeliveryAddress address) {
        DeliveryAddressModel model = addressMapper.fromDomain(address);
        DeliveryAddressModel savedModel = jpaAddressRepository.saveAndFlush(model);
        return addressMapper.toDomain(savedModel);

    }

    @Override
    public void bulkUpdate(List<DeliveryAddress> addresses) {
        List<DeliveryAddressModel> models = addresses.stream()
                .map(addressMapper::fromDomain)
                .toList();
        jpaAddressRepository.saveAll(models);
        jpaAddressRepository.flush();
    }

    @Override
    public void delete(DeliveryAddress address) {
        jpaAddressRepository.deleteById(address.getId().value());
    }
}
