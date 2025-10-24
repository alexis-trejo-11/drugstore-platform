package microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import microservice.inventory_service.internal.core.stock.domain.valueobject.StockReservation;
import microservice.inventory_service.internal.infrastructure.adapter.inbound.api.rest.dto.response.ReservationResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReservationMapper implements ResponseMapper<ReservationResponse, StockReservation> {
    @Override
    public ReservationResponse toResponse(StockReservation stockReservation) {
        if (stockReservation == null) return null;

        return ReservationResponse.builder()
                .id(stockReservation.getId() != null ? stockReservation.getId().value() : null)
                .inventoryId(stockReservation.getInventoryId() != null ? stockReservation.getInventoryId().value() : null)
                .quantity(stockReservation.getQuantity())
                .status(stockReservation.getStatus())
                .expirationTime(stockReservation.getExpirationTime())
                .reason(stockReservation.getReason())
                .isExpired(stockReservation.isExpired())
                .isActive(stockReservation.isActive())
                .createdAt(stockReservation.getCreatedAt())
                .build();
    }

    @Override
    public List<ReservationResponse> toResponses(List<StockReservation> stockReservations) {
        if (stockReservations == null || stockReservations.isEmpty()) {
            return List.of();
        }

        return stockReservations.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public PageResponse<ReservationResponse> toResponsePage(Page<StockReservation> stockReservations) {
        if (stockReservations == null || stockReservations.isEmpty()) return PageResponse.empty();

        Page<ReservationResponse> responses = stockReservations.map(this::toResponse);
        return PageResponse.from(responses);
    }
}
