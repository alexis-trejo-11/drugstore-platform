package microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.repositories;

import lombok.RequiredArgsConstructor;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.AfterwardItem;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CartId;
import microservice.ecommerce_cart_service.app.domain.entities.valueobjects.CustomerId;
import microservice.ecommerce_cart_service.app.domain.port.out.repositories.AfterwardRepository;
import microservice.ecommerce_cart_service.app.infrastructure.port.out.persistence.models.AfterwardModel;
import microservice.ecommerce_cart_service.app.shared.QueryPageData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class AfterWardsRepositoryImpl implements AfterwardRepository {
    private final AftewardsJpaRepository jpaRepository;

    @Override
    public List<AfterwardItem> findAllByCartId(CartId cartId, QueryPageData pageData) {
        Sort sort = pageData.getSortBy() != null ?
                Sort.by(pageData.getSortDirection() == Sort.Direction.ASC ? Sort.Direction.ASC : Sort.Direction.DESC, pageData.getSortBy()) :
                Sort.unsorted();

        Pageable pageable = PageRequest.of(pageData.getPageNumber(), pageData.getPageSize(), sort);

        Page<AfterwardModel> afterwardModelsPage = jpaRepository.findByCartId(cartId.getValue(), pageable);

        return afterwardModelsPage.getContent().stream()
                .map(AfterwardModel::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AfterwardItem> findAllByCartId(CartId cartId) {
        List<AfterwardModel> afterwardModels = jpaRepository.findByCartId(cartId.getValue());
        return afterwardModels.stream()
                .map(AfterwardModel::toEntity)
                .collect(Collectors.toList());
    }


    @Override
    public void bulkSave(List<AfterwardItem> afterwardItems) {
        List<AfterwardModel> modelsToSave = afterwardItems.stream()
                .map(AfterwardModel::from)
                .collect(Collectors.toList());
        jpaRepository.saveAll(modelsToSave);
    }

    @Override
    public void bulkDelete(List<AfterwardItem> afterwardItems) {
        List<AfterwardModel> modelsToDelete = afterwardItems.stream()
                .map(AfterwardModel::from)
                .collect(Collectors.toList());
        jpaRepository.deleteAll(modelsToDelete);
    }
}
