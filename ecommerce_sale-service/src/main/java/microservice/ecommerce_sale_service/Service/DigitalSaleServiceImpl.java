package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Inventory.InventoryFacadeService;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Repository.DigitalSaleRepository;
import microservice.ecommerce_sale_service.Service.DomainService.DigitalSaleDomainService;
import microservice.ecommerce_sale_service.Utils.DigitalSaleValidator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class DigitalSaleServiceImpl implements DigitalSaleService {

    private final DigitalSaleRepository saleRepository;
    private final DigitalSaleValidator validator;
    private final DigitalSaleDomainService domainService;
    private final InventoryFacadeService  inventoryFacadeService;

    public DigitalSaleServiceImpl(DigitalSaleRepository saleRepository,
                                  DigitalSaleValidator validator,
                                  DigitalSaleDomainService domainService,
                                  InventoryFacadeService inventoryFacadeService) {
        this.saleRepository = saleRepository;
        this.validator = validator;
        this.domainService = domainService;
        this.inventoryFacadeService = inventoryFacadeService;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<DigitalSaleDTO> createDigitalSale(DigitalSaleItemInsertDTO dto) {
        return CompletableFuture.supplyAsync(() ->  {
            log.info("Creating digital sale");
            validator.validateSaleCreation(dto);

            DigitalSale sale = domainService.createSale(dto);
            DigitalSaleDTO saleDTO = domainService.toDTO(sale);

            inventoryFacadeService.updateStockBySaleItemDTO(saleDTO.getSaleItemDTOS());
            return saleDTO;
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Optional<DigitalSaleDTO>> getSaleById(Long saleId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Fetching sale with id: {}", saleId);
            return saleRepository.findById(saleId)
                .map(domainService::toDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<List<DigitalSaleDTO>> getTodaySales() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Fetching today's sales");
            return domainService.getTodaySales();
        });
    }

    @Override
    @Async
    @Cacheable("todaySalesSummary")
    public CompletableFuture<SalesSummaryDTO> getTodaySummarySales() {
        return CompletableFuture.supplyAsync(() -> {
            log.info("Fetching today's sales summary");
            return domainService.getTodaySalesSummary();
        });
    }

}
