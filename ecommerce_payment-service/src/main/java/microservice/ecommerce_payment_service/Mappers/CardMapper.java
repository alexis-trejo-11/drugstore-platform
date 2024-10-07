package microservice.ecommerce_payment_service.Mappers;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import microservice.ecommerce_payment_service.Model.Card;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CardMapper {

    @Mapping(target = "cardType", source = "cardInsertDTO.cardType", qualifiedByName = "stringToCardType")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cardValid", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientId", source = "clientId")
    Card insertDtoToEntity(CardInsertDTO cardInsertDTO, Long clientId);

    @Mapping(target = "cardType", source = "cardInsertDTO.cardType", qualifiedByName = "stringToCardType")
    @Mapping(target = "id", source = "cardId")
    @Mapping(target = "cardValid", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "clientId", source = "clientId")
    Card updateDtoToEntity(CardInsertDTO cardInsertDTO, Long clientId, Long cardId);

    CardDTO entityToDto(Card card);

    @Named("stringToCardType")
    static Card.CardType stringToCardType(String cardType) {
        return Card.CardType.valueOf(cardType.toUpperCase());
    }


}
