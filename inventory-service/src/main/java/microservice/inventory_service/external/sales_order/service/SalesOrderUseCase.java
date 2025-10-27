package microservice.inventory_service.external.sales_order.service;

import microservice.inventory_service.external.sales_order.service.dto.SalesOrderDTO;

public interface SalesOrderUseCase {
    String createSalesOrder(SalesOrderDTO dto);
    SalesOrderDTO getSalesOrderById(String id);
    void confirmPayment(String orderId,  String paymentId);
    void fulfillOrder(String id);
    void readyToDelivery(String id);
    void cancelOrder(String id, String reason);

}
