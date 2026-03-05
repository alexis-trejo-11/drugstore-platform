package io.github.alexisTrejo11.drugstore.products.adapter.out.message.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alexisTrejo11.drugstore.products.core.domain.events.ProductEvent;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.SKU;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductKafkaEventPublisher implements ProductEventPublisher {

	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final ObjectMapper objectMapper;

	@Value("${app.kafka.topics.product-events:product-events}")
	private String productEventsTopic;

	@Override
	public void publishProductCreated(Product product) {
		ProductEvent event = buildProductEvent(product, "PRODUCT_CREATED");
		sendEvent(event, product.getId().toString());
	}

	@Override
	public void publishProductUpdated(Product product) {
		ProductEvent event = buildProductEvent(product, "PRODUCT_UPDATED");
		sendEvent(event, product.getId().toString());
	}

	@Override
	public void publishProductDeleted(String productId, Product product) {
		ProductEvent event = buildProductEvent(product, "PRODUCT_DELETED");
		sendEvent(event, productId);
	}

	private ProductEvent buildProductEvent(Product product, String eventType) {
		ProductEvent.ProductPayload payload = ProductEvent.ProductPayload.builder()
				.id(product.getId().toString())
				.sku(product.getSku() != null ? product.getSku().getValue() : null)
				.barcode(product.getBarcode())
				.requiresPrescription(product.isRequiresPrescription())
				.status(product.getStatus() != null ? product.getStatus().name() : null)
				.name(product.getName() != null ? product.getName().value() : null)
				.description(product.getDescription() != null ? product.getDescription().value() : null)
				.activeIngredient(product.getActiveIngredient() != null ? product.getActiveIngredient().value() : null)
				.manufacturer(product.getManufacturer() != null ? product.getManufacturer().value() : null)
				.price(product.getPrice() != null ? product.getPrice().value() : null)
				.dosage(product.getDosage() != null ? product.getDosage().value() : null)
				.administration(product.getAdministration() != null ? product.getAdministration().value() : null)
				.createdAt(product.getTimeStamps() != null ? product.getTimeStamps().getCreatedAt() : null)
				.updatedAt(product.getTimeStamps() != null ? product.getTimeStamps().getUpdatedAt() : null)
				.images(product.getImages() != null ? product.getImages().urls() : null)
				.build();

		// Add classification if exists
		if (product.getClassification() != null) {
			ProductEvent.ProductClassificationPayload classification =
					ProductEvent.ProductClassificationPayload.builder()
							.type(product.getClassification().getType() != null ? product.getClassification().getType().name() : null)
							.category(product.getClassification().getCategory() != null ? product.getClassification().getCategory().name() : null)
							.subcategory(product.getClassification().getSubcategory() != null ? product.getClassification().getSubcategory().name() : null)
							.build();
			payload.setClassification(classification);
		}

		// Add expiration range if exists
		if (product.getExpirationRange() != null) {
			ProductEvent.ExpirationRangePayload expirationRange =
					ProductEvent.ExpirationRangePayload.builder()
							.minMonths(product.getExpirationRange().getMinMonths())
							.maxMonths(product.getExpirationRange().getMaxMonths())
							.build();
			payload.setExpirationRange(expirationRange);
		}

		return ProductEvent.builder()
				.eventType(eventType != null ? ProductEvent.EventType.valueOf(eventType) : null)
				.eventId(UUID.randomUUID().toString())
				.productId(product.getId().toString())
				.timestamp(Instant.now().toEpochMilli())
				.payload(payload)
				.build();
	}

	private void sendEvent(ProductEvent event, String key) {
		try {
			log.debug("Sending product event: {} for product: {}", event.getEventType(), key);

			CompletableFuture<SendResult<String, Object>> future =
					kafkaTemplate.send(productEventsTopic, key, event);

			future.whenComplete((result, ex) -> {
				if (ex == null) {
					log.info("Product event sent successfully: topic={}, partition={}, offset={}, eventType={}, productId={}",
							result.getRecordMetadata().topic(),
							result.getRecordMetadata().partition(),
							result.getRecordMetadata().offset(),
							event.getEventType(),
							key);
				} else {
					log.error("Failed to send product event: eventType={}, productId={}, error={}",
							event.getEventType(), key, ex.getMessage(), ex);
					// Here you could implement retry logic or store failed events
				}
			});

		} catch (Exception e) {
			log.error("Error sending product event to Kafka: {}", e.getMessage(), e);
			throw new RuntimeException("Failed to publish product event", e);
		}
	}
}