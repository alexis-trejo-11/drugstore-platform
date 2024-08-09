package microservice.sale_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.CreateSaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.PaySaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.ProcessSaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleDTO;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DtoMapper {

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

    public static SaleDTO entityToDTO(PhysicalSale sale) {
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

        // Set Sale Items To DTOs
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
