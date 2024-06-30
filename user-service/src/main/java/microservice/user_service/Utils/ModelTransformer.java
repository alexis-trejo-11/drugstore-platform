package microservice.user_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import microservice.user_service.Model.User;

import java.time.LocalDateTime;

public class ModelTransformer {

    public static User ClientSignupDtoToUser(ClientSignUpDTO clientSignUpDTO) {
        User user = new User();
        user.setJoinedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setLastLogin(LocalDateTime.now());

        if (clientSignUpDTO.getEmail() != null) {
        user.setEmail(clientSignUpDTO.getEmail());
        }

        if (clientSignUpDTO.getPhoneNumber() != null) {
            user.setPhoneNumber(clientSignUpDTO.getPhoneNumber());
        }

        return user;
    }

    public static ClientInsertDTO ClientSignupDtoToClientInsertDTO(ClientSignUpDTO clientSignUpDTO) {
        ClientInsertDTO clientInsertDTO = new ClientInsertDTO();
        clientInsertDTO.setFirstName(clientSignUpDTO.getFirstName());
        clientInsertDTO.setLastName(clientSignUpDTO.getLastName());
        clientInsertDTO.setBirthdate(clientSignUpDTO.getBirthdate());

        if (clientSignUpDTO.getPhoneNumber() != null) {
            clientInsertDTO.setPhone(clientSignUpDTO.getPhoneNumber());
        }

        return clientInsertDTO;
    }

    public static UserDTO userToDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoneNumber(user.getPhoneNumber());

        if (user.getClientId() != null) {
        userDTO.setClientId(userDTO.getClientId());
        }

        if (user.getClientId() != null) {
            userDTO.setClientId(userDTO.getClientId());
        }

        return userDTO;
    }

    public static UserLoginDTO userToLoginDTO(User user) {
        UserLoginDTO userLoginDto = new UserLoginDTO();
        userLoginDto.setId(user.getId());
        userLoginDto.setEmail(user.getEmail());
        userLoginDto.setPhoneNumber(user.getPhoneNumber());
        userLoginDto.setHashedPassword(user.getPassword());

        if (user.getClientId() != null) {
            userLoginDto.setClientId(userLoginDto.getClientId());
        }

        if (user.getClientId() != null) {
            userLoginDto.setClientId(userLoginDto.getClientId());
        }

        return userLoginDto;
    }
}
