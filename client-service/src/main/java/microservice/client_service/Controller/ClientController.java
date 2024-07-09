package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.client_service.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/api/clients")
public class ClientController {

    private final ClientService clientService;

    @Autowired
    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> createClient(@Valid @RequestBody ClientInsertDTO clientInsertDTO,
                                                               BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationErrors, "Validation Error", 400));
        }

        ClientDTO clientDTO = clientService.CreateClient(clientInsertDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, clientDTO, "Client Successfully Created.", 201));
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ApiResponse<ClientDTO>> getClientById(@PathVariable Long clientId) {
        ClientDTO clientDTO = clientService.getClientById(clientId);
        if (clientDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Client Not Found", 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, clientDTO, "Client Successfully Fetched", 200));
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<List<ClientDTO>>> getAllClients() {
        List<ClientDTO> clientDTOS = clientService.getAllClients();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, clientDTOS, "Clients Successfully Fetched", 200));
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteClientById(@PathVariable Long clientId) {
         boolean isClientDeleted = clientService.deleteClient(clientId);
        if (!isClientDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Client Not Found", 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Client Successfully Deleted", 200));
    }
}
