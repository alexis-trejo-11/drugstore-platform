package microservice.inventory_service.inventory.core.inventory.service.cqrs.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.core.alert.domain.port.InventoryAlertRepository;
import microservice.inventory_service.inventory.core.inventory.service.cqrs.command.ResolveInventoryAlertCommand;
import microservice.inventory_service.inventory.core.alert.domain.entity.InventoryAlert;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResolveInventoryAlertCommandHandler {
    private final InventoryAlertRepository alertRepository;
    
    @Transactional
    public void handle(ResolveInventoryAlertCommand command) {
        InventoryAlert alert = alertRepository.findById(command.alertId())
            .orElseThrow(() -> new IllegalStateException("Alert not found"));
        
        alert.resolve(command.resolvedBy(), command.resolutionNotes());
        alertRepository.save(alert);
    }
}