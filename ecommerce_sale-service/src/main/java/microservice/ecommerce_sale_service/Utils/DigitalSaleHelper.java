package microservice.ecommerce_sale_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Sale.*;
import microservice.ecommerce_sale_service.Model.DigitalSale;
import microservice.ecommerce_sale_service.Model.DigitalSaleItem;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

public class DigitalSaleHelper {

    public static SalesSummaryDTO saleToSummaryDTO(List<DigitalSale> digitalSales, LocalDateTime startTime, LocalDateTime endTime) {
        SalesSummaryDTO salesSummaryDTO = new SalesSummaryDTO();

        salesSummaryDTO.setQuantitySales(digitalSales.size());
        salesSummaryDTO.setSummaryDate(LocalDateTime.now());
        salesSummaryDTO.setStartSummary(startTime);
        salesSummaryDTO.setEndSummary(endTime);

        BigDecimal total = BigDecimal.ZERO;
        List<BigDecimal> listOfTotal = new ArrayList<>();
        Map<Long, ProductSummaryDTO> productSummaryMap = new HashMap<>();

        // Process each sale
        for (DigitalSale sale : digitalSales) {
            // Add sale total to summary total
            total = total.add(sale.getTotal());
            listOfTotal.add(sale.getTotal());

            // Process each sale item
            for (DigitalSaleItem DigitalSaleItem : sale.getSaleItems()) {
                Long productId = DigitalSaleItem.getProductId();
                productSummaryMap.merge(productId, createProductSummaryDTO(DigitalSaleItem), (existing, newSummary) -> {
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

    private static ProductSummaryDTO createProductSummaryDTO(DigitalSaleItem saleItem) {
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



    public static List<DigitalSaleItem> SaleItemToDTO(List<DigitalSaleItem> saleItems) {
        List<DigitalSaleItem> saleItemDTOS = new ArrayList<>();
        for (var saleItem : saleItems) {
            DigitalSaleItem saleItemDTO = new DigitalSaleItem();
            saleItemDTO.setProductId(saleItem.getProductId());
            saleItemDTO.setProductName(saleItem.getProductName());
            saleItemDTO.setProductUnitPrice(saleItem.getProductUnitPrice());
            saleItemDTO.setProductQuantity(saleItem.getProductQuantity());


            saleItemDTOS.add(saleItemDTO);
        }

        return saleItemDTOS;
    }
}
