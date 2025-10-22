package microservice.inventory.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory.application.command.ResolveInventoryAlertCommand;
import microservice.inventory.domain.entity.InventoryAlert;
import microservice.inventory.domain.port.output.InventoryAlertRepository;
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