package microservice.users.core.ports.output;

import microservice.users.core.models.Customer;
import microservice.users.core.models.valueobjects.CustomerId;

import java.util.List;

public interface CustomerRepository extends CommonRepository<Customer, CustomerId> {
    List<Customer> findByFirstNameAndLastName(String firstName, String lastName);
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);
}
