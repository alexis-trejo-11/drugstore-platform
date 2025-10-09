package microservice.order_service.external.address.infrastructure.persistence;

import libs_kernel.mapper.ModelMapper;
import libs_kernel.page.PageResponse;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.BuildingType;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressModelMapper implements ModelMapper<DeliveryAddress, DeliveryAddressModel> {

    @Override
    public DeliveryAddressModel fromDomain(DeliveryAddress address) {
        if (address == null) return null;
        return DeliveryAddressModel.builder()
                .id(address.getId().value() != null ? address.getId().value() : null)
                .city(address.getCity() != null ? address.getCity() : null)
                .state(address.getState() != null ? address.getState() : null)
                .country(address.getCountry() != null ? address.getCountry() : null)
                .street(address.getStreet() != null ? address.getStreet() : null)
                .innerNumber(address.getInnerNumber() != null ? address.getInnerNumber() : null)
                .outerNumber(address.getOuterNumber() != null ? address.getOuterNumber() : null)
                .neighborhood(address.getNeighborhood() != null ? address.getNeighborhood() : null)
                .buildingType(address.getBuildingType() != null ? address.getBuildingType().getDisplayName() : null)
                .zipCode(address.getZipCode() != null ? address.getZipCode() : null)
                .additionalInfo(address.getAdditionalInfo() != null ? address.getAdditionalInfo() : null)
                .isDefault(address.isDefault())
                .user(address.getUserID() != null ? new UserModel(address.getUserID().value()) : null)
                .build();
    }

    @Override
    public DeliveryAddress toDomain(DeliveryAddressModel model) {
        if (model == null) return null;
        return DeliveryAddress.builder()
                .id(model.getId() != null ? AddressID.of(model.getId()) : null)
                .city(model.getCity() != null ? model.getCity() : null)
                .state(model.getState() != null ? model.getState() : null)
                .country(model.getCountry() != null ? model.getCountry() : null)
                .street(model.getStreet() != null ? model.getStreet() : null)
                .innerNumber(model.getInnerNumber() != null ? model.getInnerNumber() : null)
                .outerNumber(model.getOuterNumber() != null ? model.getOuterNumber() : null)
                .neighborhood(model.getNeighborhood() != null ? model.getNeighborhood() : null)
                .buildingType(model.getBuildingType() != null ? BuildingType.fromString(model.getBuildingType()) : null)
                .zipCode(model.getZipCode() != null ? model.getZipCode() : null)
                .additionalInfo(model.getAdditionalInfo() != null ? model.getAdditionalInfo() : null)
                .isDefault(model.isDefault())
                .userID(model.getUserId() != null ? UserID.of(model.getUser().getId()) : null)
                .build();
    }

    @Override
    public List<DeliveryAddressModel> fromDomains(List<DeliveryAddress> modelList) {
        return modelList.stream()
                .map(this::fromDomain)
                .toList();
    }

    @Override
    public List<DeliveryAddress> toDomains(List<DeliveryAddressModel> deliveryAddressModels) {
        return deliveryAddressModels.stream()
                .map(this::toDomain)
                .toList();
    }


    @Override
    public Page<DeliveryAddress> toDomainPage(Page<DeliveryAddressModel> modelPage) {
        return modelPage.map(this::toDomain);
    }
}
