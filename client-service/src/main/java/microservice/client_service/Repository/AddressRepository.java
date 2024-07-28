package microservice.client_service.Repository;

import microservice.client_service.Model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AddressRepository extends JpaRepository<Address, Long> {
     List<Address> findByClientId(long clientId);

}
