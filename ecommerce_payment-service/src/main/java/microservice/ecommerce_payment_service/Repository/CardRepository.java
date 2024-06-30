package microservice.ecommerce_payment_service.Repository;

import microservice.ecommerce_payment_service.Model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByClientId(Long clientId);
}