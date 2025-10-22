package microservice.inventory.application.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.port.output.InventoryAlertRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class ResolveInventoryAlertCommandHandler {
    private final InventoryAlertRepository alertRepository;

    @Transactional
    public void handle(ResolveInventoryAlertCommand command) {
        InventoryAlert alert = alertRepository.findById(command.alertId())
                .orElseThrow(() -> new IllegalArgumentException("Inventory alert not found"));

        alert.resolve(command.resolvedBy(), command.resolutionNotes());
        alertRepository.save(alert);
    }
}
