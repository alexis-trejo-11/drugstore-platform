package microservice.order_service.external.users.application.service;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.domain.exceptions.UserCredentialsConflict;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByEmailErr;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByIDErr;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByPhoneErr;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.external.users.infrastructure.persistence.repository.JpaUserRepository;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    //TODO: Exclude deleted from queries
    private final JpaUserRepository userRepository;
    private final ModelMapper<User, UserModel> modelMapper;

    @Override
    public User getUserByID(UserID userID) {
        UserModel model = userRepository.findActiveByIdWithAddress(userID.value())
                .orElseThrow(() -> new UserNotFoundByIDErr(userID));

        return modelMapper.toDomain(model);
    }

    @Override
    public User getUserByEmail(String email) {
        UserModel model = userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(() -> new UserNotFoundByEmailErr(email));

        return modelMapper.toDomain(model);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        UserModel model = userRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber)
                .orElseThrow(() -> new UserNotFoundByPhoneErr(phoneNumber));

        return modelMapper.toDomain(model);
    }

    @Override
    public User createUser(CreateUserCommand command) {
        validateUserCredentials(command.email(), command.phoneNumber());

        User user = User.create(
                command.name(),
                command.email(),
                command.phoneNumber(),
                command.role(),
                command.status()
        );

        UserModel userModel = modelMapper.fromDomain(user);
        UserModel userCreated = userRepository.saveAndFlush(userModel);

        return modelMapper.toDomain(userCreated);
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        UserModel existingModel = userRepository.findById(command.id().value())
                .orElseThrow(() -> new UserNotFoundByIDErr(command.id()));

        existingModel.update(
                command.name(),
                command.email(),
                command.phoneNumber(),
                command.role(),
                command.status()
        );
        UserModel modelUpdated = userRepository.saveAndFlush(existingModel);

        return modelMapper.toDomain(modelUpdated);
    }

    @Override
    public void deleteUser(UserID userID) {
        if (!userRepository.existsById(userID.value())) {
           throw new UserNotFoundByIDErr(userID);
        }

        userRepository.deleteById(userID.value());
    }

    @Override
    public void restoreUser(UserID userID) {
        UserModel model = userRepository.findById(userID.value())
                .orElseThrow(() -> new UserNotFoundByIDErr(userID));

        model.restore();
        userRepository.save(model);
    }

    @Override
    public boolean existsByID(UserID userID) {
        return userRepository.existsById(userID.value());
    }

    public void validateUserCredentials(String email, String phoneNumber) {
        if (email != null) {
            if (userRepository.existsByEmail(email)) {
                throw new UserCredentialsConflict("email", email);
            }
        }

        if (phoneNumber != null) {
            if (userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new UserCredentialsConflict("phone number", phoneNumber);
            }
        }
    }
}