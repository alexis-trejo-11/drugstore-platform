package microservice.sale_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee.EmployeeFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Products.ProductFacadeService;
import at.backend.drugstore.microservice.common_classes.Models.Sales.Sale;
import at.backend.drugstore.microservice.common_classes.Models.Sales.SaleStatus;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.sale_service.Mappers.SaleMapper;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import microservice.sale_service.Repository.SaleRepository;
import microservice.sale_service.Service.DomainServices.SaleDomainService;
import microservice.sale_service.Mappers.DtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class SaleServiceImpl implements SaleService {

    private final SaleMapper saleMapper;
    private final SaleRepository saleRepository;
    private final ProductFacadeService productFacadeService;
    private final EmployeeFacadeService externalEmployeeService;
    private final InventoryService inventoryService;


    @Autowired
    public SaleServiceImpl(SaleMapper saleMapper,
                           SaleRepository saleRepository,
                           ProductFacadeService productFacadeService,
                           @Qualifier("employeeFacadeService") EmployeeFacadeService externalEmployeeService,
                           InventoryService inventoryService) {
        this.saleMapper = saleMapper;
        this.saleRepository = saleRepository;
        this.productFacadeService = productFacadeService;
        this.externalEmployeeService = externalEmployeeService;
        this.inventoryService = inventoryService;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CreateSaleDTO createSale(SaleInsertDTO saleInsertDTO, Long employeeId) {
        // Bring Data From Other Services Asynchronously
        CompletableFuture<List<ProductDTO>> completableFutureProducts = productFacadeService.getProductsByIds(saleInsertDTO.getProductsId());
        CompletableFuture<Result<EmployeeDTO>> completableFutureEmployee = externalEmployeeService.getEmployeeById(employeeId);

        // Map Sale
        PhysicalSale sale = saleMapper.SaleInsertDTOtoEntity(saleInsertDTO);
        sale.setSaleStatus(SaleStatus.PAID);
        sale.setPayType(PhysicalSale.PayType.valueOf(saleInsertDTO.getPayType()));

        saleRepository.saveAndFlush(sale);

        Result<EmployeeDTO> employeeDTOResult = completableFutureEmployee.join();
        List<ProductDTO> productDTOS = completableFutureProducts.join();

        // Create Sale Items With External DATA
        if (!employeeDTOResult.isSuccess() || productDTOS == null ) { return null; }
        EmployeeDTO employeeDTO = employeeDTOResult.getData();

        sale.setEmployeeId(employeeDTO.getId());
        sale.setEmployeeName(employeeDTO.getFirstName() + " " + employeeDTO.getLastName());

        List<PhysicalSaleItem> physicalSaleItems = SaleDomainService.createSaleItems(saleInsertDTO.getItems(), productDTOS, sale);
        sale.setSaleItems(physicalSaleItems);

        saleRepository.saveAndFlush(sale);

        // Update Stock
        Result<String> stockResult = inventoryService.updateInventory(sale);
        if (!stockResult.isSuccess()) {
            throw new RuntimeException("Stock Conflict: " + stockResult.getErrorMessage());
        }

        // Map Return DTO
        return saleMapper.saleToCreateSaleDTO(sale);
    }


    @Transactional
    public Result<ProcessSaleDTO> paySale(PaySaleDTO paySaleDTO) {
        Optional<PhysicalSale> saleOptional = saleRepository.findById(paySaleDTO.getSaleId());
        if (saleOptional.isEmpty()) {
            return Result.error("Sale With Id:" + paySaleDTO.getSaleId() + " Not Found");
        }

        PhysicalSale sale = saleOptional.get();
        BigDecimal moneyProvided = paySaleDTO.getMoneyProvided();
        BigDecimal totalToPay = sale.getTotal();

        if (totalToPay.compareTo(moneyProvided) > 0) {
            BigDecimal moneyLeft = totalToPay.subtract(moneyProvided);
            return Result.error("Not Enough Money: " + moneyLeft + "$ Left");
        }

        sale.setSaleStatus(SaleStatus.PAID);
        sale.setPayType(PhysicalSale.PayType.valueOf(paySaleDTO.getPayType()));


        Result<String> stockResult = inventoryService.updateInventory(sale);
        if (!stockResult.isSuccess()) {
            return Result.error(stockResult.getErrorMessage());
        }

        ProcessSaleDTO processSaleDTO = DtoMapper.paidSaleToReturnDTO(sale, paySaleDTO);
        return Result.success(processSaleDTO);
    }

    @Override
    public Result<SaleDTO> getSaleById(Long saleId) {
        Optional<PhysicalSale> saleOptional = saleRepository.findById(saleId);
        if (saleOptional.isEmpty()) {
            return Result.error("Sale With Id:" + saleId + " Not Found");
        }

        SaleDTO saleDTO = DtoMapper.entityToDTO(saleOptional.get());
        return Result.success(saleDTO);
    }

    @Override
    @Transactional
    public List<SaleDTO> getTodaySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
        return sales.stream()
                .map(DtoMapper::entityToDTO)
                .toList();
    }

    @Override
    public SalesSummaryDTO getTodaySummarySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        List<PhysicalSale> sales = saleRepository.findPhysicalSalesByDateAndStatus(startOfDay, endOfDay, SaleStatus.PAID);
        return SaleDomainService.createSaleSummary(sales, startOfDay, endOfDay);
    }
}
