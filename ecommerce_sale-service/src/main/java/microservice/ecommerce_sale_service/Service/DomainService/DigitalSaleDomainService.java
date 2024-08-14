package microservice.ecommerce_sale_service.Service.DomainService;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.*;
import at.backend.drugstore.microservice.common_classes.Models.Sales.SaleStatus;
import microservice.ecommerce_sale_service.Mappers.DigitalSaleMapper;
import microservice.ecommerce_sale_service.Mappers.DigitalSaleItemMapper;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;
import microservice.ecommerce_sale_service.Repository.DigitalSaleRepository;
import microservice.ecommerce_sale_service.Utils.DigitalSaleHelper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DigitalSaleDomainService {

    private final DigitalSaleRepository saleRepository;
    private final DigitalSaleMapper digitalSaleMapper;
    private final DigitalSaleItemMapper digitalSaleItemMapper;

    public DigitalSaleDomainService(DigitalSaleRepository saleRepository,
                                    DigitalSaleMapper digitalSaleMapper,
                                    DigitalSaleItemMapper digitalSaleItemMapper) {
        this.saleRepository = saleRepository;
        this.digitalSaleMapper = digitalSaleMapper;
        this.digitalSaleItemMapper = digitalSaleItemMapper;
    }

    public DigitalSale createSale(DigitalSaleItemInsertDTO dto) {
        DigitalSale sale = mapAndSaveDigitalSale(dto);
        List<DigitalSaleItem> saleItems = mapAndSaveSaleItems(dto.getOrderItemDTOS(), sale);
        sale.setSaleItems(saleItems);
        return saleRepository.save(sale);
    }

    public DigitalSaleDTO toDTO(DigitalSale sale) {
        DigitalSaleDTO digitalSaleDTO = digitalSaleMapper.entityToDTO(sale);
        List<SaleItemDTO> saleItemDTOS = sale.getSaleItems().stream()
                .map(digitalSaleItemMapper::entityToDTO)
                .collect(Collectors.toList());
        digitalSaleDTO.setSaleItemDTOS(saleItemDTOS);
        return digitalSaleDTO;
    }

    public List<DigitalSaleDTO> getTodaySales() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);
        return saleRepository.findDigitalSalesByDate(startOfDay, endOfDay).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public SalesSummaryDTO getTodaySalesSummary() {
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
}