package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ExternalEmployeeServiceImpl implements ExternalEmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(ExternalEmployeeServiceImpl.class);
    private final RestTemplate restTemplate;

    private final String employeeServiceUrl = "http://employee-service:8082";

    public ExternalEmployeeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<EmployeeDTO>> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = employeeServiceUrl + "/v1/api/employees/" + saleProductsDTO.getCashierId();
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        ResponseWrapper.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<EmployeeDTO> employeeDTO = (ResponseWrapper<EmployeeDTO>) responseEntity.getBody();
                    if (employeeDTO != null && employeeDTO.isSuccess() && employeeDTO.getData() != null) {
                        return Result.success(employeeDTO.getData());
                    } else {
                        String errorMessage = "EXTERNAL SERVICE: Employee data not found for ID: " + saleProductsDTO.getCashierId();
                        logger.warn(errorMessage);
                        return Result.error(errorMessage);
                    }
                } else {
                    String errorMessage = "EXTERNAL SERVICE: Failed to fetch employee data, status code: " + responseEntity.getStatusCode();
                    logger.warn(errorMessage);
                    return Result.error(errorMessage);
                }
            } catch (Exception ex) {
                String errorMessage = "EXTERNAL SERVICE: Unexpected error: " + ex.getMessage();
                logger.error(errorMessage, ex);
                return Result.error(errorMessage);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<EmployeeDTO>> findEmployeeById(Long employeeId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrl + "/v1/api/employees/" + employeeId;
            logger.info("Fetching employee data from URL: {}", url);

            ResponseWrapper<EmployeeDTO> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseWrapper<EmployeeDTO>>() {}
            ).getBody();
            assert response != null;

            // Not Found Employee (404)
            if (response.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return Result.error(response.getMessage());
            }

            // Found (200)
            logger.info("Received response with status code: {}", response.getStatusCode());
            return Result.success(response.getData());
        });
    }

}
