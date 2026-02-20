package io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application;

import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler.*;
import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ConfirmReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReleaseReservationCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.command.ReserveStockCommand;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.handler.*;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.application.query.GetActiveReservationsQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.valueobject.ReservationId;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.domain.entity.StockReservation;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.stock.port.input.ReservationUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationApplicationService implements ReservationUseCase {
  private final ReserveStockCommandHandler reserveHandler;
  private final ConfirmReservationCommandHandler confirmHandler;
  private final ReleaseReservationCommandHandler releaseHandler;
  private final CancelReservationCommandHandler cancelHandler;
  private final GetActiveReservationsQueryHandler getActiveHandler;

  @Override
  @Transactional
  public ReservationId reserveStock(ReserveStockCommand command) {
    return reserveHandler.handle(command);
  }

  @Override
  @Transactional
  public void confirmReservation(ConfirmReservationCommand command) {
    confirmHandler.handle(command);
  }

  @Override
  @Transactional
  public void releaseReservation(ReleaseReservationCommand command) {
    releaseHandler.handle(command);
  }

  @Override
  @Transactional(readOnly = true)
  public List<StockReservation> getActiveReservations(GetActiveReservationsQuery query) {
    return getActiveHandler.handle(query);
  }

  @Override
  @Transactional
  public void cancelReservation(ReservationId reservationId) {
    cancelHandler.handle(reservationId);
  }
}
