package microservice.ecommerce_payment_service.Automappers;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Model.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", imports = {LocalDateTime.class, Payment.PaymentStatus.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    public interface CardMapper {

    @Mapping(target = "cardType", source = "cardType", qualifiedByName = "stringToCardType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardValid", ignore = true)
    Card toEntity(CardInsertDTO cardInsertDTO);

    CardDTO toDto(Card card);

    @Named("stringToCardType")
    static Card.CardType stringToCardType(String cardType) {
        return Card.CardType.valueOf(cardType.toUpperCase());
    }

    @Named("cardTypeToString")
    static String cardTypeToString(Card.CardType cardType) {
        return cardType.name();
    }
}
