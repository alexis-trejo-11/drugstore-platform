package microservice.users.infrastructure.persistence.repositories;

import microservice.users.infrastructure.persistence.models.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeJpaRepository extends JpaRepository<EmployeeModel, UUID> {
    Optional<EmployeeModel> findByEmail(String email);
    List<EmployeeModel> findByDepartment(String department);
    List<EmployeeModel> findByNameContainingIgnoreCase(String name);
    boolean existsByEmail(String email);
}
