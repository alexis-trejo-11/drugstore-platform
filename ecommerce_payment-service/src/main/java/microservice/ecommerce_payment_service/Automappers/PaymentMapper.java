package microservice.ecommerce_payment_service.Automappers;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import microservice.ecommerce_payment_service.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentMethodId", source = "payment.paymentMethod.id")
    @Mapping(target = "cardId", source = "payment.card.id")
    @Mapping(target = "status", source = "payment.status.toString()")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "paymentCreatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "status", expression = "java(microservice.ecommerce_payment_service.Model.Payment.PaymentStatus.PENDING)")
    Payment toEntity(PaymentInsertDTO paymentInsertDTO);

    void updateFromDto(PaymentInsertDTO paymentInsertDTO, @MappingTarget Payment payment);
}