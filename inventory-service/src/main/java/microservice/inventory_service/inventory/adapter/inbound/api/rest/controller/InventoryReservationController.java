package microservice.inventory_service.inventory.adapter.inbound.api.rest.controller;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.InventoryId;
import microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.inventory.core.stock.application.command.ConfirmReservationCommand;
import microservice.inventory_service.inventory.core.stock.application.command.ReleaseReservationCommand;
import microservice.inventory_service.inventory.core.stock.application.query.GetActiveReservationsQuery;
import microservice.inventory_service.inventory.core.stock.domain.entity.StockReservation;
import microservice.inventory_service.inventory.core.stock.domain.valueobject.ReservationId;
import microservice.inventory_service.inventory.core.stock.port.input.ReservationUseCase;
import microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.request.ReserveStockRequest;
import microservice.inventory_service.inventory.adapter.inbound.api.rest.dto.response.ReservationResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v2/inventories")
@RequiredArgsConstructor
public class InventoryReservationController {
    private final ReservationUseCase reservationUseCase;
    private final ResponseMapper<ReservationResponse, StockReservation> responseMapper;
    
    @PostMapping("/{inventoryId}/stock/reservations")
    public ResponseEntity<ResponseWrapper<ReservationId>> reserveStock(
            @PathVariable String inventoryId,
            @Valid @RequestBody ReserveStockRequest request) {

        ReservationId reservationId = reservationUseCase.reserveStock(request.toCommand(inventoryId));
        
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(reservationId, "Stock Reservation"));
    }
    
    @GetMapping("/{inventoryId}/stock/reservations/active")
    public ResponseWrapper<List<ReservationResponse>> getActiveReservations(@PathVariable String inventoryId) {
        var query = new GetActiveReservationsQuery(InventoryId.of(inventoryId));
        List<StockReservation> reservations = reservationUseCase.getActiveReservations(query);

        List<ReservationResponse> reservationResponses = responseMapper.toResponses(reservations);
        return ResponseWrapper.found(reservationResponses, "Active Reservations");
    }
    
    @PatchMapping("/stock/reservations/{reservationId}/confirm")
    public ResponseWrapper<Void> confirmReservation(@PathVariable String reservationId) {
        var command = new ConfirmReservationCommand(ReservationId.of(reservationId), UserId.of("system"));
        reservationUseCase.confirmReservation(command);
        
        return ResponseWrapper.updated(null, "Reservation confirmed");
    }
    
    @PatchMapping("/reservations/{reservationId}/stock/release")
    public ResponseWrapper<Void> releaseReservation(@PathVariable String reservationId,
                                                    @RequestParam(required = false) String reason) {
        String reasonValue = (reason != null) ? reason : "Released by system";
        var command = new ReleaseReservationCommand(ReservationId.of(reservationId), reasonValue);

        reservationUseCase.releaseReservation(command);
        return ResponseWrapper.updated(null, "Reservation released");
    }
    
    @DeleteMapping("reservations/{reservationId}")
    public ResponseWrapper<Void> cancelReservation(@PathVariable String reservationId) {
        reservationUseCase.cancelReservation(ReservationId.of(reservationId));
        
        return ResponseWrapper.deleted(null, "Reservation");
    }
}