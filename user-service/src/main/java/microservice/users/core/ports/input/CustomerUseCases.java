package microservice.users.core.ports.input;

import microservice.users.core.models.Customer;
import microservice.users.core.models.valueobjects.Address;
import microservice.users.core.models.valueobjects.CustomerId;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.PhoneNumber;

import java.util.List;
import java.util.Optional;

public interface CustomerUseCases {
    Customer createCustomer(Customer customer);
    Optional<Customer> getCustomerById(CustomerId id);
    Optional<Customer> getCustomerByEmail(Email email);
    List<Customer> getAllCustomers();
    Customer updateCustomer(Customer customer);
    void deleteCustomer(CustomerId id);
    Customer addShippingAddress(CustomerId id, Address address);
    Customer removeShippingAddress(CustomerId id, Address address);
    Customer updatePhoneNumber(CustomerId id, PhoneNumber phoneNumber);
    List<Customer> getCustomersByName(String firstName, String lastName);
}
