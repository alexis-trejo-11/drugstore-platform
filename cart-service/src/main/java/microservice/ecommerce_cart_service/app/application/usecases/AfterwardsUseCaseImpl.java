package microservice.ecommerce_cart_service.app.application.usecases;

import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.application.command.afterwards.CreateAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.command.afterwards.RemoveAfterwardsCommand;
import microservice.ecommerce_cart_service.app.application.dto.AfterwardsSummary;
import microservice.ecommerce_cart_service.app.application.queries.GetCartAfterwardsQuery;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.AfterwardItem;
import microservice.ecommerce_cart_service.app.domain.port.in.usecase.AfterwardUseCase;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.AfterwardRepository;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartItemRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AfterwardsUseCaseImpl implements AfterwardUseCase  {
    private final CartItemRepository cartItemRepository;
    private final AfterwardRepository afterwardRepository;

    @Override
    public AfterwardsSummary getCartAfterwards(GetCartAfterwardsQuery query) {
        List<AfterwardItem> afterwardItems = afterwardRepository.findAllByCartId(query.getCartId(), query.getPageData());
        return createSummary(afterwardItems);
    }

    @Override
    public void moveItemToAfterwards(CreateAfterwardsCommand command) {
        List<CartItem> cartItems = cartItemRepository.listByCartId(command.getCartId());
        List<CartItem> filteredItems = cartItems.stream()
                .filter(item -> command.getProductId().contains(item.getProductId()))
                .toList();

        List<AfterwardItem> afterwardItems = filteredItems.stream()
                .map(AfterwardItem::from)
                .toList();

        afterwardRepository.bulkSave(afterwardItems);
        cartItemRepository.bulkDelete(filteredItems);
    }

    @Override
    public void removeItemFromAfterwards(RemoveAfterwardsCommand command) {
        List<AfterwardItem> afterwardItems = afterwardRepository.findAllByCartId(command.getCartId());

        List<AfterwardItem> afterwardsToRemove = afterwardItems.stream()
                .filter(item -> command.getProductIdSet().contains(item.getProductId()))
                .toList();

         List<CartItem> returnedItems = afterwardsToRemove.stream()
                 .map(CartItem::from)
                 .toList();

        cartItemRepository.bulkSave(returnedItems);
        afterwardRepository.bulkDelete(afterwardsToRemove);
    }

    public AfterwardsSummary createSummary(List<AfterwardItem> afterwardItems) {
        return new AfterwardsSummary();
    }
}
