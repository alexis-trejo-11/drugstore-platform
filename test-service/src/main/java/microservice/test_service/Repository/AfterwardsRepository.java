package microservice.test_service.Repository;

import microservice.test_service.Model.Afterward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
public interface AfterwardsRepository extends JpaRepository<Afterward, Long> {


    List<Afterward> findByClientId(Long clientId);
}