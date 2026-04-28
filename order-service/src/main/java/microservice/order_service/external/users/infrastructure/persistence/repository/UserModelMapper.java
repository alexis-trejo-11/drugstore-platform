package microservice.order_service.external.users.infrastructure.persistence.repository;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.infrastructure.persistence.Model.DeliveryAddressModel;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserModelMapper implements ModelMapper<User, UserModel> {
    private final ModelMapper<DeliveryAddress, DeliveryAddressModel> addressMapper;

    @Override
    public UserModel fromDomain(User user) {
        if (user == null) return null;

        return UserModel.builder()
                .id(user.getId() != null ? user.getId().value() : null)
                .name(user.getName())
                .email(user.getEmail() != null ? user.getEmail() : null)
                .phoneNumber(user.getPhoneNumber() != null ? user.getPhoneNumber() : null)
                .role(user.getRole() != null ? user.getRole() : null)
                .status(user.getStatus() != null ? user.getStatus() : null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Override
    public User toDomain(UserModel userModel) {
        if (userModel == null) return null;
        return User.builder()
                .id(userModel.getId() != null ? new UserID(userModel.getId()) : null)
                .name(userModel.getName() != null ? userModel.getName() : null)
                .email(userModel.getEmail() != null ? userModel.getEmail() : null)
                .phoneNumber(userModel.getPhoneNumber() != null ? userModel.getPhoneNumber() : null)
                .role(userModel.getRole() != null ? userModel.getRole() : null)
                .status(userModel.getStatus() != null ? userModel.getStatus() : null)
                .createdAt(userModel.getCreatedAt() != null ? userModel.getCreatedAt() : null)
                .updatedAt(userModel.getUpdatedAt() != null ? userModel.getUpdatedAt() : null)
                .addresses(userModel.getAddresses() != null ? addressMapper.toDomains(userModel.getAddresses()) : null)
                .build();

    }

    @Override
    public List<UserModel> fromDomains(List<User> users) {
        return users.stream().map(this::fromDomain).toList();
    }

    @Override
    public List<User> toDomains(List<UserModel> userModels) {
        return userModels.stream().map(this::toDomain).toList();
    }

    @Override
    public Page<User> toDomainPage(Page<UserModel> userModels) {
        return userModels.map(this::toDomain);
    }
}
