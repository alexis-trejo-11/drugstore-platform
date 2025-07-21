package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.CartItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.ProductId;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.CartItemRepository;
import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.CartItemModel;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemRepository {
    private final CartItemJpaRepository jpaRepository;

    @Override
    public void bulkSave(List<CartItem> cartItems) {
        List<CartItemModel> modelsToSave = cartItems.stream()
                .map(CartItemModel::fromEntity)
                .collect(Collectors.toList());
        jpaRepository.saveAll(modelsToSave);
    }

    @Override
    public void bulkDelete(List<CartItem> cartItems) {
        List<CartItemModel> modelsToDelete = cartItems.stream()
                .map(CartItemModel::fromEntity)
                .collect(Collectors.toList());
        jpaRepository.deleteAll(modelsToDelete);
    }

    @Override
    public List<CartItem> listByCartId(CartId cartId, QueryPageData pageData) {
        Sort sort = pageData.getSortBy() != null ?
                Sort.by(pageData.getSortDirection() == Sort.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, pageData.getSortBy()) :
                Sort.unsorted();

        Pageable pageable = PageRequest.of(pageData.getPageNumber(), pageData.getPageSize(), sort);

        Page<CartItemModel> cartItemModelsPage = jpaRepository.findByCartId(cartId.getValue(), pageable);

        return cartItemModelsPage.getContent().stream()
                .map(CartItemModel::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItem> listByCartId(CartId cartId) {
        List<CartItemModel> cartItemModels = jpaRepository.findByCartId(cartId.getValue());
        return cartItemModels.stream()
                .map(CartItemModel::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<CartItem> listByCartIdAndProductIdIn(CartId cartId, Set<ProductId> productIds) {
        List<UUID> productIdValues = productIds.stream()
                .map(ProductId::getValue)
                .collect(Collectors.toList());

        List<CartItemModel> cartItemModels = jpaRepository.findByCartIdAndProductIdIn(cartId.getValue(), productIdValues);
        return cartItemModels.stream()
                .map(CartItemModel::toEntity)
                .collect(Collectors.toList());
    }
}
