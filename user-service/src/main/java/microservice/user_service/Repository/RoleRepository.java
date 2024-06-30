package microservice.user_service.Repository;

import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByName(String name);

}