package user_service.modules.profile.infrastructure.persistence.adapter;

import java.util.Optional;
import java.util.UUID;

import user_service.modules.users.infrastructure.adapter.persistence.mappers.ModelMapper;
import user_service.modules.users.infrastructure.adapter.persistence.models.ProfileModel;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import user_service.modules.profile.core.ports.output.ProfileRepository;
import user_service.modules.profile.core.domain.model.Profile;
import user_service.modules.users.infrastructure.adapter.persistence.jpa.ProfileJpaRepository;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {
    private final ProfileJpaRepository profileJpaRepository;
    private final ModelMapper<Profile, ProfileModel> modelMapper;

    @Override
    public Profile save(Profile profile) {
        ProfileModel profileModel = modelMapper.fromEntity(profile);
        ProfileModel savedProfileModel = profileJpaRepository.save(profileModel);
        return modelMapper.toEntity(savedProfileModel);
    }

    @Override
    public Optional<Profile> findByUserId(UUID userId) {
        return profileJpaRepository.findByUserId(userId).map(modelMapper::toEntity);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        ProfileModel profileModel = profileJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profileJpaRepository.delete(profileModel);
    }

}
