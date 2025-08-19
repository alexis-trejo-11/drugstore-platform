package microservice.users.infrastructure.persistence.mappers;

import microservice.users.infrastructure.persistence.models.CustomerModel;
import org.springframework.stereotype.Component;

@Component
public interface CustomerMapper {
    Customer toDomain(CustomerModel customerModel);
    CustomerModel toModel(Customer customer);
}
