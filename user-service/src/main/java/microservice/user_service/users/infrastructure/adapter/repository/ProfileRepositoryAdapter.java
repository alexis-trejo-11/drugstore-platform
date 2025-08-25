package microservice.user_service.users.infrastructure.adapter.repository;

import java.util.UUID;

import microservice.user_service.users.infrastructure.adapter.persistence.mappers.ModelMapper;
import microservice.user_service.users.infrastructure.adapter.persistence.models.ProfileModel;

import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;
import microservice.user_service.users.core.domain.models.entities.Profile;
import microservice.user_service.users.core.ports.output.ProfileRepository;
import microservice.user_service.users.infrastructure.adapter.persistence.jpa.ProfileJpaRepository;

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
    public Profile findByUserId(UUID userId) {
        ProfileModel profileModel = profileJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        return modelMapper.toEntity(profileModel);
    }

    @Override
    public void deleteByUserId(UUID userId) {
        ProfileModel profileModel = profileJpaRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profileJpaRepository.delete(profileModel);
    }

}
