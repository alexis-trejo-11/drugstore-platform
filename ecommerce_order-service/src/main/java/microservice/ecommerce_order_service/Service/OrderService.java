package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import microservice.ecommerce_order_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    @Autowired
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }


    @Async
    @Transactional
    public CompletableFuture<Result<OrderDTO>> createOrder(OrderInsertDTO orderInsertDTO) {
       try {
           Order order = ModelTransformer.InsertDtoToOrder(orderInsertDTO);

           List<OrderItem> orderItems = ModelTransformer.MakeOrderItems(orderInsertDTO.getItems(), order);

           order.setItems(orderItems);

           orderRepository.saveAndFlush(order);

           OrderDTO orderDTO = ModelTransformer.orderToDTO(order);
           orderDTO.setAddress(orderInsertDTO.getAddressDTO());
           return CompletableFuture.completedFuture(Result.success(orderDTO));
       } catch (Exception e) {
           return CompletableFuture.failedFuture(e);
       }
    }

    @Async
    @Transactional
    public CompletionStage<Result<Void>> createShippingData(ClientDTO clientDTO, OrderDTO orderDTO) {
        try {
            if (orderDTO.getStatus() != OrderStatus.PAID) {
                return CompletableFuture.completedStage(new Result<>(false, null, "Order Not Paid!.", HttpStatus.BAD_REQUEST));
            }

            Optional<Order> optionalOrder = orderRepository.findById(orderDTO.getId());
            if (optionalOrder.isEmpty()) {
                return CompletableFuture.completedStage(new Result<>(false, null, "Order Not Found"));
            }
            Order order = optionalOrder.get();

            ShippingData shippingData = ModelTransformer.makeShippingData(orderDTO, clientDTO);
            order.setShippingData(shippingData);
            orderRepository.saveAndFlush(order);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("Can't Generate Shipping Data", e));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Order>> validateOrderPayment(boolean isOrderPaid, Long orderId) {
        try {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if (optionalOrder.isEmpty()) {
                Result<Order> result = new Result<>();
                result.setErrorMessage("Order With Id " + orderId + " Not Found");
                return CompletableFuture.completedFuture(result);
            }

            Order order = optionalOrder.get();

            if (!isOrderPaid) {
                order.setStatus(OrderStatus.PAID_FAILED);
                orderRepository.saveAndFlush(order);

                Result<Order> result = new Result<>();
                result.setStatus(HttpStatus.CONFLICT);
                result.setErrorMessage("Payment Failed");
                result.setData(order);
                return CompletableFuture.completedFuture(result);
            }

            order.setStatus(OrderStatus.PAID);
            orderRepository.saveAndFlush(order);

            Result<Order> result = new Result<>();
            result.setStatus(HttpStatus.ACCEPTED);
            result.setSuccess(true);
            result.setData(order);
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }



    @Async
    @Transactional
    public CompletableFuture<Result<OrderDTO>> getOrderById (Long orderId)  {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Order With Id:" + orderId + " Not Found"));
            }

            Order order = orderOptional.get();
            OrderDTO orderDTO = ModelTransformer.orderToDTO(order);
            return CompletableFuture.completedFuture(Result.success(orderDTO));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<List<OrderDTO>>> getOrdersByClientId(Long clientId)  {
        try {
            List<Order> orderList = orderRepository.findByClientId(clientId);

            List<OrderDTO> orderDTOS = orderList.stream()
                    .map(ModelTransformer::orderToDTO)
                    .toList();

            return CompletableFuture.completedFuture(Result.success(orderDTOS));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }


    /**
     * Asynchronously delivers an order based on orderId and isOrderDelivered flag.
     * Performs order validation, handles delivery, and updates order status.
     *
     * @param orderId The ID of the order to be delivered.
     * @param isOrderDelivered Flag indicating whether the order is successfully delivered.
     * @return CompletableFuture<Result<String>> A CompletableFuture holding the delivery result.
     */
    @Async
    @Transactional
    public CompletableFuture<Result<String>> deliveryOrder(Long orderId, boolean isOrderDelivered) {
        // Step 1: Find the order by orderId
        return findOrderById(orderId)
                .thenCompose(result -> {
                    // Step 2: Handle scenario where order is not found
                    if (!result.isSuccess()) {
                        Result<String> errorResult = new Result<>();
                        errorResult.setErrorMessage(result.getErrorMessage());
                        errorResult.setStatus(HttpStatus.NOT_FOUND);
                        return CompletableFuture.completedFuture(errorResult);
                    }

                    // Step 3: Proceed with order validation for delivery
                    Order order = result.getData();
                    return validateOrderForDelivery(order)
                            .thenCompose(validationResult -> {
                                // Step 4: Handle validation failure scenario
                                if (!validationResult.isSuccess()) {
                                    Result<String> errorResult = new Result<>();
                                    errorResult.setErrorMessage(validationResult.getErrorMessage());
                                    errorResult.setStatus(HttpStatus.BAD_REQUEST);
                                    return CompletableFuture.completedFuture(errorResult);
                                }

                                // Step 5: Handle order delivery processing
                                return handleDelivery(order, isOrderDelivered)
                                        .thenCompose(deliveryResult -> {
                                            // Step 6: Handle delivery failure scenario
                                            if (!deliveryResult.isSuccess()) {
                                                Result<String> errorResult = new Result<>();
                                                errorResult.setErrorMessage(deliveryResult.getErrorMessage());
                                                errorResult.setStatus(HttpStatus.BAD_REQUEST);
                                                return CompletableFuture.completedFuture(errorResult);
                                            }

                                            // Step 7: Update order status after successful delivery
                                            return updateOrderStatus(order, deliveryResult.getData());
                                        });
                            });
                })
                // Step 8: Handle any unexpected exceptions during the delivery process
                .exceptionally(ex -> Result.error("An unexpected error occurred while delivering the order"));
    }


    private CompletableFuture<Result<Order>> findOrderById(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                return Result.error("Order With Id:" + orderId + " Not Found");
            }
            return Result.success(orderOptional.get());
        });
    }

    private CompletableFuture<Result<Void>> validateOrderForDelivery(Order order) {
        return CompletableFuture.supplyAsync(() -> {
            if (order.getStatus() != OrderStatus.PAID) {
                return Result.error("Order With Id:" + order.getId() + " Is Not Being Shipped");
            }
            return Result.success();
        });
    }

    private CompletableFuture<Result<String>> handleDelivery(Order order, boolean isOrderDelivered) {
        return CompletableFuture.supplyAsync(() -> {
            int deliveryTries = order.getDeliveryTries() + 1;
            if (!isOrderDelivered) {
                order.setDeliveryTries(deliveryTries);
                order.setLastOrderUpdate(LocalDateTime.now());
                if (deliveryTries > 3) {
                    order.setStatus(OrderStatus.CANCELLED);
                    orderRepository.saveAndFlush(order);
                    Result result = new Result<>();
                    result.error("Order Is Cancelled.");
                    result.setStatus(HttpStatus.CONFLICT);
                } else {
                    orderRepository.saveAndFlush(order);
                    Result result = new Result<>();
                    result.setStatus(HttpStatus.OK);
                    result.error("Order Cannot Be Delivered, We Will Try Again.");
                }
            }

            Result result = new Result<>();
            result.setStatus(HttpStatus.OK);
            return Result.success("Delivered!");
        });
    }

    private CompletableFuture<Result<String>> updateOrderStatus(Order order, String status) {
        return CompletableFuture.supplyAsync(() -> {
            order.setDeliveryTries(order.getDeliveryTries() + 1);
            order.setLastOrderUpdate(LocalDateTime.now());
            order.setStatus(OrderStatus.PAID);
            orderRepository.saveAndFlush(order);
            return Result.success(status);
        });
    }

    /*
    @Async
    private CompletableFuture<Result<Void>> MakeOrderIntoSale() {
    }
     */

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> cancelOrder(Long orderId) {
        try {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            if (orderOptional.isEmpty()) {
                Result result = new Result<>();
                result.setStatus(HttpStatus.NOT_FOUND);
                result.setErrorMessage("Order With Id:" + orderId + " Not Found");
                return CompletableFuture.completedFuture(result);            }

            Order order = orderOptional.get();
            if (order.getStatus() != OrderStatus.PENDING) {
                Result result = new Result<>();
                result.setStatus(HttpStatus.CONFLICT);
                result.setErrorMessage("Order With Id:" + orderId + " Can Not Be Canceled");
                return CompletableFuture.completedFuture(result);
            }

            order.setLastOrderUpdate(LocalDateTime.now());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.saveAndFlush(order);
            return CompletableFuture.completedFuture(Result.success(null));
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}

