package microservice.ecommerce_payment_service.Automappers;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import microservice.ecommerce_payment_service.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "paymentMethod", source = "payment.paymentMethod", qualifiedByName = "mapPaymentMethodToString")
    @Mapping(target = "cardId", source = "payment.card.id")
    @Mapping(target = "paymentStatus", source = "payment.status", qualifiedByName = "mapStatusToString")
    PaymentDTO toDto(Payment payment);

    @Mapping(target = "status", expression = "java(microservice.ecommerce_payment_service.Model.Payment.PaymentStatus.PENDING)")
    @Mapping(target = "paymentMethod", source = "paymentInsertDTO.paymentMethod", qualifiedByName = "mapStringToPaymentMethod")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "paymentDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "card", ignore = true)
    Payment toEntity(PaymentInsertDTO paymentInsertDTO);

    @Named("mapStatusToString")
    static String mapStatusToString(Payment.PaymentStatus status) {
        return status.name();
    }

    @Named("mapPaymentMethodToString")
    static String mapPaymentMethodToString(Payment.PaymentMethod paymentMethod) {
        return paymentMethod.name();
    }

    @Named("mapStringToPaymentMethod")
    static Payment.PaymentMethod mapStringToPaymentMethod(String paymentMethod) {
        return Payment.PaymentMethod.valueOf(paymentMethod.toUpperCase());
    }
}
