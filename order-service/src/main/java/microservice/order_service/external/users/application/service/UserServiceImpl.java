package microservice.order_service.external.users.application.service;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByEmailErr;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByIDErr;
import microservice.order_service.external.users.domain.exceptions.UserNotFoundByPhoneErr;
import microservice.order_service.external.users.infrastructure.persistence.models.UserModel;
import microservice.order_service.external.users.infrastructure.persistence.repository.JpaUserRepository;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService  {
    //TODO: Exclude deleted from queries
    private final JpaUserRepository userRepository;
    private final ModelMapper<User, UserModel> modelMapper;

    @Override
    public User getUserByID(UserID userID) {
        UserModel model = userRepository.findById(userID.value())
                .orElseThrow(() -> new UserNotFoundByIDErr(userID));

        return modelMapper.toDomain(model);
    }

    @Override
    public User getUserByEmail(String email) {
        UserModel model = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundByEmailErr(email));

        return modelMapper.toDomain(model);
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        UserModel model = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new UserNotFoundByPhoneErr(phoneNumber));

        return modelMapper.toDomain(model);
    }

    @Override
    public User createUser(CreateUserCommand command) {
        User user = command.toDomain();

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
                command.status());

        UserModel modelUpdated = userRepository.saveAndFlush(existingModel);
        return modelMapper.toDomain(modelUpdated);
    }

    @Override
    public void deleteUser(UserID userID) {
        UserModel model = userRepository.findById(userID.value())
                .orElseThrow(() -> new UserNotFoundByIDErr(userID));

        userRepository.deleteById(model.getId());
    }

    @Override
    public void restoreUser(UserID userID) {
        UserModel model = userRepository.findById(userID.value())
                .orElseThrow(() -> new UserNotFoundByIDErr(userID));

        model.restore();
        userRepository.save(model);
    }
}
