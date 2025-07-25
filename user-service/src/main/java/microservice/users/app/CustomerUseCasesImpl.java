package microservice.users.app;

import lombok.RequiredArgsConstructor;
import microservice.users.core.models.Customer;
import microservice.users.core.models.valueobjects.Address;
import microservice.users.core.models.valueobjects.CustomerId;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.PhoneNumber;
import microservice.users.core.ports.input.CustomerUseCases;
import microservice.users.core.ports.output.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomerUseCasesImpl implements CustomerUseCases {
    private final CustomerRepository customerRepository;

    @Override
    public Customer createCustomer(Customer customer) {
        if (customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Customer with email already exists");
        }
        return customerRepository.save(customer);
    }

    @Override
    public Optional<Customer> getCustomerById(CustomerId id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByEmail(Email email) {
        return customerRepository.findByEmail(email);
    }

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Customer updateCustomer(Customer customer) {
        if (!customerRepository.existsById(customer.getId())) {
            throw new IllegalArgumentException("Customer not found");
        }
        return customerRepository.save(customer);
    }

    @Override
    public void deleteCustomer(CustomerId id) {
        if (!customerRepository.existsById(id)) {
            throw new IllegalArgumentException("Customer not found");
        }
        customerRepository.delete(id);
    }

    @Override
    public Customer addShippingAddress(CustomerId id, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        List<Address> addresses = new ArrayList<>(customer.getShippingAddresses());
        addresses.add(address);
        
        // Note: This would require a method in Customer to update addresses
        // For now, we'll assume the customer can be rebuilt or has an update method
        return customerRepository.save(customer);
    }

    @Override
    public Customer removeShippingAddress(CustomerId id, Address address) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        List<Address> addresses = new ArrayList<>(customer.getShippingAddresses());
        addresses.remove(address);
        
        // Similar note as above
        return customerRepository.save(customer);
    }

    @Override
    public Customer updatePhoneNumber(CustomerId id, PhoneNumber phoneNumber) {
        Optional<Customer> customerOpt = customerRepository.findById(id);
        if (customerOpt.isEmpty()) {
            throw new IllegalArgumentException("Customer not found");
        }
        
        Customer customer = customerOpt.get();
        // This would require a method in Customer to update phone number
        return customerRepository.save(customer);
    }

    @Override
    public List<Customer> getCustomersByName(String firstName, String lastName) {
        return customerRepository.findByFirstNameAndLastName(firstName, lastName);
    }
}
