package microservice.order_service.external.users.application.service;

import microservice.order_service.external.users.application.CreateUserCommand;
import microservice.order_service.external.users.application.UpdateUserCommand;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.external.users.domain.ports.input.UserService;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl  implements UserService  {
    @Override
    public User getUserByID(UserID userID) {
        return null;
    }

    @Override
    public User getUserByEmail(String email) {
        return null;
    }

    @Override
    public User getUserByPhoneNumber(String phoneNumber) {
        return null;
    }

    @Override
    public User createUser(CreateUserCommand command) {
        return null;
    }

    @Override
    public User updateUser(UpdateUserCommand command) {
        return null;
    }

    @Override
    public void deleteUser(UserID userID) {

    }

    @Override
    public void restoreUser(UserID userID) {

    }
}
