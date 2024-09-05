package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class EmployeeFacadeServiceImpl implements EmployeeFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> employeeServiceUrlProvider;

    public EmployeeFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> employeeServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.employeeServiceUrlProvider = employeeServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<EmployeeDTO>> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrlProvider.get() + "/v1/drugstore/employees/" + saleProductsDTO.getCashierId();
            log.info("Fetching employee with URL: {}", url);

            ResponseEntity<ResponseWrapper<EmployeeDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<ResponseWrapper<EmployeeDTO>>() {}
            );

            ResponseWrapper<EmployeeDTO> responseEntityBody = responseEntity.getBody();
            log.debug("Response received: {}", responseEntityBody);

            assert responseEntityBody != null;

            if (responseEntityBody.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Employee not found with ID: {}", saleProductsDTO.getCashierId());
                return Result.error(responseEntityBody.getMessage());
            }

            log.info("Employee found with ID: {}", saleProductsDTO.getCashierId());
            return Result.success(responseEntityBody.getData());
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<EmployeeDTO>> findEmployeeById(Long employeeId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrlProvider.get() + "/v1/drugstore/employees/" + employeeId;
            log.info("Fetching employee with URL: {}", url);

            ResponseEntity<ResponseWrapper<EmployeeDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<ResponseWrapper<EmployeeDTO>>() {}
            );

            ResponseWrapper<EmployeeDTO> responseEntityBody = responseEntity.getBody();
            log.debug("Response received: {}", responseEntityBody);

            assert responseEntityBody != null;

            if (responseEntityBody.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Employee not found with ID: {}", employeeId);
                return Result.error(responseEntityBody.getMessage());
            }

            log.info("Employee found with ID: {}", employeeId);
            return Result.success(responseEntityBody.getData());
        });
    }

}
