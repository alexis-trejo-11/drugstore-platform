package microservice.order_service.external.users.application.service;


import microservice.order_service.external.users.application.command.CreateUserCommand;
import microservice.order_service.external.users.application.command.UpdateUserCommand;
import microservice.order_service.external.users.domain.entity.User;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

public interface UserService {
    User getUserByID(UserID userID);
    User getUserByEmail(String  email);
    User getUserByPhoneNumber(String phoneNumber);

    User createUser(CreateUserCommand command);
    User updateUser(UpdateUserCommand command);
    void deleteUser(UserID userID);
    void restoreUser(UserID userID);
    boolean existsByID(UserID userID);
}
