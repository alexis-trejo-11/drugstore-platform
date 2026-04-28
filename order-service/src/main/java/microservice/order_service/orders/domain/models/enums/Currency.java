package microservice.order_service.orders.domain.models.enums;

import lombok.Getter;

@Getter
public enum Currency {
    USD("USD"),
    EUR("EUR"),
    MXN("MXN");

    private final String code;

    Currency(String code) {
        this.code = code;
    }

    public static Currency fromCode(String code) {
        for (Currency currency : Currency.values()) {
            if (currency.getCode().equalsIgnoreCase(code)) {
                return currency;
            }
        }
        throw new IllegalArgumentException("Unsupported currency code: " + code);
    }
}
