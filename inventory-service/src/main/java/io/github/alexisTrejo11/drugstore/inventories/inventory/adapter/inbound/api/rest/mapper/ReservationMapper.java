package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.mapper;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.response.ReservationResponse;
import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.inbound.api.rest.dto.response.StockReservationItemResponse;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservationMapper implements ResponseMapper<ReservationResponse, StockReservation> {
    @Override
    public ReservationResponse toResponse(StockReservation stockReservation) {
        if (stockReservation == null) return null;

        List<StockReservationItemResponse> reservationItems = getItemResponses(stockReservation);

        return ReservationResponse.builder()
                .reservationId(stockReservation.getId() != null ? stockReservation.getId().value() : null)
                .expirationTime(stockReservation.getExpirationTime())
                .orderId(stockReservation.getOrderReference() != null ? stockReservation.getOrderReference().orderId() : null)
                .orderType(stockReservation.getOrderReference() != null ? stockReservation.getOrderReference().type() : null)
                .status(stockReservation.getStatus())
                .reservations(reservationItems)
                .createdAt(stockReservation.getCreatedAt())
                .updatedAt(stockReservation.getUpdatedAt())
                .build();
    }


    private static List<StockReservationItemResponse> getItemResponses(StockReservation stockReservation) {
        List<StockReservationItemResponse> reservationItems = new ArrayList<>();
        if (stockReservation.getReservations() != null && !stockReservation.getReservations().isEmpty()) {
            var reservations = stockReservation.getReservations().values();

            reservations.forEach(item -> {
                var reservationItemResponse =
                        StockReservationItemResponse.builder()
                                .inventoryId(item.getInventoryId() != null ? item.getInventoryId().value() : null)
                                .associatedBatchId(item.getAssociatedBatchId() != null ? item.getAssociatedBatchId().value() : null)
                                .quantity(item.getQuantity())
                                .reason(item.getReason())
                                .status(item.getStatus())
                                .build();

                reservationItems.add(reservationItemResponse);
            });
        }
        return reservationItems;
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
