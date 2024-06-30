package microservice.ecommerce_payment_service.Automappers;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import microservice.ecommerce_payment_service.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentMethodId", source = "paymentMethod.id")
    @Mapping(target = "cardId", source = "card.id")
    @Mapping(target = "status", expression = "java(payment.getStatus().toString())")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "paymentCreatedAt", expression = "java(LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(PaymentStatus.PENDING)")
    Payment toEntity(PaymentInsertDTO paymentInsertDTO);
}
