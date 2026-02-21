package io.github.alexisTrejo11.drugstore.users.profile.core.application.dto;

import java.time.LocalDate;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.Gender;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;

public record CreateProfileCommand(
    FullName fullName,
    LocalDate dateOfBirth,
    PhoneNumber phoneNumber,
    String bio,
    String profilePictureUrl,
    Gender gender) {
}
