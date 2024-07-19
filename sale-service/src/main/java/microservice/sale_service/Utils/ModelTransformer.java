package microservice.sale_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import microservice.sale_service.Model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class ModelTransformer {

    public static PhysicalSale MakeSale(EmployeeDTO employeeDTO) {
        PhysicalSale sale = new PhysicalSale();
        // Date
        sale.setSaleDate(LocalDateTime.now());

        //Employee
        sale.setEmployeeId(employeeDTO.getId());
        sale.setEmployeeName(employeeDTO.getFirstName() + " " + employeeDTO.getLastName());

        return sale;
    }


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

    public static SalesSummaryDTO saleToSummaryDTO(List<PhysicalSale> physicalSales, LocalDateTime startTime, LocalDateTime endTime) {
        SalesSummaryDTO salesSummaryDTO = new SalesSummaryDTO();

        // Set basic summary information
        salesSummaryDTO.setQuantitySales(physicalSales.size());
        salesSummaryDTO.setSummaryDate(LocalDateTime.now());
        salesSummaryDTO.setStartSummary(startTime);
        salesSummaryDTO.setEndSummary(endTime);

        BigDecimal total = BigDecimal.ZERO;
        List<BigDecimal> listOfTotal = new ArrayList<>();
        Map<Long, ProductSummaryDTO> productSummaryMap = new HashMap<>();

        // Process each sale
        for (PhysicalSale sale : physicalSales) {
            // Add sale total to summary total
            total = total.add(sale.getTotal());
            listOfTotal.add(sale.getTotal());

            // Process each sale item
            for (PhysicalSaleItem PhysicalSaleItem : sale.getSaleItems()) {
                Long productId = PhysicalSaleItem.getProductId();
                productSummaryMap.merge(productId, createProductSummaryDTO(PhysicalSaleItem), (existing, newSummary) -> {
                    existing.setQuantity(existing.getQuantity() + newSummary.getQuantity());
                    BigDecimal newTotal = existing.getTotal().add(newSummary.getTotal());
                    existing.setTotal(newTotal);
                    return existing;
                });
            }
        }

        // Calculate the average amount
        BigDecimal averageAmount = calculateAverage(listOfTotal);
        salesSummaryDTO.setAverageAmountSale(averageAmount);

        // Set product summaries and total amount sales
        salesSummaryDTO.setProductSummaryDTOS(new ArrayList<>(productSummaryMap.values()));
        salesSummaryDTO.setTotalAmountSales(total);

        return salesSummaryDTO;
    }

    private static ProductSummaryDTO createProductSummaryDTO(PhysicalSaleItem saleItem) {
        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();
        productSummaryDTO.setProductId(saleItem.getProductId());
        productSummaryDTO.setProductName(reduceProductName(saleItem.getProductName()));
        productSummaryDTO.setQuantity(saleItem.getProductQuantity());
        productSummaryDTO.setUnitPrice(saleItem.getProductUnitPrice());

        // Calc Total
        BigDecimal unitPrice = saleItem.getProductUnitPrice();
        int quantity = saleItem.getProductQuantity();
        BigDecimal total = unitPrice.multiply(new BigDecimal(quantity));
        productSummaryDTO.setTotal(total);

        return productSummaryDTO;
    }

    public static List<PhysicalSaleItem> MakeSaleItems(List<SaleItemInsertDTO> saleItemInsertDTOS, List<ProductDTO> productDTOS, PhysicalSale sale) {
        List<PhysicalSaleItem> saleItems = new ArrayList<>();
        for (var saleItemInsertDTO : saleItemInsertDTOS) {
            // Get Product
            Optional<ProductDTO> productOptional = productDTOS.stream()
                    .filter(product -> product.getId().equals(saleItemInsertDTO.getProductId()))
                    .findFirst();
            if (productOptional.isEmpty()) {
                // Handle case where product is not found
                continue;
            }
            ProductDTO productDTO = productOptional.get();

            // Create Sale Item
            PhysicalSaleItem saleItem = new PhysicalSaleItem();

            saleItem.setProductId(productDTO.getId());
            saleItem.setProductName(productOptional.get().getName());
            saleItem.setProductUnitPrice(productDTO.getPrice());
            saleItem.setPhysicalSale(sale);

            saleItem.setCreatedAt(LocalDateTime.now());

            saleItem.setProductQuantity(saleItemInsertDTO.getQuantity());

            saleItems.add(saleItem);
        }
        return saleItems;
    }

    private static String reduceProductName(String productName) {
        String[] productWords = productName.split(" ");
        if (productWords.length < 2) {
            return productName;
        }
        String firstWord = productWords[0];
        String secondWord = productWords[1].substring(0, Math.min(2, productWords[1].length()));
        return firstWord + " " + secondWord + "****";
    }

    private static BigDecimal calculateAverage(List<BigDecimal> listOfTotal) {
        if (listOfTotal.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = listOfTotal.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(listOfTotal.size()), BigDecimal.ROUND_HALF_UP);
    }



    public static ProcessSaleDTO PaidSaleToReturnDTO(PhysicalSale sale, PaySaleDTO paySaleDTO) {
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
