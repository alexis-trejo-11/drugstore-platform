package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Inventory.InventoryFacadeService;
import at.backend.drugstore.microservice.common_classes.Models.Sales.Sale;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Repository.DigitalSaleRepository;
import microservice.ecommerce_sale_service.Service.DomainService.DigitalSaleDomainService;
import microservice.ecommerce_sale_service.Utils.DigitalSaleValidator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DigitalSaleServiceImpl implements DigitalSaleService {

    private final DigitalSaleRepository saleRepository;
    private final DigitalSaleValidator digitalSaleValidator;
    private final DigitalSaleDomainService digitalSaleDomainService;
    private final InventoryFacadeService  inventoryFacadeService;

    public DigitalSaleServiceImpl(DigitalSaleRepository saleRepository,
                                  DigitalSaleValidator digitalSaleValidator,
                                  DigitalSaleDomainService digitalSaleDomainService,
                                  InventoryFacadeService inventoryFacadeService) {
        this.saleRepository = saleRepository;
        this.digitalSaleValidator = digitalSaleValidator;
        this.digitalSaleDomainService = digitalSaleDomainService;
        this.inventoryFacadeService = inventoryFacadeService;
    }

    @Override
    public DigitalSaleDTO createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
            digitalSaleValidator.validateSaleCreation(digitalSaleItemInsertDTO);

            DigitalSale sale = digitalSaleDomainService.createSale(digitalSaleItemInsertDTO);
            DigitalSaleDTO saleDTO = digitalSaleDomainService.entityToDTO(sale);

            inventoryFacadeService.updateStockBySaleItemDTO(saleDTO.getSaleItemDTOS());
            return saleDTO;
    }

    @Override
    @Cacheable(value = "saleById", key = "#saleId")
    public DigitalSaleDTO getSaleById(Long saleId) {
        DigitalSale sale = saleRepository.findById(saleId).orElse(null);
        if (sale == null) { return null;}

        return digitalSaleDomainService.entityToDTO(sale);
    }

    @Override
    @Cacheable("todaySales")
    public Page<DigitalSaleDTO> getTodaySales(Pageable pageable) {
            return digitalSaleDomainService.getTodaySales(pageable);
    }

    @Override
    @Cacheable("todaySalesSummary")
    public SalesSummaryDTO getTodaySummarySales(Pageable pageable) {
        return digitalSaleDomainService.getTodaySalesSummary(pageable);
    }

    @Override
    public boolean validateExistingSale(Long saleId) {
       return saleRepository.findById(saleId).isPresent();
    }

}
