package microservice.users.core.domain.models.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("male"),
    FEMALE("female"),
    NON_BINARY("non-binary"),
    OTHER("other");

    private final String value;

    Gender(String value) {
        this.value = value;
    }
}
