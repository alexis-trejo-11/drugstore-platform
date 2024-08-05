package microservice.sale_service.Utils;

import at.backend.drugstore.microservice.common_models.Models.Sales.SaleItem;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class saleCalculator {

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
}
