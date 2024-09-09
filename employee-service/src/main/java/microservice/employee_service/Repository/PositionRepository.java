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

public interface PositionRepository extends JpaRepository<Position, Long> {
    Page<Position> findAllByOrderByPositionNameAsc(Pageable pageable);
    Page<Position> findAllByOrderByPositionNameDesc(Pageable pageable);
}
