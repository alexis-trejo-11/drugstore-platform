package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.client_service.Service.ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/clients")
@Tag(name = "Drugstore Microservice API (Client Service)", description = "Service for managing client CRUD")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created")
    })
    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> createClient(@Valid @RequestBody ClientInsertDTO clientInsertDTO) {
        log.info("Request to create a new client");
        return clientService.createClient(clientInsertDTO)
                .thenApply(clientDTO -> {
                    log.info("Client successfully created with ID: {}", clientDTO.getId());
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseWrapper<>(true, clientDTO, "Client Successfully Created.", 201));
                });
    }

    @Operation(summary = "Get client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> getClientById(@PathVariable Long clientId) {
        log.info("Request to get client with ID: {}", clientId);
        return clientService.getClientById(clientId)
                .thenApply(clientDTO -> {
                    if (clientDTO == null) {
                        log.warn("Client not found with ID: {}", clientId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client Not Found", 404));
                    }
                    log.info("Client successfully fetched with ID: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, clientDTO, "Client Successfully Fetched", 200));
                });
    }

    @Operation(summary = "Get all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients successfully fetched")
    })
    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ClientDTO>>>> getAllClients() {
        log.info("Request to get all clients");
        return clientService.getAllClients()
                .thenApply(clientDTOS -> {
                    log.info("Clients successfully fetched");
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, clientDTOS, "Clients Successfully Fetched", 200));
                });
    }

    @Operation(summary = "Delete client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/remove/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteClientById(@PathVariable Long clientId) {
        log.info("Request to delete client with ID: {}", clientId);
        return clientService.deleteClient(clientId)
                .thenApply(isClientDeleted -> {
                    if (!isClientDeleted) {
                        log.warn("Client not found with ID: {}", clientId);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, "Client Not Found", 404));
                    }
                    log.info("Client successfully deleted with ID: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, "Client Successfully Deleted", 200));
                });
    }
}
