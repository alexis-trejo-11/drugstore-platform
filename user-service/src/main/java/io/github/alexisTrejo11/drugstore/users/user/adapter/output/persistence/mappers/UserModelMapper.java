package io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.mappers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.users.user.adapter.output.persistence.models.UserModel;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.ReconstructUserParams;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.UserId;


@Component
@RequiredArgsConstructor
public class UserModelMapper {
	public UserModel fromEntity(User entity) {
		if (entity == null) {
			return null;
		}
		UserModel model = new UserModel();
		model.setId(entity.getId() != null ? entity.getId().value() : null);
		model.setEmail(entity.getEmail() != null ? entity.getEmail().value() : null);
		model.setPhoneNumber(entity.getPhoneNumber() != null ? entity.getPhoneNumber().value() : null);
		model.setHashedPassword(entity.getHashedPassword());
		model.setStatus(entity.getStatus());
		model.setRole(entity.getRole());

		// Extract two-factor configuration
		if (entity.getTwoFactorConfig() != null) {
			model.setTwoFactorId(entity.getTwoFactorConfig().twoFactorId());
		}

		// Extract timestamps
		if (entity.getTimestamps() != null) {
			model.setLastLoginAt(entity.getTimestamps().lastLoginAt());
			model.setCreatedAt(entity.getTimestamps().createdAt());
			model.setUpdatedAt(entity.getTimestamps().updatedAt());
		}

		return model;
	}


	public User toEntity(UserModel model) {
		if (model == null) {
			return null;
		}

		// Build the full name from profile if available
		FullName fullName = null;
		if (model.getProfile() != null) {
			String firstName = model.getProfile().getFirstName();
			String lastName = model.getProfile().getLastName();
			if (firstName != null || lastName != null) {
				fullName = new FullName(firstName != null ? firstName : "", lastName != null ? lastName : "");
			}
		}

		// Use the builder pattern for ReconstructUserParams
		ReconstructUserParams params = ReconstructUserParams.builder()
				.id(model.getId() != null ? new UserId(model.getId()) : null)
				.email(model.getEmail() != null ? new Email(model.getEmail()) : null)
				.fullName(fullName)
				.phoneNumber(model.getPhoneNumber() != null ? new PhoneNumber(model.getPhoneNumber()) : null)
				.hashedPassword(model.getHashedPassword())
				.role(model.getRole())
				.twoFactorEnabled(model.getTwoFactorId() != null && !model.getTwoFactorId().isEmpty())
				.twoFactorId(model.getTwoFactorId())
				.status(model.getStatus())
				.lastLoginAt(model.getLastLoginAt())
				.createdAt(model.getCreatedAt())
				.updatedAt(model.getUpdatedAt())
				.build();

		return User.reconstructUser(params);
	}

	public List<UserModel> fromEntities(List<User> entities) {
		if (entities == null || entities.isEmpty()) {
			return List.of();
		}
		return entities.stream()
				.map(this::fromEntity)
				.toList();
	}

	public List<User> toEntities(List<UserModel> models) {
		if (models == null || models.isEmpty()) {
			return List.of();
		}
		return models.stream()
				.map(this::toEntity)
				.toList();
	}

	public Page<User> toDomainPage(Page<UserModel> modelPage) {
		if (modelPage == null) {
			return null;
		}
		return modelPage.map(this::toEntity);
	}
}
