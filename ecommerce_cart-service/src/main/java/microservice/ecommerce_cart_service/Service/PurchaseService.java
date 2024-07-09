package microservice.ecommerce_cart_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.ClientEcommerceDataDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface PurchaseService {
    Result<ClientEcommerceDataDTO> prepareClientData(Long clientId);
    void processPurchase(ClientEcommerceDataDTO clientEcommerceDataDTO, Long cardId, List<CartItemDTO> itemsToPurchase, Long addressId);
}
