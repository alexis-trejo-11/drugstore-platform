package microservice.users.infrastructure.persistence.repositories;

import microservice.users.infrastructure.persistence.models.CustomerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerJpaRepository extends JpaRepository<CustomerModel, UUID> {
    Optional<CustomerModel> findByEmail(String email);
    List<CustomerModel> findByFirstNameAndLastName(String firstName, String lastName);
    List<CustomerModel> findByFirstNameContainingIgnoreCase(String firstName);
    List<CustomerModel> findByLastNameContainingIgnoreCase(String lastName);
    boolean existsByEmail(String email);
}
