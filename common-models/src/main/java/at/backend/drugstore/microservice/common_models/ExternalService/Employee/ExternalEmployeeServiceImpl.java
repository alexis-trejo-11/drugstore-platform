package at.backend.drugstore.microservice.common_models.ExternalService.Employee;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalEmployeeServiceImpl implements ExternalEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalEmployeeServiceImpl.class);

    private final RestTemplate restTemplate;

    @Value("${employee.service.url}")
    private String employeeServiceUrl;

    @Autowired
    public ExternalEmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Result<EmployeeDTO> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO) {
        try {
            // Construct URL to fetch Employee Data
            String employeeUrl = employeeServiceUrl + "/" + saleProductsDTO.getCashierId();
            logger.info("EXTERNAL SERVICE -> Fetching employee data from: {}", employeeUrl);

            // Make HTTP GET request using RestTemplate
            ResponseEntity<ApiResponse<EmployeeDTO>> employeeResponseEntity = restTemplate.exchange(
                    employeeUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<EmployeeDTO>>() {});

            // Check if response status is OK (200)
            if (employeeResponseEntity.getStatusCode() != HttpStatus.OK) {
                String errorMessage = "EXTERNAL SERVICE -> Failed to validate employee for ID: " + saleProductsDTO.getCashierId();
                logger.error(errorMessage);
                return Result.error(errorMessage);
            }

            // Retrieve EmployeeDTO from ApiResponse
            ApiResponse<EmployeeDTO> employeeDTO = employeeResponseEntity.getBody();
            if (employeeDTO == null || !employeeDTO.isSuccess() || employeeDTO.getData() == null) {
                String errorMessage = "EXTERNAL SERVICE -> Employee data not found for ID: " + saleProductsDTO.getCashierId();
                logger.warn(errorMessage);
                return Result.error(errorMessage);
            }

            // Return success result with EmployeeDTO data
            return Result.success(employeeDTO.getData());
        } catch (RestClientException ex) {
            // Handle RestClientException (e.g., HttpClientErrorException, HttpServerErrorException, ResourceAccessException)
            String errorMessage = "EXTERNAL SERVICE -> Error while fetching employee data: " + ex.getMessage();
            logger.error(errorMessage, ex);
            return Result.error(errorMessage);
        } catch (Exception ex) {
            // Handle any other unexpected exceptions
            String errorMessage = "EXTERNAL SERVICE -> Unexpected error: " + ex.getMessage();
            logger.error(errorMessage, ex);
            return Result.error(errorMessage);
        }
    }

    @Override
    public Result<EmployeeDTO> findEmployeeById(Long employeeId) {
        try {
            // Construct URL to fetch Employee Data
            String employeeUrl = employeeServiceUrl + "/" + employeeId;
            logger.info("EXTERNAL SERVICE -> Fetching employee data from: {}", employeeUrl);

            // Make HTTP GET request using RestTemplate
            ResponseEntity<ApiResponse<EmployeeDTO>> employeeResponseEntity = restTemplate.exchange(
                    employeeUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<EmployeeDTO>>() {});

            // Check if response status is OK (200)
            if (employeeResponseEntity.getStatusCode() != HttpStatus.OK) {
                String errorMessage = "EXTERNAL SERVICE -> Failed to find employee for ID: " + employeeId;
                logger.error(errorMessage);
                return Result.error(errorMessage);
            }

            // Retrieve EmployeeDTO from ApiResponse
            ApiResponse<EmployeeDTO> employeeDTO = employeeResponseEntity.getBody();

            // Return success result with EmployeeDTO data
            return Result.success(employeeDTO.getData());
        } catch (RestClientException ex) {
            // Handle RestClientException (e.g., HttpClientErrorException, HttpServerErrorException, ResourceAccessException)
            String errorMessage = "EXTERNAL SERVICE -> Error while fetching employee data: " + ex.getMessage();
            logger.error(errorMessage, ex);
            return Result.error(errorMessage);
        } catch (Exception ex) {
            // Handle any other unexpected exceptions
            String errorMessage = "Unexpected error: " + ex.getMessage();
            logger.error(errorMessage, ex);
            return Result.error(errorMessage);
        }
    }
}
