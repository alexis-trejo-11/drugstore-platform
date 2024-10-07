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
import org.checkerframework.checker.units.qual.C;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
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

    @Operation(summary = "Get client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @GetMapping("/{clientId}")
    public ResponseEntity<ResponseWrapper<ClientDTO>> getClientById(@PathVariable Long clientId) {
        Optional<ClientDTO> optionalClientDTO = clientService.getClientById(clientId);

        return optionalClientDTO.map(clientDTO -> ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(clientDTO, "Client")))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Client")));
    }

    @Operation(summary = "Add loyalty points to client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loyalty points succesfully added to user"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @PutMapping("loyalty-points/{clientId}/{points}")
    public ResponseEntity<ResponseWrapper<ClientDTO>> adjustLoyaltyPoints(@Valid @PathVariable Long clientId, @PathVariable int points) {
        log.info("adjustLoyaltyPoints -> Request add {} to client with id {} ", points, clientId);

        clientService.adjustLoyaltyPoints(clientId, points);

        return ResponseEntity.ok(ResponseWrapper.ok("User Loyalty Points", "Update"));
    }


    @Operation(summary = "Get all clients")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Clients successfully fetched"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/all")
    public ResponseEntity<ResponseWrapper<Page<ClientDTO>>> getClientsSortedByName(@RequestParam(defaultValue = "0") int page,
                                                                                   @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ClientDTO> clientDTOs = clientService.getClientsSortedByName(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok(clientDTOs, "Clients", "Retrieved by name"));
    }

    @Operation(summary = "Create a new client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Client successfully created"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<ClientDTO>> createClient(@Valid @RequestBody ClientInsertDTO clientInsertDTO) {
        ClientDTO clientDTO = clientService.createClient(clientInsertDTO);

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.ok(clientDTO, "Client", "Create"));
    }

    @Operation(summary = "Update a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> updateClient(@Valid @RequestBody ClientUpdateDTO clientUpdateDTO) {
        clientService.updateClient(clientUpdateDTO);
        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.ok("Client", "Update"));
    }

    @Operation(summary = "Delete client by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Client successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")

    })
    @DeleteMapping("/delete/{clientId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteClient(@PathVariable Long clientId) {
        boolean isClientExisting = clientService.validateExistingClient(clientId);
        if (!isClientExisting) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Client Not Found", 404));
        }

        clientService.deleteClientByID(clientId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseWrapper<>(true, null, "Client Successfully Deleted", 200));
    }

    @GetMapping("/validate/{clientId}")
    public boolean validateExisitngClient(@PathVariable Long clientId) {
        return clientService.validateExistingClient(clientId);
    }
}
