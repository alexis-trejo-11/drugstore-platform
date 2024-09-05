package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.client_service.Service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/clients")
@Tag(name = "Drugstore Microservice API (Client Service)", description = "Service for managing client CRUD")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    // Completable Future Commonly User To Connect Services Each Other

    @Operation(summary = "Get client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @GetMapping("/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> getClientById(@PathVariable Long clientId) {
        log.info("Request to get client with ID: {}", clientId);

        boolean isClientExisting = clientService.validateExistingClient(clientId);
        if (!isClientExisting) {
            log.warn("getClientById -> Client not found with ID: {}", clientId);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Client", "ID")));
        }

        return clientService.getClientById(clientId)
                .thenApply(clientDTO -> {
                    log.info("Client successfully fetched with ID: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, clientDTO, "Client Successfully Fetched", 200));
                });
    }

    @Operation(summary = "Get all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients successfully fetched"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<Page<ClientDTO>>> getAllClientsSortedByName(@RequestParam(defaultValue = "0") int page,
                                                                                      @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClientDTO> clientDTOs = clientService.getClientsSortedByName(pageable);

        log.info("getAllClientsSortedByName -> Clients successfully fetched");
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, clientDTOs, "Clients Successfully Fetched", 200));
    }

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @PostMapping("/create")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> createClient(@Valid @RequestBody ClientInsertDTO clientInsertDTO) {
        log.info("Request to create a new client");
        return clientService.createClient(clientInsertDTO)
                .thenApply(clientDTO -> {
                    log.info("createClient -> Client successfully created with ID: {}", clientDTO.getId());
                    return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.ok("Client", "Create", clientDTO));
                });
    }

    @Operation(summary = "Update a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> updateClient(@Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {
        boolean isClientExisting = clientService.validateExistingClient(clientUpdateDTO.getId());
        if (!isClientExisting) {
            log.warn("updateClient -> Client not found with ID: {}", clientUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Client Not Found", 404));
        }

        clientService.updateClient(clientUpdateDTO);
        log.info("updateClient ->  Client successfully updated with ID: {}", clientUpdateDTO.getId());

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Client", "Update"));
    }

    @Operation(summary = "Delete client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @DeleteMapping("/delete/{clientID}")
    public ResponseEntity<ResponseWrapper<Void>> deleteClient(@PathVariable Long clientID) {
         boolean isClientExisting = clientService.validateExistingClient(clientID);
            if (!isClientExisting) {
                log.warn("deleteClient -> Client not found with ID: {}", clientID);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Client Not Found", 404));
            }

            clientService.deleteClientByID(clientID);
            log.info("deleteClient -> Client successfully deleted with ID: {}", clientID);

            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, null, "Client Successfully Deleted", 200));
    }
}
