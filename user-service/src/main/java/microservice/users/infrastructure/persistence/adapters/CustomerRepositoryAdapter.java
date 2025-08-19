package microservice.users.infrastructure.persistence.adapters;

import lombok.RequiredArgsConstructor;
import microservice.users.core.domain.models.valueobjects.Email;
import microservice.users.infrastructure.persistence.mappers.CustomerMapper;
import microservice.users.infrastructure.persistence.repositories.CustomerJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CustomerRepositoryAdapter implements CustomerRepository {
    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerMapper customerMapper;

    @Override
    public Customer save(Customer customer) {
        var customerModel = customerMapper.toModel(customer);
        var savedModel = customerJpaRepository.save(customerModel);
        return customerMapper.toDomain(savedModel);
    }

    @Override
    public Optional<Customer> findById(UUID uuid) {
        return customerJpaRepository.findById(uuid)
                .map(customerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(Email email) {
        return customerJpaRepository.findByEmail(email.value())
                .map(customerMapper::toDomain);
    }

    @Override
    public List<Customer> findAll() {
        return customerJpaRepository.findAll().stream()
                .map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public void delete(UUID uuid) {
        customerJpaRepository.deleteById(uuid);
    }

    @Override
    public boolean existsById(UUID uuid) {
        return customerJpaRepository.existsById(uuid);
    }


    @Override
    public List<Customer> findByFirstNameAndLastName(String firstName, String lastName) {
        return customerJpaRepository.findByFirstNameAndLastName(firstName, lastName).stream()
                .map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public List<Customer> findByFirstNameContainingIgnoreCase(String firstName) {
        return customerJpaRepository.findByFirstNameContainingIgnoreCase(firstName).stream()
                .map(customerMapper::toDomain)
                .toList();
    }

    @Override
    public List<Customer> findByLastNameContainingIgnoreCase(String lastName) {
        return customerJpaRepository.findByLastNameContainingIgnoreCase(lastName).stream()
                .map(customerMapper::toDomain)
                .toList();
    }
}
