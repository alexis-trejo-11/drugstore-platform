package microservice.sale_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.ProductSummaryDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SalesSummaryDTO;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class SaleCreator {
    public  ProductSummaryDTO createProductSummaryDTO(PhysicalSaleItem saleItem) {
        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();
        productSummaryDTO.setProductId(saleItem.getProductId());
        productSummaryDTO.setProductName(reduceProductName(saleItem.getProductName()));
        productSummaryDTO.setQuantity(saleItem.getProductQuantity());
        productSummaryDTO.setUnitPrice(saleItem.getProductUnitPrice());

        BigDecimal total = saleCalculator.calculateTotalPerProduct(saleItem);
        productSummaryDTO.setTotal(total);

        return productSummaryDTO;
    }

    public SalesSummaryDTO createSaleSummary(List<PhysicalSale> physicalSales, LocalDateTime startTime, LocalDateTime endTime) {
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
        BigDecimal averageAmount = saleCalculator.calculateAverage(listOfTotal);
        salesSummaryDTO.setAverageAmountSale(averageAmount);

        // Set product summaries and total amount sales
        salesSummaryDTO.setProductSummaryDTOS(new ArrayList<>(productSummaryMap.values()));
        salesSummaryDTO.setTotalAmountSales(total);

        return salesSummaryDTO;
    }

    public List<PhysicalSaleItem> createSaleItems(List<SaleItemInsertDTO> saleItemInsertDTOS, List<ProductDTO> productDTOS, PhysicalSale sale) {
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



    private String reduceProductName(String productName) {
        String[] productWords = productName.split(" ");
        if (productWords.length < 2) {
            return productName;
        }
        String firstWord = productWords[0];
        String secondWord = productWords[1].substring(0, Math.min(2, productWords[1].length()));
        return firstWord + " " + secondWord + "****";
    }


}
