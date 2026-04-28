package io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.grpc;

import io.alexisTrejo11.drugstore.grpc.cart.*;
import io.github.alexisTrejo11.drugstore.carts.cart.adapter.input.grpc.mapper.CartGrpcMapper;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.command.ClearCartCommand;
import io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries.GetCartByCustomerIdQuery;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.Cart;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.CustomerId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartCommandUseCase;
import io.github.alexisTrejo11.drugstore.carts.cart.core.port.in.usecase.CartQueryUseCase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.stream.Collectors;

@GrpcService
public class CartGrpcService extends CartServiceGrpc.CartServiceImplBase {

  private static final Logger log = LoggerFactory.getLogger(CartGrpcService.class);

  private final CartQueryUseCase cartQueryUseCase;
  private final CartCommandUseCase cartCommandUseCase;
  private final CartGrpcMapper mapper;

  @Autowired
  public CartGrpcService(
      CartQueryUseCase cartQueryUseCase,
      CartCommandUseCase cartCommandUseCase,
      CartGrpcMapper mapper) {
    this.cartQueryUseCase = cartQueryUseCase;
    this.cartCommandUseCase = cartCommandUseCase;
    this.mapper = mapper;
  }

  @Override
  public void getUserCart(GetUserCartRequest request, StreamObserver<CartResponse> responseObserver) {
    try {
      log.info("gRPC GetUserCart called for userId: {}", request.getUserId());

      GetCartByCustomerIdQuery query = GetCartByCustomerIdQuery.from(request.getUserId());
      Cart cart = cartQueryUseCase.getCartByCustomerId(query);

      CartResponse response = mapper.toGrpcResponse(cart);

      responseObserver.onNext(response);
      responseObserver.onCompleted();

      log.info("gRPC GetUserCart completed successfully for userId: {}", request.getUserId());

    } catch (Exception e) {
      log.error("Error in gRPC GetUserCart for userId: {}", request.getUserId(), e);
      responseObserver.onError(e);
    }
  }

  @Override
  public void clearCart(ClearCartRequest request, StreamObserver<ClearCartResponse> responseObserver) {
    try {
      log.info("gRPC ClearCart called for userId: {}, excludedProducts: {}",
          request.getUserId(),
          request.getProductIdsToExcludeList().size());

      // TODO: Ajustar el comando según tus necesidades
      CustomerId customerId = new CustomerId(request.getUserId());

      // Convertir los product IDs a excluir
      List<ProductId> excludeProductIds = null;
      if (!request.getProductIdsToExcludeList().isEmpty()) {
        excludeProductIds = request.getProductIdsToExcludeList().stream()
            .map(ProductId::new)
            .collect(Collectors.toList());
      }

      // TODO: Ajusta el reason según tu lógica de negocio
      String reason = "Cleared via gRPC"; // Ajusta esto

      ClearCartCommand command = new ClearCartCommand(customerId, reason, excludeProductIds);
      cartCommandUseCase.clearCart(command);

      ClearCartResponse response = ClearCartResponse.newBuilder()
          .setSuccess(true)
          .setMessage("Cart cleared successfully")
          .build();

      responseObserver.onNext(response);
      responseObserver.onCompleted();

      log.info("gRPC ClearCart completed successfully for userId: {}", request.getUserId());

    } catch (Exception e) {
      log.error("Error in gRPC ClearCart for userId: {}", request.getUserId(), e);

      ClearCartResponse errorResponse = ClearCartResponse.newBuilder()
          .setSuccess(false)
          .setMessage("Error clearing cart: " + e.getMessage())
          .build();

      responseObserver.onNext(errorResponse);
      responseObserver.onCompleted();
    }
  }
}
