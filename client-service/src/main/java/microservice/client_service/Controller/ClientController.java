package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.ErrorResponseUtil;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import microservice.client_service.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> createClient(@RequestBody ClientInsertDTO clientInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            ClientDTO clientDTO = clientService.CreateClient(clientInsertDTO);
            ResponseWrapper<ClientDTO> responseWrapper = new ResponseWrapper<>(clientDTO, null);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseWrapper);
        }).exceptionally(ex -> {
            ResponseWrapper<ClientDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<ClientDTO>>> getClientById(@PathVariable Long id) {
        return clientService.getClientById(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<ClientDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<ClientDTO> responseWrapper = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<ClientDTO> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @GetMapping("/all")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<ClientDTO>>>> getAllClients() {
        return clientService.getAllClients()
                .thenApply(clientDTOS -> {
                    ResponseWrapper<List<ClientDTO>> responseWrapper = new ResponseWrapper<>(clientDTOS, null);
                    return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<ClientDTO>> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @DeleteMapping("/remove/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteClientById(@PathVariable Long id) {
        return clientService.deleteClient(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.NOT_FOUND, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, "Client successfully deleted");
                        return ResponseEntity.status(HttpStatus.OK).body(responseWrapper);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = ErrorResponseUtil.createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
}
