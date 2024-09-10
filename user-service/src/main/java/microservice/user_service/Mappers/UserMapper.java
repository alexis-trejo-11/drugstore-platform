package microservice.user_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import microservice.user_service.Model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "joinedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastLogin", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recoveryEmail", ignore = true)
    @Mapping(target = "lastOldPassword", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User signupDtoToEntity(ClientSignUpDTO clientSignUpDTO);

    @Mapping(target = "joinedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "lastLogin", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "email", source = "companyEmail")
    @Mapping(target = "phoneNumber", source = "companyPhone")
    @Mapping(target = "recoveryEmail", ignore = true)
    @Mapping(target = "lastOldPassword", ignore = true)
    @Mapping(target = "employeeId", source = "id")
    @Mapping(target = "clientId", ignore = true)
    @Mapping(target = "roles", ignore = true)
    User employeeDtoToEntity(EmployeeDTO employeeDTO);


    @Mapping(target = "phone", source = "phoneNumber")
    ClientInsertDTO clientSignupDtoToClientInsertDTO(ClientSignUpDTO clientSignUpDTO);


    UserDTO userToDTO(User user);

    UserLoginDTO entityToLoginDTO(User user);
}
