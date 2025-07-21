package microservice.ecommerce_cart_service.app.domain.port.out.repositories;

public interface CartEventPublisher {
    void publishCartCreated(Long cartId, Long clientId);
    void publishItemAdded(Long cartId, Long productId, int quantity);
    void publishItemRemoved(Long cartId, Long productId);
    void publishItemQuantityUpdated(Long cartId, Long productId, int oldQuantity, int newQuantity);
    void publishCartCleared(Long cartId);
}

