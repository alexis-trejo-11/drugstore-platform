package microservice.auth_service.app.auth.core.ports.output;

import microservice.auth_service.app.auth.core.domain.event.notification.EmailVerificationEvent;
import microservice.auth_service.app.auth.core.domain.event.notification.TwoFactorCodeEvent;
import microservice.auth_service.app.auth.core.domain.event.notification.WelcomeEmailEvent;

public interface NotificationEventPublisher {

  boolean publishEmailVerification(EmailVerificationEvent event);

  boolean publishTwoFactorCode(TwoFactorCodeEvent event);

  boolean publishWelcomeEmail(WelcomeEmailEvent event);
}
