package microservice.employee_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionReturnDTO;
import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Model.enums.ClassificationWorkday;
import microservice.employee_service.Model.enums.Genre;

import java.time.LocalDateTime;

public class ModelTransform {

    public static EmployeeDTO employeeToReturnDTO(Employee employee) {
        EmployeeDTO employeeDTO = new EmployeeDTO();
        employeeDTO.setId(employee.getId());
        employeeDTO.setFirstName(employee.getFirstName());
        employeeDTO.setLastName(employee.getLastName());
        employeeDTO.setGenre(employee.getGenre().toString());
        employeeDTO.setBirthDate(employee.getBirthDate());
        employeeDTO.setCompanyEmail(employee.getCompanyEmail());
        employeeDTO.setCompanyPhone(employee.getCompanyPhone());
        employeeDTO.setHiredAt(employee.getHiredAt());
        employeeDTO.setAddress(employee.getAddress());
        employeeDTO.setPosition(employee.getPosition().getPositionName());

        return employeeDTO;
    }

    public static Employee insertDtoToEmployee(EmployeInsertDTO employeInsertDTO) {
        Employee employee = new Employee();
        employee.setFirstName(employeInsertDTO.getFirstName());
        employee.setLastName(employeInsertDTO.getLastName());
        employee.setGenre(Genre.valueOf(employeInsertDTO.getGenre()));
        employee.setBirthDate(employeInsertDTO.getBirthDate());
        employee.setHiredAt(employeInsertDTO.getHiredAt());
        employee.setAddress(employeInsertDTO.getAddress());

        return employee;
    }

    public static Employee employeeReturnDTO(EmployeInsertDTO employeInsertDTO) {
        Employee employee = new Employee();
        employee.setFirstName(employeInsertDTO.getFirstName());
        employee.setLastName(employeInsertDTO.getLastName());
        employee.setGenre(Genre.valueOf(employeInsertDTO.getGenre()));
        employee.setBirthDate(employeInsertDTO.getBirthDate());
        employee.setHiredAt(employeInsertDTO.getHiredAt());
        employee.setAddress(employeInsertDTO.getAddress());

        return employee;
    }

    public static Employee employeeReturnDTO(EmployeeUpdateDTO employeeUpdateDTO) {
        Employee employee = new Employee();
        employee.setFirstName(employeeUpdateDTO.getFirstName());
        employee.setLastName(employeeUpdateDTO.getLastName());
        employee.setGenre(Genre.valueOf(employeeUpdateDTO.getGenre()));
        employee.setBirthDate(employeeUpdateDTO.getBirthDate());
        employee.setHiredAt(employeeUpdateDTO.getHiredAt());
        employee.setAddress(employeeUpdateDTO.getAddress());
        return employee;
    }

    public static PositionReturnDTO positionToReturnDTO(Position position) {
        PositionReturnDTO positionReturnDTO = new PositionReturnDTO();
        positionReturnDTO.setId(position.getId());
        positionReturnDTO.setPositionName(position.getPositionName());
        positionReturnDTO.setSalary(position.getSalary());
        positionReturnDTO.setClassificationWorkday(String.valueOf(position.getClassificationWorkday()));

        return positionReturnDTO;
    }

    public static Position insertDtoToPosition(PositionInsertDTO positionInsertDTO) {
        Position position = new Position();
        position.setPositionName(positionInsertDTO.getPositionName());
        position.setSalary(positionInsertDTO.getSalary());
        position.setClassificationWorkday(ClassificationWorkday.valueOf(positionInsertDTO.getClassificationWorkday()));
        position.setCreatedAt(LocalDateTime.now());
        position.setUpdatedAt(LocalDateTime.now());

        return  position;
    }
}
