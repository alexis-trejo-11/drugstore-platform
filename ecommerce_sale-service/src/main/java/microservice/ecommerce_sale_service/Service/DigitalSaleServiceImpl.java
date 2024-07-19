package microservice.ecommerce_sale_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Inventory.ExternalInventoryService;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import microservice.ecommerce_sale_service.Mappers.DigitalSaleItemMapper;
import microservice.ecommerce_sale_service.Mappers.DigitalSaleMapper;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import microservice.ecommerce_sale_service.Repository.DigitalSaleRepository;
import microservice.ecommerce_sale_service.Utils.DigitalSaleHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class DigitalSaleServiceImpl implements  DigitalSaleService{

    private final DigitalSaleRepository saleRepository;
    private final DigitalSaleMapper digitalSaleMapper;
    private final DigitalSaleItemMapper digitalSaleItemMapper;
    private final ExternalInventoryService externalInventoryService;

    public DigitalSaleServiceImpl(DigitalSaleRepository saleRepository,
                                  DigitalSaleMapper digitalSaleMapper,
                                  DigitalSaleItemMapper digitalSaleItemMapper,
                                  ExternalInventoryService externalInventoryService) {
        this.saleRepository = saleRepository;
        this.digitalSaleMapper = digitalSaleMapper;
        this.digitalSaleItemMapper = digitalSaleItemMapper;
        this.externalInventoryService = externalInventoryService;
    }

    @Override
    @Async
    @Transactional
    public DigitalSaleDTO createDigitalSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        DigitalSale sale = mapAndSaveDigitalSale(digitalSaleItemInsertDTO);
        List<DigitalSaleItem> saleItems = mapAndSaveSaleItems(digitalSaleItemInsertDTO.getOrderItemDTOS(), sale);
        return createDigitalSaleDTO(sale, saleItems);
    }

    @Override
    @Async
    @Transactional
    public void updateInventory(DigitalSaleDTO digitalSaleDTO) {
        externalInventoryService.updateStockBySaleItemDTO(digitalSaleDTO.getSaleItemDTOS());
    }

    @Override
    @Async
        public Optional<DigitalSaleDTO> getSaleById(Long saleId) {
            return saleRepository.findById(saleId)
                    .map(sale -> createDigitalSaleDTO(sale, sale.getSaleItems()));
        }

    @Override
    @Async
    @Transactional
    public List<DigitalSaleDTO> getTodaySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return saleRepository.findDigitalSalesByDate(startOfDay, endOfDay).stream()
                .map(sale -> createDigitalSaleDTO(sale, sale.getSaleItems()))
                .collect(Collectors.toList());
    }

    @Override
    @Async
    @Transactional
    public SalesSummaryDTO getTodaySummarySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        List<DigitalSale> sales = saleRepository.findDigitalSalesByDate(startOfDay, endOfDay);
        return DigitalSaleHelper.saleToSummaryDTO(sales, startOfDay, endOfDay);
    }

    private DigitalSale mapAndSaveDigitalSale(DigitalSaleItemInsertDTO dto) {
        DigitalSale sale = digitalSaleMapper.insertDTOToEntity(dto.getPaymentDTO(), SaleStatus.PAID);
        return saleRepository.saveAndFlush(sale);
    }

    private List<DigitalSaleItem> mapAndSaveSaleItems(List<OrderItemDTO> orderItemDTOS, DigitalSale sale) {
        return orderItemDTOS.stream()
                .map(cartItemDTO -> {
                    DigitalSaleItem saleItem = digitalSaleItemMapper.toEntity(cartItemDTO);
                    digitalSaleItemMapper.updateDigitalSale(sale, saleItem);
                    return saleItem;
                })
                .collect(Collectors.toList());
    }

    private DigitalSaleDTO createDigitalSaleDTO(DigitalSale sale, List<DigitalSaleItem> saleItems) {
        DigitalSaleDTO digitalSaleDTO = digitalSaleMapper.entityToDTO(sale);
        List<SaleItemDTO> saleItemDTOS = saleItems.stream()
                .map(digitalSaleItemMapper::entityToDTO)
                .collect(Collectors.toList());
        digitalSaleDTO.setSaleItemDTOS(saleItemDTOS);
        return digitalSaleDTO;
    }

}
