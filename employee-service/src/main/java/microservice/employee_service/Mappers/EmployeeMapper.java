package microservice.employee_service.Mappers;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeeDTO;
import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.enums.Genre;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;


@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mappings({
            @Mapping(source = "employee.position.positionName", target = "position"),
            @Mapping(source = "employee.genre", target = "genre")
    })
    EmployeeDTO employeeToDTO(Employee employee);

    @Mappings({
            @Mapping(target = "id", ignore = true),
            @Mapping(target = "companyEmail", ignore = true),
            @Mapping(target = "companyPhone", ignore = true),
            @Mapping(target = "firedAt", ignore = true),
            @Mapping(target = "position", ignore = true),
            @Mapping(target = "phoneNumber", ignore = true),
            @Mapping(target = "employeeActive", expression = "java(true)"),
            @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())"),
            @Mapping(source = "employeInsertDTO.genre", target = "genre")
    })
    Employee insertDtoToEmployee(EmployeInsertDTO employeInsertDTO);

    default String genreToString(Genre genre) {
        return genre.toString();
    }

    default Genre stringToGenre(String genre) {
        return Genre.valueOf(genre);
    }
}