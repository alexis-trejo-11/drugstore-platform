package microservice.auth_service.app.auth.core.application.command;

public record RequestNotificationCommand(String userId, String type) {

		public RequestNotificationCommand {
				if (userId == null || userId.isBlank()) {
						throw new IllegalArgumentException("User ID cannot be null or blank");
				}
				if (type == null || type.isBlank()) {
						throw new IllegalArgumentException("Notification type cannot be null or blank");
				}
		}
}
