package microservice.sale_service.Service.DomainServices;

import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.ProductSummaryDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SalesSummaryDTO;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleItem;
import at.backend.drugstore.microservice.common_models.Models.Sales.SaleStatus;
import microservice.sale_service.Model.PhysicalSale;
import microservice.sale_service.Model.PhysicalSaleItem;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SaleDomainService {

    public static BigDecimal calculateAverage(List<BigDecimal> listOfTotal) {
        if (listOfTotal.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sum = listOfTotal.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(listOfTotal.size()), RoundingMode.HALF_UP);
    }

    public static BigDecimal calculateTotalPerProduct(SaleItem saleItem) {
        BigDecimal unitPrice = saleItem.getProductUnitPrice();
        int quantity = saleItem.getProductQuantity();
        return unitPrice.multiply(new BigDecimal(quantity));

    }

    public static SalesSummaryDTO createSaleSummary(List<PhysicalSale> physicalSales, LocalDateTime startTime, LocalDateTime endTime) {
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
            for (microservice.sale_service.Model.PhysicalSaleItem PhysicalSaleItem : sale.getSaleItems()) {
                Long productId = PhysicalSaleItem.getProductId();
                productSummaryMap.merge(productId, createProductSummary(PhysicalSaleItem), (existing, newSummary) -> {
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

    public static List<PhysicalSaleItem> createSaleItems(List<SaleItemInsertDTO> saleItemInsertDTOS, List<ProductDTO> productDTOS, PhysicalSale sale) {
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

    public static ProductSummaryDTO createProductSummary(PhysicalSaleItem saleItem) {
        ProductSummaryDTO productSummaryDTO = new ProductSummaryDTO();
        productSummaryDTO.setProductId(saleItem.getProductId());
        productSummaryDTO.setProductName(reduceProductName(saleItem.getProductName()));
        productSummaryDTO.setQuantity(saleItem.getProductQuantity());
        productSummaryDTO.setUnitPrice(saleItem.getProductUnitPrice());

        BigDecimal total = calculateTotalPerProduct(saleItem);
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

    public static PhysicalSale calculateTotal(PhysicalSale sale) {
        BigDecimal total = BigDecimal.ZERO;

        for (var saleItem : sale.getSaleItems()) {
            BigDecimal itemPrice = saleItem.getProductUnitPrice();
            int quantity = saleItem.getProductQuantity();
            total = total.add(itemPrice.multiply(BigDecimal.valueOf(quantity)));
        }

        sale.setSubtotal(total);
        sale.setTotal(total);
        sale.setDiscount(BigDecimal.ZERO);
        sale.setSaleStatus(SaleStatus.PAID);

        return sale;
    }
}
