package at.backend.drugstore.microservice.common_models.ExternalService.Employee;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalEmployeeServiceImpl implements ExternalEmployeeService {

    private final RestTemplate restTemplate;

    @Value("${employee.service.url}")
    private String employeeServiceUrl;

    @Autowired
    public ExternalEmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }


    @Override
    public Result<EmployeeDTO> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO) {
        // Fetch Employee Data
        String employeeUrl = employeeServiceUrl + "/" + saleProductsDTO.getCashierId();
        ResponseEntity<EmployeeDTO> employeeResponseEntity = restTemplate.exchange(
                employeeUrl,
                HttpMethod.GET,
                null,
                EmployeeDTO.class);

        if (employeeResponseEntity.getStatusCode() != HttpStatus.OK) {
            return Result.error("Failed to validate employee for ID: " + saleProductsDTO.getCashierId());
        }

        EmployeeDTO employeeDTO = employeeResponseEntity.getBody();
        return Result.success(employeeDTO);
    }

    @Override
    public Result<EmployeeDTO> findEmployeeById(Long employeeId) {
        // Fetch Employee Data
        String employeeUrl = employeeServiceUrl + "/" + employeeId;
        ResponseEntity<EmployeeDTO> employeeResponseEntity = restTemplate.exchange(
                employeeUrl,
                HttpMethod.GET,
                null,
                EmployeeDTO.class);

        if (employeeResponseEntity.getStatusCode() != HttpStatus.OK) {
            return Result.error("Failed to validate employee for ID: " + employeeId);
        }
        EmployeeDTO employeeDTO = employeeResponseEntity.getBody();

        return Result.success(employeeDTO);
    }
}
