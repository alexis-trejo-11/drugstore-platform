package user_service.modules.profile.core.domain.model.entities;

import lombok.Builder;
import user_service.modules.profile.core.domain.model.valueobjects.PersonalData;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileId;
import user_service.modules.profile.core.domain.model.valueobjects.ProfileTimestamps;
import user_service.modules.users.core.domain.models.valueobjects.UserId;

@Builder
public record ReconstructProfileParams(
    ProfileId id,
    UserId userId,
    PersonalData personalData,
    String bio,
    String avatarUrl,
    String coverPic,
    ProfileTimestamps timestamps) {
}