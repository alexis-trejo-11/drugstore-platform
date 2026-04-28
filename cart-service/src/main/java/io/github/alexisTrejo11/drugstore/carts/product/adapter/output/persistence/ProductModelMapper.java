package io.github.alexisTrejo11.drugstore.carts.product.adapter.output.persistence;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductModelMapper {
	public Product toDomain(ProductModel productModel) {
		if (productModel == null) {
			return null;
		}

		return Product.reconstruct(
				new Product.ProductReconstructionParams(
						productModel.getId() != null ? ProductId.from(productModel.getId()) : null,
						productModel.getName(),
						productModel.getUnitPrice(),
						productModel.getDiscountPerUnit(),
						productModel.getDescription(),
						productModel.isAvailable(),
						productModel.getCreatedAt(),
						productModel.getUpdatedAt()
				)
		);
	}

	public List<Product> toDomains(List<ProductModel> productModels) {
		if (productModels == null) {
			return List.of();
		}

		return productModels.stream()
				.map(this::toDomain)
				.toList();
	}

	public ProductModel toJpaModel(Product product) {
		if (product == null) {
			return null;
		}

		ProductModel productModel = new ProductModel();
		productModel.setId(product.getId() != null ? product.getId().value() : null);
		productModel.setName(product.getName());
		productModel.setUnitPrice(product.getUnitPrice());
		productModel.setDescription(product.getDescription());
		productModel.setAvailable(product.isAvailable());
		productModel.setCreatedAt(product.getCreatedAt());
		productModel.setUpdatedAt(product.getUpdatedAt());

		return productModel;
	}
}
