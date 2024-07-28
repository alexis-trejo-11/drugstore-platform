package microservice.employee_service.Repository;

import microservice.employee_service.Model.PhoneNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PhoneRepository extends JpaRepository<PhoneNumber , Long> {

    @Query(value = "SELECT * FROM phone_numbers WHERE employee_id IS NULL LIMIT 1", nativeQuery = true)
    Optional<PhoneNumber> findFirstByEmployeeIsNullNative();
}
