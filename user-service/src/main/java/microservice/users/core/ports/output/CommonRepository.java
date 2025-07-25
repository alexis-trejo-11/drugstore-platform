package microservice.users.core.ports.output;

import microservice.users.core.models.valueobjects.Email;

import java.util.List;
import java.util.Optional;

public interface CommonRepository<T, ID> {
    T save(T entity);
    Optional<T> findById(ID id);
    Optional<T> findByEmail(Email email);
    List<T> findAll();
    void delete(ID id);
    boolean existsById(ID id);
}
