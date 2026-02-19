package io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.entities;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects.PersonalData;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects.ProfileId;
import io.github.alexisTrejo11.drugstore.users.app.profile.core.domain.model.valueobjects.ProfileTimestamps;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;

@Builder
public record ReconstructProfileParams(
    ProfileId id,
    UserId userId,
    PersonalData personalData,
    String bio,
    String profilePictureUrl,
    ProfileTimestamps timestamps) {
}