package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Order.ExternalOrderService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_cart_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class OrderService {

    private final ExternalOrderService externalOrderService;

    @Autowired
    public OrderService(ExternalOrderService externalOrderService) {
        this.externalOrderService = externalOrderService;
    }

    @Async
    @Transactional
    public Result<Void> CreateOrder(List<CartItemDTO> itemsToPurchase, ClientEcommerceDataDTO clientEcommerceDataDTO, Long addressId) {
        try {
            List<AddressDTO> addressDTOS = clientEcommerceDataDTO.getAddressDTOS();
            ClientDTO clientDTO = clientEcommerceDataDTO.clientDTO;

            AddressDTO addressDTO = addressDTOS.stream()
                    .filter(address -> address.getId().equals(addressId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Address with ID " + addressId + " not found"));

            OrderInsertDTO orderInsertDTO = ModelTransformer.CartItemsToOrderInsertDTO(itemsToPurchase, clientDTO);
            orderInsertDTO.setAddressDTO(addressDTO);

            Result<Void> orderResult = externalOrderService.createOrder(orderInsertDTO);
            if (!orderResult.isSuccess()) {
                return new Result<>(false, null, orderResult.getErrorMessage(), orderResult.getStatus());
            }

            return Result.success();
        } catch (Exception e) {
            throw new RuntimeException("Can't Create Order", e);
        }
    }

    public Result<Void> ValidateAddress(ClientDTO clientDTO, AddressDTO addressDTO) {
       var isAddressCorrect = clientDTO.getId().equals(addressDTO.getClientId());
       if(!isAddressCorrect) {
           return Result.error("Invalid Address");
       }

       return Result.success();
    }
}
