package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
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
    public CompletionStage<Result<Void>> CreateOrder(List<CartItemDTO> itemsToPurchase, AddressDTO addressDTO, ClientDTO clientDTO) {
        try {
            OrderInsertDTO orderInsertDTO = ModelTransformer.CartItemsToOrderInsertDTO(itemsToPurchase, clientDTO);
            orderInsertDTO.setAddressDTO(addressDTO);

            Result<Void> orderResult = externalOrderService.createOrder(orderInsertDTO);
            if (!orderResult.isSuccess()) {
                return CompletableFuture.completedFuture(new Result<>(false, null, orderResult.getErrorMessage(), orderResult.getStatus()));
            }

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("Can't Create Order", e));
        }
    }

    public boolean ValidateAddress(ClientDTO clientDTO, AddressDTO addressDTO) {
        return clientDTO.getId().equals(addressDTO.getClientId());
    }
}
