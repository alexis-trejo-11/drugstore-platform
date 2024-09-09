package microservice.employee_service.Repository;

import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.Genre;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByLastName(String lastName);

    List<Employee> findByGenre(Genre genre);

    List<Employee> findByBirthDateBetween(Date startDate, Date endDate);

    Optional<Employee> findByCompanyEmail(String companyEmail);

    Optional<Employee> findByCompanyPhone(String companyEmail);

    List<Employee> findByPosition(Position position);

    List<Employee> findByIsEmployeeActive(boolean isEmployeeActive);

    List<Employee> findByHiredAtAfter(LocalDateTime hiredAt);

    List<Employee> findByFiredAtBefore(LocalDateTime firedAt);

    Page<Employee> findAllByOrderByLastName(Pageable pageable);
}
