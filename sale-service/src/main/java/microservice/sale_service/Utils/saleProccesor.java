package microservice.sale_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import microservice.sale_service.Model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class saleProccesor {

    public static CreateSaleDTO SaleToCreateSaleDTO(PhysicalSale sale) {
       CreateSaleDTO createSaleDTO = new CreateSaleDTO();
        createSaleDTO.setSaleId(sale.getId());
        createSaleDTO.setSaleDate(sale.getSaleDate());
        createSaleDTO.setSaleStatus(String.valueOf(sale.getSaleStatus()));
        createSaleDTO.setTotalAmount(sale.getTotal());

        // Employee
        createSaleDTO.setCashierId(sale.getId());
        createSaleDTO.setCashierName(sale.getEmployeeName());

        // Client
        if (sale.getClientId() != null) {
            createSaleDTO.setClientId(sale.getClientId());

        }

        //Products
        List<PhysicalSaleItem> saleItemDTOS = SaleItemToDTO(sale.getSaleItems());
        /*
        createSaleDTO.setItems(saleItemDTOS);
         */
        return createSaleDTO;
    }

    public static SaleDTO saleToDTO(PhysicalSale sale) {
        SaleDTO saleDTO = new SaleDTO();
        saleDTO.setId(sale.getId());
        saleDTO.setSaleStatus(String.valueOf(sale.getSaleStatus()));
        saleDTO.setDiscount(sale.getDiscount());
        saleDTO.setTotal(sale.getTotal());
        if (sale.getClientId() != null) {
            saleDTO.setClientId(sale.getClientId());

        }
        saleDTO.setEmployeeId(sale.getEmployeeId());
        saleDTO.setSaleDate(sale.getSaleDate());

        // Set Sale Items To DTO
        List<PhysicalSaleItem> saleItemDTOS = SaleItemToDTO(sale.getSaleItems());
        /*
        saleDTO.setSaleItemDTOS(saleItemDTOS);
         */
        return saleDTO;
    }

    public static ProcessSaleDTO paidSaleToReturnDTO(PhysicalSale sale, PaySaleDTO paySaleDTO) {
        ProcessSaleDTO processSaleDTO = new ProcessSaleDTO();
        processSaleDTO.setDiscount(sale.getDiscount());
        processSaleDTO.setAmountPaid(paySaleDTO.getMoneyProvided());
        processSaleDTO.setSubTotal(sale.getSubtotal());
        processSaleDTO.setTotal(sale.getTotal());
        processSaleDTO.setPaymentMethod(String.valueOf(sale.getPayType()));


        // Calculate Change
        BigDecimal moneyProvided = paySaleDTO.getMoneyProvided();
        BigDecimal total = sale.getTotal();
        BigDecimal change = moneyProvided.subtract(total);
        processSaleDTO.setChange(change);

        return processSaleDTO;
    }

    public static List<PhysicalSaleItem> SaleItemToDTO(List<PhysicalSaleItem> saleItems) {
        List<PhysicalSaleItem> saleItemDTOS = new ArrayList<>();
        for (var saleItem : saleItems) {
            PhysicalSaleItem saleItemDTO = new PhysicalSaleItem();
            saleItemDTO.setProductId(saleItem.getProductId());
            saleItemDTO.setProductName(saleItem.getProductName());
            saleItemDTO.setProductUnitPrice(saleItem.getProductUnitPrice());
            saleItemDTO.setProductQuantity(saleItem.getProductQuantity());


            saleItemDTOS.add(saleItemDTO);
        }

        return saleItemDTOS;
    }
}
