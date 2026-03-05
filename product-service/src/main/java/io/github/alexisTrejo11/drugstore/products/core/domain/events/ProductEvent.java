package io.github.alexisTrejo11.drugstore.products.core.domain.events;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEvent {
    private EventType eventType;
    private String eventId;             // Unique event ID for idempotency
    private String productId;           
    private Long timestamp;              // Event timestamp
    
    private ProductPayload payload;

		public enum EventType {
				PRODUCT_CREATED,
				PRODUCT_UPDATED,
				PRODUCT_DELETED
		}

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductPayload {
        private String id;
        private String sku;
        private String barcode;
        private Boolean requiresPrescription;
        private String status;
        private String name;
        private String description;
        private String activeIngredient;
        private String manufacturer;
        private BigDecimal price;
        private String dosage;
        private String administration;
        
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        
        @JsonSerialize(using = LocalDateTimeSerializer.class)
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime updatedAt;
        
        private ProductClassificationPayload classification;
        private ExpirationRangePayload expirationRange;
        private List<String> images;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductClassificationPayload {
        private String type;
        private String category;
        private String subcategory;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExpirationRangePayload {
        private Integer minMonths;
        private Integer maxMonths;
    }
}