package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
    public CompletableFuture<Result<EmployeeDTO>> getEmployeeById(Long employeeId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrlProvider.get() + "/v1/drugstore/employees/" + employeeId;
            log.info("getEmployeeById -> Fetching employee with URL: {}", url);

            ResponseEntity<ResponseWrapper<EmployeeDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    new ParameterizedTypeReference<ResponseWrapper<EmployeeDTO>>() {}
            );

            ResponseWrapper<EmployeeDTO> responseEntityBody = responseEntity.getBody();
            log.debug("getEmployeeById -> Response received: {}", responseEntityBody);

            if (responseEntityBody.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("getEmployeeById -> Employee not found with ID: {}", employeeId);
                return Result.error(responseEntityBody.getMessage());
            }

            log.info("getEmployeeById -> Employee found with ID: {}", employeeId);
            return Result.success(responseEntityBody.getData());
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<EmployeeDTO>> getEmployeeForUserCreation(RequestEmployeeUser requestEmployeeUser) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrlProvider.get() + "/v1/drugstore/employees/search" ;
            log.info("getEmployeeForUserCreation -> Fetching employee with URL: {}", url);
            log.info("getEmployeeForUserCreation -> Fetching employee with DATA: {}", requestEmployeeUser);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<RequestEmployeeUser> requestEntity = new HttpEntity<>(requestEmployeeUser, headers);

            ResponseEntity<ResponseWrapper<EmployeeDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<EmployeeDTO>>() {}
            );

            ResponseWrapper<EmployeeDTO> responseEntityBody = responseEntity.getBody();
            log.debug("getEmployeeForUserCreation -> Response received: {}", responseEntityBody);


            if (responseEntityBody.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                log.warn("Employee not found with values: {}", requestEmployeeUser);
                return Result.error(responseEntityBody.getMessage());
            }

            log.info("Employee found with value: {}", requestEmployeeUser);
            return Result.success(responseEntityBody.getData());
        });
    }

    @Override
    public CompletableFuture<Boolean> validateExistingEmployee(Long employeeId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = employeeServiceUrlProvider.get() + "/v1/drugstore/employees/validate/" + employeeId;
            ResponseEntity<Boolean> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    Boolean.class
            );
            return response.getBody();
        });
    }

}
