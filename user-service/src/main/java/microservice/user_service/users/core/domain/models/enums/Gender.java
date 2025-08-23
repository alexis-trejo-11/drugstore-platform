package microservice.user_service.users.core.domain.models.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    PREFER_NOT_SAY("prefer_not_to_say"),
    OTHER("other");

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
