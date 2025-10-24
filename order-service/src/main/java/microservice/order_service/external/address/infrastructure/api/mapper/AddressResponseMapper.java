package microservice.order_service.external.address.infrastructure.api.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AddressResponseMapper implements ResponseMapper<DeliveryAddressResponse, DeliveryAddress> {

    @Override
    public DeliveryAddressResponse toResponse(DeliveryAddress address) {
        if (address == null) return null;
        return DeliveryAddressResponse.builder()
                .id(address.getId() != null ? address.getId().value() : null)
                .city(address.getCity() != null ? address.getCity() : null)
                .state(address.getState() != null ? address.getState() : null)
                .country(address.getCountry() != null ? address.getCountry() : null)
                .street(address.getStreet() != null ? address.getStreet() : null)
                .innerNumber(address.getInnerNumber() != null ? address.getInnerNumber() : null)
                .outerNumber(address.getOuterNumber() != null ? address.getOuterNumber() : null)
                .neighborhood(address.getNeighborhood() != null ? address.getNeighborhood() : null)
                .buildingType(address.getBuildingType() != null ? address.getBuildingType() : null)
                .zipCode(address.getZipCode() != null ? address.getZipCode() : null)
                .additionalInfo(address.getAdditionalInfo() != null ? address.getAdditionalInfo() : null)
                .isDefault(address.isDefault())
                .build();
    }

    @Override
    public List<DeliveryAddressResponse> toResponses(List<DeliveryAddress> addresses) {
        return addresses.stream().map(this::toResponse).toList();
    }

    @Override
    public PageResponse<DeliveryAddressResponse> toResponsePage(PageResponse<DeliveryAddress> deliveryAddresses) {
        var responsePage = deliveryAddresses.map(this::toResponse);
        return PageResponse.from(responsePage);
    }
}
