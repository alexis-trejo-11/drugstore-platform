package microservice.inventory_service.internal.core.inventory.application.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.application.cqrs.command.ResolveInventoryAlertCommand;
import microservice.inventory_service.internal.core.alert.domain.entity.InventoryAlert;
import microservice.inventory_service.internal.core.alert.domain.port.InventoryAlertOutputPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResolveInventoryAlertCommandHandler {
    private final InventoryAlertOutputPort alertRepository;
    
    @Transactional
    public void handle(ResolveInventoryAlertCommand command) {
        InventoryAlert alert = alertRepository.findById(command.alertId())
            .orElseThrow(() -> new IllegalStateException("Alert not found"));
        
        alert.resolve(command.resolvedBy(), command.resolutionNotes());
        alertRepository.save(alert);
    }
}