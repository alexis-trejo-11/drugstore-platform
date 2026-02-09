package microservice.product_service.app.core.domain.model.valueobjects;


import java.time.LocalDateTime;


public class ProductTimeStamps {
    private  LocalDateTime createdAt;
    private  LocalDateTime updatedAt;
    private  LocalDateTime deletedAt;

    public void markAsUpdated() {
        updatedAt = LocalDateTime.now();
    }

    public static ProductTimeStamps now() {
        LocalDateTime now = LocalDateTime.now();
        return new ProductTimeStamps(now, now, null);
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }


	public ProductTimeStamps(LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime deletedAt) {
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.deletedAt = deletedAt;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public LocalDateTime getDeletedAt() {
		return deletedAt;
	}
}

