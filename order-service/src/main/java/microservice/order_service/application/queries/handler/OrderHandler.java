package microservice.order_service.application.queries.handler;

import lombok.RequiredArgsConstructor;
import microservice.order_service.application.queries.mapper.OrderQueryMapper;
import microservice.order_service.domain.ports.output.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderHandler {
    private final OrderRepository orderRepository;
    private final OrderQueryMapper orderQueryMapper;
}
