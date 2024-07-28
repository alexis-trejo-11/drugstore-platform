package microservice.ecommerce_payment_service.Automappers;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import microservice.ecommerce_payment_service.Model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    @Mapping(target = "cardType", source = "cardInsertDTO.cardType", qualifiedByName = "stringToCardType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardValid", ignore = true)
    Card toEntity(CardInsertDTO cardInsertDTO);

    CardDTO toDto(Card card);

    @Named("stringToCardType")
    static Card.CardType stringToCardType(String cardType) {
        return Card.CardType.valueOf(cardType.toUpperCase());
    }


}
